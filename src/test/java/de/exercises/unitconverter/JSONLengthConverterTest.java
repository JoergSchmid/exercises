package de.exercises.unitconverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

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
    @CsvFileSource(resources = "/lengthUnitTests.csv", numLinesToSkip = 1)
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

        assert output.size() == 2;
        assert result == expected;
    }

    @Test
    public void testMeterToFoot() throws IOException, ParseException {
        JSONObject input = new JSONObject();
        input.put("from", "meter");
        input.put("to", "foot");
        input.put("value", 1);
        writeToInputFile(input);

        new JSONLengthConverter();

        // Round the result
        double result = Math.round((double) readFromOutputFile().get("foot"));

        assert result == 3.0;

        input.clear();
        input.put("from", "meter");
        input.put("to", "foot");
        input.put("value", -100.5);
        writeToInputFile(input);

        new JSONLengthConverter();

        result = Math.round((double) readFromOutputFile().get("foot"));

        assert result == -330.0;
    }
}
