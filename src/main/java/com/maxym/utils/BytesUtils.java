package com.maxym.utils;

import java.util.function.IntFunction;

public final class BytesUtils {

    private BytesUtils() {
        // this class cannot be instantiated
    }

    /**
     *
     * @param bytes an array of {@code bytes} to be represented
     * @return a binary representation of the given {@code byte} array
     */
    public static String toBinary(byte[] bytes) {
        return toNumeralSystem(bytes, Integer::toBinaryString);
    }

    /**
     *
     * @param bytes an array of {@code bytes} to be represented
     * @return a hexadecimal representation of the given {@code byte} array
     */
    public static String toHex(byte[] bytes) {
        return toNumeralSystem(bytes, Integer::toHexString);
    }

    private static String toNumeralSystem(byte[] bytes, IntFunction<String> converter) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(converter.apply((b & 0xFF) + 0x100).substring(1));
        }

        return stringBuilder.toString();
    }
}
