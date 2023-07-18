package de.exercises.unitconverter.lengths;

public class Meter extends LengthUnit {
    public static final String name = "meter";

    @Override
    public double getConversionFactorToMeter() {
        return 1.0;
    }
}
