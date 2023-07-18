package de.exercises.unitconverter.lengths;

public class Kilometer extends Meter {
    public static final String name = "kilometer";

    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*1000;
    }
}
