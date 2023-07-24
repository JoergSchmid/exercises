package de.exercises.unitconverter.lengths;

public class Kilometer extends Meter {
    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*1000;
    }
}
