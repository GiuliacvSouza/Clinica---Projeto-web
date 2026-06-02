package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Valida força da palavra-passe:
 * mínimo 8 caracteres, 1 maiúscula, 1 minúscula, 1 dígito, 1 símbolo.
 */
@Documented
@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "A palavra-passe deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula, número e símbolo.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
