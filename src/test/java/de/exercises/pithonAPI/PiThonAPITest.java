package de.exercises.pithonAPI;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

public class PiThonAPITest {
    // Define the 3 available irrational numbers up to 20 digits (after ".")
    private final Map<String, String> nums = Map.of(
            "pi", "3.14159265358979323846",
            "e", "2.71828182845904523536",
            "sqrt2", "1.41421356237309504880"
    );

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 10, 20})
    public void testGetDigits(int amount) {
        for(Map.Entry<String, String> num : nums.entrySet()) {
            String numberName = num.getKey();
            String expected = num.getValue().substring(0, amount + 2);

            // 0 digits is a special case, since we have to remove the "."
            if(amount == 0)
                expected = expected.substring(0,1);

            assertEquals(expected, PiThonAPI.getDigits(numberName, amount));
        }
    }
}
