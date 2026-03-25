package com.cts.auth_service.utils.validators;

import java.util.Optional;
import java.util.function.Function;

public interface UserValidator<T> extends Function<T, Optional<String>> {
    default  UserValidator<T> and(UserValidator<T> other){
        return dto->{
            Optional<String> res = this.apply(dto);
            return  res.isPresent()?res : other.apply(dto);
        };
    }

    default void validateOrThrow(T dto) {
        this.apply(dto).ifPresent(message -> {
            throw new IllegalArgumentException(message);
        });
    }
}
