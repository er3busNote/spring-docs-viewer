package com.docs.viewer.global.common.utils;

import java.util.function.Function;

public class ExceptionUtil {

    public static <T, R> Function<T, R> wrapFunction(CheckedFunction<T, R> function) {
        return input -> {
            try {
                return function.apply(input);
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred while processing input: " + input, e);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
