package de.exercises.unitconverter.lengths;

public class Mile extends Foot {
    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*5280;
    }
}
