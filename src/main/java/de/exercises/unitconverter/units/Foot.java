package de.exercises.unitconverter.units;

public class Foot extends LengthUnit {
    public static final String name = "foot";

    public double toMeter(double value) {
        return value / 3.280839895;
    }

    public double fromMeter(double value) {
        return value * 3.280839895;
    }
}
