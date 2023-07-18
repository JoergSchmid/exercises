package de.exercises.unitconverter.lengths;

public class Millimeter extends Meter {
    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*0.001;
    }
}
