package de.exercises.unitconverter.lengths;

public abstract class LengthUnit {
    public String name;
    public abstract double getConversionFactorToMeter();
    public double toMeter(double value) {
        return value * getConversionFactorToMeter();
    }
    public double fromMeter(double value) {
        return value / getConversionFactorToMeter();
    }
}
