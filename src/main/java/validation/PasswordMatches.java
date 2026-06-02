package validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de nível de classe — verifica que password == confirmPassword.
 * Deve ser aplicada no DTO/Form, não num campo individual.
 */
@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {

    String message() default "As palavras-passe não coincidem.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
