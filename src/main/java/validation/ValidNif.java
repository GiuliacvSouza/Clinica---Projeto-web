package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida NIF português (9 dígitos + dígito de controlo).
 * Campo opcional — null/blank passa sempre.
 */
@Documented
@Constraint(validatedBy = ValidNifValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNif {

    String message() default "Introduza um NIF válido.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
