package de.exercises.unitconverter;

import de.exercises.unitconverter.units.LengthUnit;
import de.exercises.unitconverter.units.LengthUnitFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

public class JSONLength {
    private String fromUnitName;
    private double fromValue;
    private String toUnitName;
    private double toValue;

    public static void main(String[] args) {
        new JSONLength();
    }

    public JSONLength() {
        JSONParser parser = new JSONParser();

        try {
            String baseURL = "C:\\gitroot\\Exercises\\unitConversionIO\\";

            Reader reader = new FileReader(baseURL + "input.json");

            JSONObject input = (JSONObject) parser.parse(reader);
            fromUnitName = (String) input.get("from");
            toUnitName = (String) input.get("to");
            Object value = input.get("value"); // Value might be of type Long or Double. Convert to double.
            fromValue = value instanceof Long ? ((Long) value).doubleValue() : (double) value;

            calculate();

            JSONObject output = new JSONObject();
            output.put(fromUnitName, fromValue);
            output.put(toUnitName, toValue);

            Writer writer = new FileWriter(baseURL + "result.json");
            writer.write(output.toJSONString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculate() {
        LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
        LengthUnit toUnit = LengthUnitFactory.getClass(toUnitName);
        if(fromUnit == null || toUnit == null) {
            toValue = 0;
            return;
        }
        toValue = toUnit.fromMeter(fromUnit.toMeter(fromValue));
    }
}
