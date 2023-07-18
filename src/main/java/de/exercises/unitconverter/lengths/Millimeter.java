package de.exercises.unitconverter.lengths;

public class Millimeter extends Meter {
    public static final String name = "millimeter";

    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*0.001;
    }
}
