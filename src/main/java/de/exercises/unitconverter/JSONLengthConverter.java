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

public class JSONLengthConverter {
    private JSONObject input;
    private Double fromValue;
    private String fromUnit;
    private String toUnit;
    private final Path inputPath;
    private final Path outputPath;
    private String errorMessage;
    private final JSONObject result;

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
    }

    public void convert() {
        try {
            reset();

            if(checkFileExists()) {
                input = readFromInputFile();

                if(checkHasCorrectInputKeys()) {
                    fromUnit = (String) input.get("from");
                    fromValue = getDoubleFromObject(input.get("value"));
                    toUnit = input.has("to") ? (String) input.get("to") : null;

                    if(checkUnitsExist()) {
                        if(toUnit != null) {
                            singleConversion(toUnit);
                        } else {
                            completeConversion();
                        }
                        writeToOutputFile();
                        return;
                    }
                }
            }
            writeError();
        } catch (Exception e) {
            System.out.println("Unexpected error occurred:\n" + e.getMessage());
        }
    }

    private void reset() {
        fromValue = null;
        fromUnit = null;
        toUnit = null;
        errorMessage = null;
        result.clear();

    }

    private void singleConversion(String unit) {
        LengthUnit fromUnitClass = LengthUnitFactory.getClass(fromUnit);
        LengthUnit toUnitClass = LengthUnitFactory.getClass(unit);
        if(fromUnitClass == null || toUnitClass == null)
            return;
        result.put(fromUnit, fromValue);
        result.put(unit, toUnitClass.fromMeter(fromUnitClass.toMeter(fromValue)));
    }

    private void completeConversion() {
        for(String unit : LengthUnitFactory.lengthUnitMapping.keySet())
            singleConversion(unit);
    }

    private double getDoubleFromObject(Object number) {
        if(number instanceof Integer)
            return ((Integer) number).doubleValue();
        if(number instanceof BigDecimal)
            return ((BigDecimal) number).doubleValue();
        return (Double) number;
    }

    private JSONObject readFromInputFile() throws IOException {
        return new JSONObject(new String(Files.readAllBytes(inputPath)));
    }

    private void writeToOutputFile() throws IOException {
        Writer writer = new FileWriter(outputPath.toString());
        writer.write(result.toString());
        writer.close();
    }

    private boolean checkFileExists() {
        if(Files.exists(inputPath))
            return true;

        errorMessage = "File not found.";
        return false;
    }

    private boolean checkHasCorrectInputKeys() {
        if(input.has("from") && input.has("value"))
            return true;
        errorMessage = "Key(s) missing. Use 'from' and 'value' keys.";
        return false;
    }

    private boolean checkUnitsExist() {
        if(LengthUnitFactory.lengthUnitMapping.containsKey(fromUnit) &&
                (toUnit == null || LengthUnitFactory.lengthUnitMapping.containsKey(toUnit))) {
            return true;
        }
        errorMessage = "Unit not found.";
        return false;
    }

    private void writeError() throws IOException {
        result.clear();
        result.put("error", errorMessage);
        writeToOutputFile();
    }
}
