package de.exercises.unitconverter.lengths;

public class Inch extends Foot {
    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*(1.0/12);
    }
}
