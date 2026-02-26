package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for common operations including JSON serialization,
 * map operations, and FEEL expression evaluation.
 *
 * @since 1.0.0
 */
public final class Util {

    private static final String FALSE_STRING = "false";
    private static final String TRUE_STRING = "true";
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Util() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates and configures an ObjectMapper for JSON operations.
     *
     * @return configured ObjectMapper instance
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    /**
     * Sets a value in the map with key and value as strings.
     * No-op if any parameter is null.
     *
     * @param map the target map
     * @param key the key object to convert to string
     * @param value the value object to convert to string
     */
    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) {
            return;
        }

        String keyStr = key.toString();
        String valueStr = value.toString();
        map.put(keyStr, valueStr);
    }

    /**
     * Sets a value in the map with key as string and value as object.
     * No-op if any parameter is null.
     *
     * @param map the target map
     * @param key the key object to convert to string
     * @param value the value object
     */
    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) {
            return;
        }

        String keyStr = key.toString();
        map.put(keyStr, value);
    }

    /**
     * Converts a map to a pretty-printed JSON string.
     *
     * @param variables the map to convert
     * @return the JSON string representation
     * @throws JsonProcessingException if JSON serialization fails
     * @throws NullPointerException if variables is null
     */
    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        Objects.requireNonNull(variables, "variables must not be null");
        return OBJECT_MAPPER.writeValueAsString(variables);
    }

    /**
     * Parses a boolean value from a map by key.
     * Returns "false" if key not found or parsing fails.
     *
     * @param map the map to search
     * @param key the key to look up
     * @return "true" or "false" as a string
     */
    public static String parseBoolean(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return FALSE_STRING;
        }

        try {
            Object value = map.get(key);
            if (value == null) {
                return FALSE_STRING;
            }

            if (value instanceof Boolean) {
                return value.toString();
            }

            String stringValue = value.toString();
            return stringValue.equalsIgnoreCase(TRUE_STRING) ? TRUE_STRING : FALSE_STRING;
        } catch (Exception e) {
            return FALSE_STRING;
        }
    }

    /**
     * Gets a string value from a map by key.
     * Returns empty string if key not found or value is null.
     *
     * @param map the map to search
     * @param key the key to look up
     * @return the string value or empty string
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return "";
        }

        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /**
     * Gets a map value from a map by key.
     * Returns null if key not found or value is not a Map.
     *
     * @param map the map to search
     * @param key the key to look up
     * @return the map value or null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object value = map.get(key);
        return (value instanceof Map) ? (Map<String, Object>) value : null;
    }

    /**
     * Gets a list value from a map by key.
     * Returns null if key not found or value is not a List.
     *
     * @param map the map to search
     * @param key the key to look up
     * @return the list value or null
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getListValue(Map<String, Object> map, String key) {
        if (map == null || key == null) {
            return null;
        }

        Object value = map.get(key);
        return (value instanceof List) ? (List<Object>) value : null;
    }

    /**
     * Evaluates a FEEL expression using the Camunda FEEL engine.
     * Returns null if the expression evaluation fails.
     *
     * @param expression the FEEL expression to evaluate
     * @param variables the variables context for the expression
     * @return the evaluation result as a JsonNode, or null if evaluation fails
     * @throws JsonProcessingException if JSON serialization fails
     * @throws NullPointerException if expression or variables is null
     */
    public static JsonNode evaluateFeel(String expression, Map<String, Object> variables) throws JsonProcessingException {
        Objects.requireNonNull(expression, "expression must not be null");
        Objects.requireNonNull(variables, "variables must not be null");

        FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var evaluationResult = feelEngineApi.evaluateExpression(expression, variables);

        if (evaluationResult == null || evaluationResult.isFailure()) {
            return null;
        }

        return OBJECT_MAPPER.readTree(
                OBJECT_MAPPER.writeValueAsString(evaluationResult.result())
        );
    }
}
