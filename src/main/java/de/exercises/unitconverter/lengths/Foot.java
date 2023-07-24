package de.exercises.unitconverter.lengths;

public class Foot extends LengthUnit {
    @Override
    public double getConversionFactorToMeter() {
        return 0.3048;
    }
}
