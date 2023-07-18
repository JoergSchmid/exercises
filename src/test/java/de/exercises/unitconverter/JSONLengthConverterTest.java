package de.exercises.unitconverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import java.io.*;

public class JSONLengthConverterTest {
    private void writeToInputFile(JSONObject input) throws IOException {
        Writer writer = new FileWriter("C:\\gitroot\\Exercises\\unitConversionIO\\input.json");
        writer.write(input.toJSONString());
        writer.close();
    }

    private JSONObject readFromOutputFile() throws IOException, ParseException {
        Reader reader = new FileReader("C:\\gitroot\\Exercises\\unitConversionIO\\result.json");
        return (JSONObject) new JSONParser().parse(reader);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/lengthUnitSingleConversionTests.csv", numLinesToSkip = 2)
    public void testSingleConversions(String fromUnit, String toUnit, double value, double expected) throws IOException, ParseException {
        JSONObject input = new JSONObject();
        input.put("from", fromUnit);
        input.put("to", toUnit);
        input.put("value", value);
        writeToInputFile(input);

        new JSONLengthConverter();

        // Round the result
        JSONObject output = readFromOutputFile();
        double result = Math.round((double) output.get(toUnit));

        assertTrue(output.size() <= 2); // Only "from" and "to", except when they are identical
        assertEquals(result, expected);
    }

    @Test
    public void testCompleteConversions() throws IOException, ParseException {
        JSONObject input = new JSONObject();
        input.put("from", "foot");
        input.put("value", 100000);
        writeToInputFile(input);

        new JSONLengthConverter();

        JSONObject result = readFromOutputFile();

        assertEquals(7, result.size()); // 6 units + "from"
        assertEquals(100000, (double) result.get("foot"));
        assertEquals(1200000, (double) result.get("inch"));
        assertEquals(19, (double) result.get("mile"));
        assertEquals(30480, (double) result.get("meter"));
        assertEquals(30480000, (double) result.get("millimeter"));
        assertEquals(30, (double) result.get("kilometer"));
    }

    @Test
    public void testIncompleteInput() throws IOException, ParseException {
        JSONObject input = new JSONObject();
        writeToInputFile(input);

        new JSONLengthConverter();

        JSONObject result = readFromOutputFile();
        assertTrue(result.containsKey("error"));

        input.put("from", "meter");

        new JSONLengthConverter();

        result = readFromOutputFile();
        assertTrue(result.containsKey("error"));

        input.clear();
        input.put("value", 1);

        new JSONLengthConverter();

        result = readFromOutputFile();
        assertTrue(result.containsKey("error"));
    }
}
