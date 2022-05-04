package com.starry.netty.protocol;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/17 19:09
 * @Description
 */
public interface Serializer {

    /**
     * 反序列化
     * @param clazz
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deSerialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T object);

}
