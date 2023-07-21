package de.exercises.unitconverter;

import de.exercises.unitconverter.lengths.LengthUnit;
import de.exercises.unitconverter.lengths.LengthUnitFactory;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONLengthConverter {

    public static void main(String[] args) {
        convertFromFile(Path.of("unitConversionIO\\input.json"));
    }

    public static void convertFromFile(Path inputPath) {
        String outputUrl = inputPath.getParent() + "\\result.json";
        try {
            JSONObject input = readFromInputFile(inputPath.toString());

            if(!input.has("from") || !input.has("value")) {
                writeError("Key(s) missing. Use 'from' and 'value' keys.", outputUrl);
                return;
            }

            String fromUnitName = (String) input.get("from");
            double fromValue = getDoubleFromObject(input.get("value"));

            if(!LengthUnitFactory.lengthUnitMapping.containsKey(fromUnitName) ||
                    (input.has("to") && !LengthUnitFactory.lengthUnitMapping.containsKey((String) input.get("to")))) {
                writeError("Unit not found.", outputUrl);
                return;
            }

            JSONObject output = new JSONObject();

            if(input.has("to")) {
                singleConversion(output, fromUnitName, (String) input.get("to"), fromValue);
            } else {
                completeConversion(output, fromUnitName, fromValue);
            }

            writeToOutputFile(output, outputUrl);

        } catch (IOException e) {
            try {
                writeError(e.getMessage(), outputUrl);
            } catch (Exception f) {
                System.out.println("Error: Could not write file:\n" + f.getMessage());
            }
        }
    }

    private static void singleConversion(JSONObject output, String fromUnitName, String toUnitName, double value) {
        LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
        LengthUnit toUnit = LengthUnitFactory.getClass(toUnitName);
        if(fromUnit == null || toUnit == null)
            return;
        output.put(fromUnitName, value);
        output.put(toUnitName, toUnit.fromMeter(fromUnit.toMeter(value)));
    }

    private static void completeConversion(JSONObject output, String fromUnitName, double value) {
        for(String unit : LengthUnitFactory.lengthUnitMapping.keySet()) {
            LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
            LengthUnit toUnit = LengthUnitFactory.getClass(unit);
            if(fromUnit == null || toUnit == null)
                return;
            output.put(unit, toUnit.fromMeter(fromUnit.toMeter(value)));
        }
    }

    private static double getDoubleFromObject(Object number) {
        if(number instanceof Integer)
            return ((Integer) number).doubleValue();
        if(number instanceof BigDecimal)
            return ((BigDecimal) number).doubleValue();
        return (Double) number;
    }

    private static JSONObject readFromInputFile(String url) throws IOException {
        return new JSONObject(new String(Files.readAllBytes(Paths.get(url))));
    }

    private static void writeToOutputFile(JSONObject output, String url) throws IOException {
        Writer writer = new FileWriter(url);
        writer.write(output.toString());
        writer.close();
    }

    private static void writeError(String errorMessage, String outputUrl) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", errorMessage);
        writeToOutputFile(error, outputUrl);
    }
}
