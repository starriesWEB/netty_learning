package com.starry.netty.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/19 22:16
 * @Description
 */
public class SequenceIdGenerator {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(1);

    public static Integer nextId() {
        return ATOMIC_INTEGER.incrementAndGet();
    }
}
