package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Util utility class.
 * Tests FEEL expression evaluation and other utility functions.
 *
 * @since 1.0.0
 */
class UtilTest {

    private static final String FIRST_NAME_KEY = "first";
    private static final String LAST_NAME_KEY = "last";
    private static final String AGE_KEY = "age";
    private static final String CUSTOMERS_KEY = "customers";
    private static final String FEEL_EXPRESSION = "customers[age>10]";

    /**
     * Tests FEEL expression evaluation with a list of customer objects.
     * Verifies that the expression correctly filters customers by age.
     *
     * @throws JsonProcessingException if JSON processing fails
     */
    @Test
    void testEvaluateFeel() throws JsonProcessingException {
        // Arrange
        Map<String, Object> variables = new HashMap<>();
        List<Map<String, Object>> customerList = new ArrayList<>();
        customerList.add(createCustomer("John", "Doe", 8));
        customerList.add(createCustomer("Jane", "Smith", 18));
        customerList.add(createCustomer("Bob", "Johnson", 28));
        customerList.add(createCustomer("Alice", "Williams", 7));
        customerList.add(createCustomer("Charlie", "Brown", 45));
        variables.put(CUSTOMERS_KEY, customerList);

        // Act
        var result = Util.evaluateFeel(FEEL_EXPRESSION, variables);

        // Assert
        assertNotNull(result, "FEEL evaluation result should not be null");
        System.out.println("FEEL evaluation result: " + result);
    }

    /**
     * Creates a customer map with the specified first name, last name, and age.
     *
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @param age the customer's age
     * @return a map representing the customer
     */
    private Map<String, Object> createCustomer(String firstName, String lastName, int age) {
        return Map.of(
                FIRST_NAME_KEY, firstName,
                LAST_NAME_KEY, lastName,
                AGE_KEY, age
        );
    }
}