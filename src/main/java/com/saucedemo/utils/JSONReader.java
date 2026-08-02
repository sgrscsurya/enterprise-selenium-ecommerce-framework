package com.saucedemo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saucedemo.exceptions.FrameworkException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JSONReader provides reusable methods for reading JSON test-data files (Jackson-backed),
 * used for Data-Driven Testing alongside {@link ExcelUtility} and {@link CSVReader}.
 */
public class JSONReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JSONReader() {
        // Prevent instantiation
    }

    /** Parses the JSON file at the given path into a navigable {@link JsonNode} tree. */
    public static JsonNode readAsTree(String filePath) {
        try {
            return MAPPER.readTree(new File(filePath));
        } catch (IOException e) {
            throw new FrameworkException("Failed to read JSON file: " + filePath, e);
        }
    }

    /** Deserializes the JSON file at the given path into an instance of the given type. */
    public static <T> T readAs(String filePath, Class<T> type) {
        try {
            return MAPPER.readValue(new File(filePath), type);
        } catch (IOException e) {
            throw new FrameworkException("Failed to read JSON file: " + filePath + " as " + type.getSimpleName(), e);
        }
    }

    /** Deserializes a JSON array file into a List of Maps, convenient for @DataProvider-style consumption. */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> readAsListOfMaps(String filePath) {
        try {
            return MAPPER.readValue(new File(filePath), List.class);
        } catch (IOException e) {
            throw new FrameworkException("Failed to read JSON file: " + filePath + " as list of maps", e);
        }
    }

    /** Reads a single field's value from a JSON object file by key name. */
    public static String getValue(String filePath, String key) {
        JsonNode node = readAsTree(filePath).get(key);
        if (node == null) {
            throw new FrameworkException("Key '" + key + "' not found in JSON file: " + filePath);
        }
        return node.asText();
    }
}
