package de.exercises.unitconverter;

import static org.junit.jupiter.api.Assertions.*;

import de.exercises.unitconverter.lengths.LengthUnitFactory;
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
    private Path inputPath;
    private Path outputPath;

    @BeforeEach
    public void createTestFolder() throws IOException {
        temporaryFolder.create();
        temporaryFolder.newFolder("conversion_test");
        inputPath = Path.of(temporaryFolder.getRoot().toPath() + "\\input.json");
        outputPath = Path.of(temporaryFolder.getRoot().toPath() + "\\result.json");
    }

    private void writeToInputFile(JSONObject input) throws IOException {
        Writer writer = new FileWriter(inputPath.toString());
        writer.write(input.toString());
        writer.close();
    }

    private JSONObject readFromOutputFile() throws IOException {
        String outputFile = new String(Files.readAllBytes(outputPath));
        return new JSONObject(outputFile);
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

        JSONLengthConverter.convertFromFile(inputPath);

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

        JSONLengthConverter.convertFromFile(inputPath);

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
    public void testIncompleteInput() throws IOException {
        JSONObject input = new JSONObject();
        writeToInputFile(input);

        JSONLengthConverter.convertFromFile(inputPath);

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.put("from", "meter");
        writeToInputFile(input);

        JSONLengthConverter.convertFromFile(inputPath);

        result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.clear();
        input.put("value", 1);
        writeToInputFile(input);

        JSONLengthConverter.convertFromFile(inputPath);

        result = readFromOutputFile();
        assertTrue(result.has("error"));
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
        JSONLengthConverter.convertFromFile(inputPath);

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));
    }

    @Test
    public void testInvalidUnitName() throws IOException {
        JSONObject input = new JSONObject();
        input.put("from", "invalid");
        input.put("value", 1);
        writeToInputFile(input);

        JSONLengthConverter.convertFromFile(inputPath);

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));
    }
}
