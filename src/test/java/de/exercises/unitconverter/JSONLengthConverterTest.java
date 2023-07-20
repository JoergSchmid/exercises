package de.exercises.unitconverter;

import static org.junit.jupiter.api.Assertions.*;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONLengthConverterTest {
    private final Path inputPath = Path.of( "C:\\gitroot\\Exercises\\unitConversionIO\\input.json");
    private final String outputURL = "C:\\gitroot\\Exercises\\unitConversionIO\\result.json";

    private void writeToInputFile(JSONObject input) throws IOException {
        Writer writer = new FileWriter(inputPath.toString());
        writer.write(input.toString());
        writer.close();
    }

    private JSONObject readFromOutputFile() throws IOException {
        String inputFile = new String(Files.readAllBytes(Paths.get(outputURL)));
        return new JSONObject(inputFile);
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
        Object number = output.get(toUnit);
        double result = Math.round(
                number instanceof Integer ? ((Integer) number).doubleValue() :
                        number instanceof BigDecimal ? ((BigDecimal) number).doubleValue() :
                                ((Double) number)
                );

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

        assertEquals(7, result.length()); // 6 units + "from"
        assertEquals(100000, (double) result.get("foot"));
        assertEquals(1200000, (double) result.get("inch"));
        assertEquals(19, (double) result.get("mile"));
        assertEquals(30480, (double) result.get("meter"));
        assertEquals(30480000, (double) result.get("millimeter"));
        assertEquals(30, (double) result.get("kilometer"));
    }

    @Test
    public void testIncompleteInput() throws IOException {
        JSONObject input = new JSONObject();
        writeToInputFile(input);

        JSONLengthConverter.convertFromFile(inputPath);

        JSONObject result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.put("from", "meter");

        JSONLengthConverter.convertFromFile(inputPath);

        result = readFromOutputFile();
        assertTrue(result.has("error"));

        input.clear();
        input.put("value", 1);

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
}
