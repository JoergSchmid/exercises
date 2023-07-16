package de.exercises.unitconverter.lengths;

public abstract class LengthUnit {
    public String name;
    public abstract double toMeter(double value);
    public abstract double fromMeter(double value);
}
