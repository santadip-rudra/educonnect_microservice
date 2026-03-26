package com.ctx.user_management_service.utils;

import java.util.Optional;
import java.util.function.Consumer;

public class UpdateUtil {
    /**
     * Maps a value from a source to a target if the source value is present.
     * @param source The value from the DTO (e.g., request.getEmail())
     * @param setter The setter method of the entity (e.g., student::setEmail)
     */
    public static <T> void setIfPresent(T source, Consumer<T> setter){
        Optional.ofNullable(source).ifPresent(setter);
    }
}
