package de.exercises.unitconverter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

import java.io.*;

public class JSONLengthConverterTest {
    private void writeToInputFile(JSONObject input) throws IOException {
        Writer writer = new FileWriter("C:\\gitroot\\Exercises\\unitConversionIO\\input.json");
        writer.write(input.toJSONString());
        writer.close();
    }

    private JSONObject readfromOutputFile() throws IOException, ParseException {
        Reader reader = new FileReader("C:\\gitroot\\Exercises\\unitConversionIO\\result.json");
        return (JSONObject) new JSONParser().parse(reader);
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
        double result = Math.round((double) readfromOutputFile().get("foot"));

        assert result == 3.0;

        input.clear();
        input.put("from", "meter");
        input.put("to", "foot");
        input.put("value", -100.5);
        writeToInputFile(input);

        new JSONLengthConverter();

        result = Math.round((double) readfromOutputFile().get("foot"));

        assert result == -330.0;
    }
}
