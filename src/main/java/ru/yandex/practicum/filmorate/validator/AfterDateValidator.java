package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class AfterDateValidator implements ConstraintValidator<ru.yandex.practicum.filmorate.validator.AfterDate,
        LocalDate> {
    private int day;
    private int month;
    private int year;

    @Override
    public void initialize(ru.yandex.practicum.filmorate.validator.AfterDate constraintAnnotation) {
        day = constraintAnnotation.day();
        month = constraintAnnotation.month();
        year = constraintAnnotation.year();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value != null) {
            return value.isAfter(LocalDate.of(year, month, day));
        }
        return false;
    }
}