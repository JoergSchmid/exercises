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
    private double fromValue;
    private String fromUnit;
    private String toUnit;
    private Path inputPath;
    private Path outputPath;

    public void main(String[] args) {
        JSONLengthConverter converter = new JSONLengthConverter(Path.of("unitConversionIO\\input.json"));
        convert();
    }

    public JSONLengthConverter(Path inputPath) {
        this.inputPath = inputPath;
        outputPath = Path.of(inputPath.getParent() + "\\result.json");
    }

    public void convert() {
        try {
            JSONObject input = readFromInputFile(inputPath);

            if(!input.has("from") || !input.has("value")) {
                writeError("Key(s) missing. Use 'from' and 'value' keys.", outputPath);
                return;
            }

            fromUnit = (String) input.get("from");
            fromValue = getDoubleFromObject(input.get("value"));

            if(!LengthUnitFactory.lengthUnitMapping.containsKey(fromUnit) ||
                    (input.has("to") && !LengthUnitFactory.lengthUnitMapping.containsKey((String) input.get("to")))) {
                writeError("Unit not found.", outputPath);
                return;
            }

            JSONObject output = new JSONObject();

            if(input.has("to")) {
                singleConversion(output, fromUnit, (String) input.get("to"), fromValue);
            } else {
                completeConversion(output, fromUnit, fromValue);
            }

            writeToOutputFile(output, outputPath);

        } catch (IOException e) {
            try {
                writeError(e.getMessage(), outputPath);
            } catch (Exception f) {
                System.out.println("Error: Could not write file:\n" + f.getMessage());
            }
        }
    }

    private void singleConversion(JSONObject output, String fromUnitName, String toUnitName, double value) {
        LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
        LengthUnit toUnit = LengthUnitFactory.getClass(toUnitName);
        if(fromUnit == null || toUnit == null)
            return;
        output.put(fromUnitName, value);
        output.put(toUnitName, toUnit.fromMeter(fromUnit.toMeter(value)));
    }

    private void completeConversion(JSONObject output, String fromUnitName, double value) {
        for(String unit : LengthUnitFactory.lengthUnitMapping.keySet()) {
            LengthUnit fromUnit = LengthUnitFactory.getClass(fromUnitName);
            LengthUnit toUnit = LengthUnitFactory.getClass(unit);
            if(fromUnit == null || toUnit == null)
                return;
            output.put(unit, toUnit.fromMeter(fromUnit.toMeter(value)));
        }
    }

    private double getDoubleFromObject(Object number) {
        if(number instanceof Integer)
            return ((Integer) number).doubleValue();
        if(number instanceof BigDecimal)
            return ((BigDecimal) number).doubleValue();
        return (Double) number;
    }

    private JSONObject readFromInputFile(Path path) throws IOException {
        return new JSONObject(new String(Files.readAllBytes(path)));
    }

    private void writeToOutputFile(JSONObject output, Path path) throws IOException {
        Writer writer = new FileWriter(path.toString());
        writer.write(output.toString());
        writer.close();
    }

    private void writeError(String errorMessage, Path path) throws IOException {
        JSONObject error = new JSONObject();
        error.put("error", errorMessage);
        writeToOutputFile(error, path);
    }
}
