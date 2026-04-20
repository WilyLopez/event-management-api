package com.playzone.pems.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DniValidator.DniConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DniValidator {

    String message() default "El DNI debe tener exactamente 8 dígitos numéricos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean requerido() default true;

    class DniConstraintValidator implements ConstraintValidator<DniValidator, String> {

        private boolean requerido;

        @Override
        public void initialize(DniValidator anotacion) {
            this.requerido = anotacion.requerido();
        }

        @Override
        public boolean isValid(String dni, ConstraintValidatorContext context) {
            if (dni == null || dni.isBlank()) {
                return !requerido;
            }
            return dni.matches("^\\d{8}$");
        }
    }
}