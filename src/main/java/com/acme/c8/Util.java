package com.acme.c8;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class Util {
    public static void setMapValue(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return;

        String k = key.toString();
        String v = value.toString();

        map.put(k, v);
    }

    public static Map<String, Object> setMapValueAsObject(Map<String, Object> map, Object key, Object value) {
        if (map == null || key == null || value == null) return Collections.emptyMap();
        Map<String, Object> map1 = new HashMap<>(map);
        String k = key.toString();
        Object v = value;

        map1.put(k, v);
        return map1;
    }
    public static String toPrettyJson(HashMap<String, Object> variables) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        String prettyJson = objectMapper.writeValueAsString(variables);
        return prettyJson;
    }
    public static String parseBoolean(Map<String, Object> m, String key) {
        try {
            if (m == null || key == null) {
                return Boolean.FALSE.toString();
            }
            boolean boolValue = Boolean.parseBoolean(String.valueOf(m.get(key)));
            return String.valueOf(boolValue);
        } catch (Exception e) {
            log.error("asdkajsdoias ", e);
            return null;
        }
    }
    public static String getStringValue(Map<String,Object> m, String key) {
        Object o=m.get(key);
        if (o!=null)
        {
            return o.toString();
        }
        else
        {
            return "";
        }
    }
    public static Map<String,Object> getMapValue(Map<String,Object> m, String key) {
        Map<String,Object> retval=null;
        Object o=m.get(key);
        if (o!=null && o instanceof Map)
        {
            retval = (Map<String, Object>) o;
        }
        return retval;
    }

    public static List<Object> getListValue(Map<String,Object> m, String key) {
        List<Object> retval=null;
        Object o=m.get(key);
        if (o!=null && o instanceof List)
        {
            retval = (List<Object>) o;
        }
        return retval;
    }

    public static JsonNode evaluateFeel(final String feel, final Map<String, Object> variables) throws JsonProcessingException {
        final FeelEngineApi feelEngineApi = FeelEngineBuilder.forJava().build();
        var result = feelEngineApi.evaluateExpression(feel, variables);

        if( result==null || result.isFailure()) {
            return null;
        }

        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(objectMapper.writeValueAsString(result.result()));
    }

}
