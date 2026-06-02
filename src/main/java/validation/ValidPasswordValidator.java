package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {

    /**
     * 8+ chars, ≥1 maiúscula, ≥1 minúscula, ≥1 dígito, ≥1 símbolo.
     * Lookaheads independentes para cada requisito.
     */
    private static final Pattern FORTE = Pattern.compile(
        "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Campo em branco é tratado por @NotBlank separado
        if (value == null || value.isBlank()) return true;
        return FORTE.matcher(value).matches();
    }
}
