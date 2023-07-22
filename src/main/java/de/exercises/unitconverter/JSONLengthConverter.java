package de.exercises.unitconverter;

import de.exercises.unitconverter.lengths.LengthUnit;
import de.exercises.unitconverter.lengths.LengthUnitFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONLengthConverter {
    private final Path inputPath;
    private final Path outputPath;
    private String errorMessage;
    private final JSONObject result;
    private final JSONArray output;

    public static void main(String[] args) {
        Path path;
        if(args.length == 0)
            path = Path.of("unitConversionIO\\input.json");
        else
            path = Path.of(args[0]);
        JSONLengthConverter converter = new JSONLengthConverter(path);
        converter.convert();
    }

    public JSONLengthConverter(Path inputPath) {
        this.inputPath = inputPath;
        outputPath = Path.of(inputPath.getParent() + "\\result.json");
        result = new JSONObject();
        output = new JSONArray();
    }

    public void convert() {
        try {
            reset();

            if(checkFileExists()) {
                JSONArray input = readFromInputFile();

                if(input != null) {
                    convertJSONArray(input);
                } else writeErrorIntoOutputArray();
            } else writeErrorIntoOutputArray();
            
            writeToOutputFile();
        } catch (Exception e) {
            System.out.println("Unexpected error occurred:\n" + e.getMessage());
        }
    }

    private void convertJSONArray(JSONArray input) {
        JSONObject obj;
        for(int i = 0; i < input.length(); i++) {
            obj = input.getJSONObject(i);

            if(checkHasCorrectInputKeys(obj)) {
                String fromUnit = (String) obj.get("from");
                double fromValue = getDoubleFromObject(obj.get("value"));
                String toUnit = obj.has("to") ? (String) obj.get("to") : null;

                if(checkUnitsExist(fromUnit, toUnit)) {
                    if(toUnit != null) {
                        singleConversion(fromUnit, fromValue, toUnit);
                    } else {
                        completeConversion(fromUnit, fromValue);
                    }
                    writeIntoOutputArray(result);
                    continue;
                }
            }
            writeIntoOutputArray(new JSONObject().put("error", errorMessage));
        }
    }

    private void reset() {
        errorMessage = null;
        result.clear();

    }

    private void singleConversion(String fromUnit, double value, String toUnit) {
        LengthUnit fromUnitClass = LengthUnitFactory.getClass(fromUnit);
        LengthUnit toUnitClass = LengthUnitFactory.getClass(toUnit);
        if(fromUnitClass == null || toUnitClass == null)
            return;
        result.put(fromUnit, value);
        result.put(toUnit, toUnitClass.fromMeter(fromUnitClass.toMeter(value)));
    }

    private void completeConversion(String fromUnit, double value) {
        for(String unit : LengthUnitFactory.lengthUnitMapping.keySet())
            singleConversion(fromUnit, value, unit);
    }

    private double getDoubleFromObject(Object number) {
        if(number instanceof Integer)
            return ((Integer) number).doubleValue();
        if(number instanceof BigDecimal)
            return ((BigDecimal) number).doubleValue();
        return (Double) number;
    }

    private JSONArray readFromInputFile() throws IOException {
        String str = new String(Files.readAllBytes(inputPath));
        JSONArray arr;
        // Create array from json array or -object
        try {
            arr = new JSONArray(str);
        } catch (JSONException e) {
            try {
                arr = new JSONArray().put(new JSONObject(str));
            } catch (JSONException f) {
                errorMessage = "No valid JSON found.";
                return null;
            }
        }
        return arr;
    }

    private void writeToOutputFile() throws IOException {
        Writer writer = new FileWriter(outputPath.toString());
        writer.write(output.toString());
        writer.close();
    }

    private void writeIntoOutputArray(JSONObject object) {
        output.put(new JSONObject(object.toString()));
    }

    private void writeErrorIntoOutputArray() {
        output.put(new JSONObject().put("error", errorMessage));
    }

    private boolean checkFileExists() {
        if(Files.exists(inputPath))
            return true;

        errorMessage = "File not found.";
        return false;
    }

    private boolean checkHasCorrectInputKeys(JSONObject obj) {
        if(obj.has("from") && obj.has("value"))
            return true;
        errorMessage = "Key(s) missing. Use 'from' and 'value' keys.";
        return false;
    }

    private boolean checkUnitsExist(String fromUnit, String toUnit) {
        if(LengthUnitFactory.lengthUnitMapping.containsKey(fromUnit) &&
                (toUnit == null || LengthUnitFactory.lengthUnitMapping.containsKey(toUnit))) {
            return true;
        }
        errorMessage = "Unit not found.";
        return false;
    }
}
