package com.acme.c8.jobworker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.List;
import java.util.Map;

public class Util {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private Util() {}

    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value.toString());
    }

    public static void setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;
        map.put(key.toString(), value);
    }

    public static String toPrettyJson(Map<String, Object> variables) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(variables);
    }

    public static String parseBoolean(Map<String, Object> map, String key) {
        if (map == null || key == null) return "false";
        Object value = map.get(key);
        if (value instanceof Boolean) return value.toString();
        if (value != null && value.toString().equalsIgnoreCase("true")) return "true";
        return "false";
    }

    public static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    @SuppressWarnings("unchecked")
    public static List<Object> getListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof List ? (List<Object>) value : null;
    }

    public static JsonNode evaluateFeel(String expression, Map<String, Object> variables) throws JsonProcessingException {
        FeelEngineApi feelEngine = FeelEngineBuilder.forJava().build();
        var result = feelEngine.evaluateExpression(expression, variables);

        if (result == null || result.isFailure()) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(mapper.writeValueAsString(result.result()));
    }
}
