package com.sammwy.classserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A utility class that provides methods to flatten (deflate) and unflatten
 * (inflate) nested maps.
 * <p>
 * The deflate operation takes a map with nested maps and transforms it into a
 * single-level map
 * where keys from nested maps are prefixed with their parent key and a
 * separator.
 * <p>
 * The inflate operation reverses this process, taking a flat map and converting
 * it back into
 * a nested structure based on the key prefixes and separators.
 * 
 * @author MapProcessor
 * @version 1.0
 */
public class MapDeflate {

    /**
     * Flattens a nested map structure into a single-level map.
     * <p>
     * For example, if the input is:
     * 
     * <pre>
     * {
     *   "test": {
     *     "foo": "bar"
     *   }
     * }
     * </pre>
     * 
     * The output with separator "." would be:
     * 
     * <pre>
     * {
     *   "test.foo": "bar"
     * }
     * </pre>
     *
     * @param nestedMap The potentially nested map to flatten
     * @param separator The string to use as a separator between parent and child
     *                  keys
     * @return A new map with all nested structures flattened
     */
    public static Map<String, Object> deflate(Map<String, Object> nestedMap, String separator) {
        Map<String, Object> result = new HashMap<>();
        deflateRecursive(nestedMap, "", separator, result);
        return result;
    }

    /**
     * Helper method that recursively traverses the nested map structure.
     *
     * @param currentMap The current map being processed
     * @param prefix     The current key prefix (empty for the root map)
     * @param separator  The string separator to use between keys
     * @param result     The resulting flattened map that's being built
     */
    @SuppressWarnings("unchecked")
    private static void deflateRecursive(Map<String, Object> currentMap, String prefix,
            String separator, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : currentMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Determine the full key path
            String fullKey = prefix.isEmpty() ? key : prefix + separator + key;

            if (value instanceof Map) {
                // Recursively process nested maps
                deflateRecursive((Map<String, Object>) value, fullKey, separator, result);
            } else {
                // Add the leaf node to the result
                result.put(fullKey, value);
            }
        }
    }

    /**
     * Converts a flattened map back into a nested structure.
     * <p>
     * For example, if the input is:
     * 
     * <pre>
     * {
     *   "test.foo": "bar"
     * }
     * </pre>
     * 
     * The output with separator "." would be:
     * 
     * <pre>
     * {
     *   "test": {
     *     "foo": "bar"
     *   }
     * }
     * </pre>
     *
     * @param flatMap   The flattened map to convert back to a nested structure
     * @param separator The string that was used as a separator between parent and
     *                  child keys
     * @return A new map with the nested structure restored
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> inflate(Map<String, Object> flatMap, String separator) {
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : flatMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String[] keyParts = key.split(Pattern.quote(separator));

            // Start with the result map
            Map<String, Object> currentMap = result;

            // Navigate through the key parts (except the last one)
            for (int i = 0; i < keyParts.length - 1; i++) {
                String keyPart = keyParts[i];

                // If this key doesn't exist yet, create a new map for it
                if (!currentMap.containsKey(keyPart)) {
                    currentMap.put(keyPart, new HashMap<String, Object>());
                } else if (!(currentMap.get(keyPart) instanceof Map)) {
                    // If a non-map value already exists at this key, replace it with a map
                    currentMap.put(keyPart, new HashMap<String, Object>());
                }

                // Move to the next level
                currentMap = (Map<String, Object>) currentMap.get(keyPart);
            }

            // Add the value at the final level
            currentMap.put(keyParts[keyParts.length - 1], value);
        }

        return result;
    }

    /**
     * Flattens a nested map structure into a single-level map using "." as the
     * default separator.
     *
     * @param nestedMap The potentially nested map to flatten
     * @return A new map with all nested structures flattened
     */
    public static Map<String, Object> deflate(Map<String, Object> nestedMap) {
        return deflate(nestedMap, ".");
    }

    /**
     * Converts a flattened map back into a nested structure using "." as the
     * default separator.
     *
     * @param flatMap The flattened map to convert back to a nested structure
     * @return A new map with the nested structure restored
     */
    public static Map<String, Object> inflate(Map<String, Object> flatMap) {
        return inflate(flatMap, ".");
    }
}