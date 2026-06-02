package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida número de telefone português (telemóvel ou fixo).
 * Campo opcional — null/blank passa sempre.
 */
@Documented
@Constraint(validatedBy = ValidTelefonePortuguesValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefonePortugues {

    String message() default "Introduza um número de telemóvel português válido (ex: 912 345 678).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
