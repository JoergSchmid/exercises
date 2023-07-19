package de.exercises.unitconverter;

import de.exercises.unitconverter.lengths.LengthUnit;
import de.exercises.unitconverter.lengths.LengthUnitFactory;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSONLengthConverter {
    private String fromUnitName;
    private double fromValue;
    private String toUnitName;
    private double toValue;

    public static void main(String[] args) {
        new JSONLengthConverter();
    }

    public JSONLengthConverter() {
        try {
            String baseURL = "C:\\gitroot\\Exercises\\unitConversionIO\\";

            JSONObject input = new JSONObject(new String(Files.readAllBytes(Paths.get(baseURL + "input.json"))));
            fromUnitName = (String) input.get("from");
            toUnitName = (String) input.get("to");
            Object number = input.get("value"); // Value might be of type Long or Double. Convert to double.
            fromValue = number instanceof Integer ? ((Integer) number).doubleValue() :
                    number instanceof BigDecimal ? ((BigDecimal) number).doubleValue() :
                            ((Double) number);

            calculate();

            JSONObject output = new JSONObject();
            output.put(fromUnitName, fromValue);
            output.put(toUnitName, toValue);

            Writer writer = new FileWriter(baseURL + "result.json");
            writer.write(output.toString());
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
