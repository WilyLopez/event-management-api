package com.playzone.pems.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.util.Set;

@Documented
@Constraint(validatedBy = RucValidator.RucConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface RucValidator {

    String message() default "El RUC debe tener 11 dígitos y comenzar con 10 (persona natural) o 20 (persona jurídica).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean requerido() default false;

    class RucConstraintValidator implements ConstraintValidator<RucValidator, String> {

        private static final Set<String> PREFIJOS_VALIDOS = Set.of("10", "20");

        private boolean requerido;

        @Override
        public void initialize(RucValidator anotacion) {
            this.requerido = anotacion.requerido();
        }

        @Override
        public boolean isValid(String ruc, ConstraintValidatorContext context) {
            if (ruc == null || ruc.isBlank()) {
                return !requerido;
            }

            if (!ruc.matches("^\\d{11}$")) {
                return false;
            }

            String prefijo = ruc.substring(0, 2);
            return PREFIJOS_VALIDOS.contains(prefijo);
        }
    }
}