package com.starry.netty.protocol;

import com.google.gson.*;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/17 19:13
 * @Description
 */
public enum SerializerAlgorithm implements Serializer {

    /**
     * Java 实现
     */
    Java {
        @Override
        public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
            try {
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
                Object object = in.readObject();
                return (T) object;
            } catch (Exception e) {
                throw new RuntimeException("反序列化失败", e);
            }
        }

        @Override
        public <T> byte[] serialize(T object) {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                new ObjectOutputStream(out).writeObject(object);
                return out.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException("序列化失败", e);
            }
        }
    },

    /**
     * Json 实现
     */
    Json {
        @Override
        @SneakyThrows
        public <T> T deSerialize(Class<T> clazz, byte[] bytes) {
            String s = new String(bytes, StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            return gson.fromJson(s, clazz);
            //return JSONUtil.toBean(s, clazz);
        }

        @Override
        public <T> byte[] serialize(T object) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
            //return JSONUtil.toJsonStr(object).getBytes(StandardCharsets.UTF_8);
        }
    }


}
class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Override
    public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        // 反序列化 json > object
        try {
            String s = jsonElement.getAsString();
            return Class.forName(s);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {
        // 序列化 object > json
        return new JsonPrimitive(aClass.getName());
    }
}
