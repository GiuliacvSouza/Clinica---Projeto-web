package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidTelefonePortuguesValidator
        implements ConstraintValidator<ValidTelefonePortugues, String> {

    /**
     * Após remover prefixo +351 / 00351 e espaços/traços:
     *   - Telemóvel: 9[1236]XXXXXXX  (91, 92, 93, 96)
     *   - Fixo:      2XXXXXXXX
     */
    private static final Pattern NUMERO = Pattern.compile("^[92]\\d{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // campo opcional

        // Normalizar: remover espaços, traços, parênteses
        String limpo = value.trim().replaceAll("[\\s\\-().]+", "");

        // Remover prefixo internacional
        if (limpo.startsWith("+351")) {
            limpo = limpo.substring(4);
        } else if (limpo.startsWith("00351")) {
            limpo = limpo.substring(5);
        }

        return NUMERO.matcher(limpo).matches();
    }
}
