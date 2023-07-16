package de.exercises.unitconverter.lengths;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class LengthUnitFactory {

    // Add new Units here
    public static Map<String, Class<? extends LengthUnit>> lengthUnitMapping = Map.of(
            Meter.name, Meter.class,
            Foot.name, Foot.class
    );

    public static LengthUnit getClass(String name) {
        try {
            Class<? extends LengthUnit> unit = lengthUnitMapping.get(name);
            if (unit == null)
                return null;
            return unit.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
