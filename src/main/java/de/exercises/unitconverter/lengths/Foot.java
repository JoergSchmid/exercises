package de.exercises.unitconverter.lengths;

public class Foot extends LengthUnit {
    public static final String name = "foot";

    @Override
    public double getConversionFactorToMeter() {
        return 0.3048;
    }
}
