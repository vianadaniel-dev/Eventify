package com.example.eventify.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class NoPastEventsValidator implements ConstraintValidator<NoPastEvents, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        // Si es null, dejamos que @NotNull se encargue de reportar el campo vacío
        if (value == null) {
            return true;
        }
        // Rechaza cualquier fecha anterior al día de hoy
        return !value.isBefore(LocalDate.now());
    }
}