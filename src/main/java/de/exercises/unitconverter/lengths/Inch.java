package de.exercises.unitconverter.lengths;

public class Inch extends Foot {
    public static final String name = "inch";

    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*(1.0/12);
    }
}
