package com.example.eventify.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoPastEventsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoPastEvents {
    String message() default "No se permite registrar eventos en fechas pasadas (debe ser hoy o una fecha futura)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}