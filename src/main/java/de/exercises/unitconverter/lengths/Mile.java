package de.exercises.unitconverter.lengths;

public class Mile extends Foot {
    public static final String name = "mile";

    @Override
    public double getConversionFactorToMeter() {
        return super.getConversionFactorToMeter()*5280;
    }
}
