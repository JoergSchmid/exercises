package de.exercises.unitconverter;

import static org.junit.jupiter.api.Assertions.*;

import de.exercises.unitconverter.lengths.LengthUnitFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.rules.TemporaryFolder;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONLengthConverterTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private JSONLengthConverter jsonLengthConverter;
    private Path inputPath;
    private Path outputPath;

    @BeforeEach
    public void createTestFolder() throws IOException {
        temporaryFolder.create();
        temporaryFolder.newFolder("conversion_test");
        inputPath = Path.of(temporaryFolder.getRoot().toPath() + "\\input.json");
        outputPath = Path.of(temporaryFolder.getRoot().toPath() + "\\result.json");
        jsonLengthConverter = new JSONLengthConverter(inputPath);
    }

    private void writeToInputFile(JSONObject input) throws IOException {
        Writer writer = new FileWriter(inputPath.toString());
        writer.write(input.toString());
        writer.close();
    }

    private void writeToInputFile(JSONArray input) throws IOException {
        Writer writer = new FileWriter(inputPath.toString());
        writer.write(input.toString());
        writer.close();
    }

    private JSONObject readFromOutputFile() throws IOException {
        return readArrayFromOutputFile().getJSONObject(0);
    }

    private JSONArray readArrayFromOutputFile() throws IOException {
        String outputFile = new String(Files.readAllBytes(outputPath));
        return new JSONArray(outputFile);
    }

    private double getRoundedDoubleFromObject(Object number) {
        double num;
        if(number instanceof Integer)
            num = ((Integer) number).doubleValue();
        else if(number instanceof BigDecimal)
            num = ((BigDecimal) number).doubleValue();
        else
            num = (Double) number;
        return Math.round(num);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/lengthUnitSingleConversionTests.csv", numLinesToSkip = 2)
    public void testSingleConversions(String fromUnit, String toUnit, double value, double expected) throws IOException {
        JSONObject input = new JSONObject();
        input.put("from", fromUnit);
        input.put("to", toUnit);
        input.put("value", value);
        writeToInputFile(input);

        jsonLengthConverter.convert();

        // Round the result
        JSONObject output = readFromOutputFile();
        double result = getRoundedDoubleFromObject(output.get(toUnit));

        assertTrue(output.length() <= 2); // Only "from" and "to", except when they are identical
        assertEquals(result, expected);
    }

    @Test
    public void testCompleteConversions() throws IOException {
        JSONObject input = new JSONObject();
        input.put("from", "foot");
        input.put("value", 100000);
        writeToInputFile(input);

        jsonLengthConverter.convert();

        JSONObject result = readFromOutputFile();

        assertEquals(LengthUnitFactory.lengthUnitMapping.size(), result.length());
        assertEquals(100000, getRoundedDoubleFromObject(result.get("foot")));
        assertEquals(1200000, getRoundedDoubleFromObject(result.get("inch")));
        assertEquals(19, getRoundedDoubleFromObject(result.get("mile")));
        assertEquals(30480, getRoundedDoubleFromObject(result.get("meter")));
        assertEquals(30480000, getRoundedDoubleFromObject(result.get("millimeter")));
        assertEquals(30, getRoundedDoubleFromObject(result.get("kilometer")));
    }
    
    @Test
    public void testInputJsonArray() throws IOException {
        JSONObject[] input = new JSONObject[2];
        input[0].put("from", "foot");
        input[0].put("value", 10);
        input[1].put("from", "kilometer");
        input[1].put("to", "inch");
        input[1].put("value", -5.5);

        JSONArray array = new JSONArray();
        array.put(input);
        writeToInputFile(array);

        jsonLengthConverter.convert();

        JSONArray result = readArrayFromOutputFile();

        assertNotNull(result);
        assertEquals(120, getRoundedDoubleFromObject(result.getJSONObject(0).get("inch")));
        assertEquals(-216535, getRoundedDoubleFromObject(result.getJSONObject(0).get("inch")));
    }

    @Test
    public void testIncompleteInput() throws IOException {
        JSONObject input = new JSONObject();
        writeToInputFile(input);

        jsonLengthConverter.convert();

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.put("from", "meter");
        writeToInputFile(input);

        jsonLengthConverter.convert();

        result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.clear();
        input.put("value", 1);
        writeToInputFile(input);

        jsonLengthConverter.convert();

        result = readFromOutputFile();
        assertTrue(result.has("error"));
    }

    @Test
    public void testInputArrayWithOneFaultyInput() throws IOException {
        JSONObject[] input = new JSONObject[3];

        input[0].put("from", "meter");
        input[0].put("value", 5);

        // "value" is missing here
        input[1].put("from", "millimeter");
        input[1].put("to", "inch");

        input[2].put("from", "kilometer");
        input[2].put("to", "inch");
        input[2].put("value", -10);

        JSONArray array = new JSONArray();
        array.put(input);
        writeToInputFile(array);

        jsonLengthConverter.convert();

        JSONArray result = readArrayFromOutputFile();

        assertNotNull(result);
        // Correct inputs
        assertEquals(5000, getRoundedDoubleFromObject(result.getJSONObject(0).get("millimeter")));
        assertEquals(-393701, getRoundedDoubleFromObject(result.getJSONObject(2).get("inch")));
        // Faulty input
        assertTrue(result.getJSONObject(1).has("error"));
    }

    @Test
    public void testInputFileMissing() throws IOException {
        File file = new File(inputPath.toUri());
        if(file.exists())
            if(!file.delete()) {
                System.out.println("Error occurred while testing in testInputFileMissing()\n"
                                    + "Could not delete file.");
                return;
            }

        // Try to convert with file not present
        jsonLengthConverter.convert();

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));
    }

    @Test
    public void testInvalidUnitName() throws IOException {
        JSONObject input = new JSONObject();
        input.put("from", "invalid");
        input.put("value", 1);
        writeToInputFile(input);

        jsonLengthConverter.convert();

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));
    }
}
