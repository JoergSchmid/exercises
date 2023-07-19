package de.exercises.unitconverter;

import de.exercises.unitconverter.lengths.LengthUnit;
import de.exercises.unitconverter.lengths.LengthUnitFactory;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONLengthConverter {

    public static void main(String[] args) {
        convertFromFile(Path.of("C:\\gitroot\\Exercises\\unitConversionIO\\input.json"));
    }

    public static void convertFromFile(Path path) {
        try {
            JSONObject input = new JSONObject(new String(Files.readAllBytes(path)));
            String fromUnitName = (String) input.get("from");
            String toUnitName = (String) input.get("to");
            Object number = input.get("value"); // Value might be of type Long or Double. Convert to double.
            double fromValue = number instanceof Integer ? ((Integer) number).doubleValue() :
                    number instanceof BigDecimal ? ((BigDecimal) number).doubleValue() :
                            ((Double) number);

            double toValue = calculate(fromUnitName, toUnitName, fromValue);

            JSONObject output = new JSONObject();
            output.put(fromUnitName, fromValue);
            output.put(toUnitName, toValue);

            Writer writer = new FileWriter(path.getParent() + "\\result.json");
            writer.write(output.toString());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double calculate(String fromUnitName, String toUnitName, double value) {
        LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
        LengthUnit toUnit = LengthUnitFactory.getClass(toUnitName);
        if(fromUnit == null || toUnit == null) {
            return 0.0;
        }
        return toUnit.fromMeter(fromUnit.toMeter(value));
    }
}
