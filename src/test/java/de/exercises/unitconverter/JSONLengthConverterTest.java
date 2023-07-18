package de.exercises.unitconverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
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

        assert output.size() <= 2; // Only "from" and "to", except when they are identical
        assert result == expected;
    }

    @Test
    public void testCompleteConversions() throws IOException, ParseException {
        JSONObject input = new JSONObject();
        input.put("from", "foot");
        input.put("value", 100000);
        writeToInputFile(input);

        new JSONLengthConverter();

        JSONObject result = readFromOutputFile();

        assert result.size() == 7; // 6 units + "from"
        assert (double) result.get("foot") == 100000;
        assert (double) result.get("inch") == 1200000;
        assert (double) result.get("mile") == 19;
        assert (double) result.get("meter") == 30480;
        assert (double) result.get("millimeter") == 30480000;
        assert (double) result.get("kilometer") == 30;
    }
}
