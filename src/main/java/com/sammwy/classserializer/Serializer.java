package com.sammwy.classserializer;

/**
 * Interface for custom serializers.
 *
 * @param <T> The type of object the serializer handles.
 */
public interface Serializer<T> {
    /**
     * Serializes an object to a representation that can be stored in a map.
     *
     * @param object The object to serialize.
     * @return The serialized representation.
     */
    Object serialize(T object);

    /**
     * Deserializes an object from its map representation.
     *
     * @param serialized The serialized representation.
     * @return The deserialized object.
     */
    T deserialize(Object serialized);
}