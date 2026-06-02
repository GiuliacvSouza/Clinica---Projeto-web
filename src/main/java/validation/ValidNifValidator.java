package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Valida NIF português:
 * - Exactamente 9 dígitos numéricos.
 * - Primeiro dígito pertence ao conjunto {1, 2, 3, 5, 6, 8, 9}.
 * - Dígito de controlo calculado pelo algoritmo oficial da AT.
 */
public class ValidNifValidator implements ConstraintValidator<ValidNif, String> {

    private static final int[] PRIMEIROS_VALIDOS = {1, 2, 3, 5, 6, 8, 9};

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // campo opcional

        String nif = value.trim();

        // Deve ter exactamente 9 dígitos
        if (!nif.matches("\\d{9}")) return false;

        // Primeiro dígito válido
        int primeiro = nif.charAt(0) - '0';
        boolean primeiroValido = false;
        for (int p : PRIMEIROS_VALIDOS) {
            if (p == primeiro) { primeiroValido = true; break; }
        }
        if (!primeiroValido) return false;

        // Calcular dígito de controlo
        int soma = 0;
        for (int i = 0; i < 8; i++) {
            soma += (nif.charAt(i) - '0') * (9 - i);
        }
        int resto = soma % 11;
        int digitoControlo = resto < 2 ? 0 : 11 - resto;

        return digitoControlo == (nif.charAt(8) - '0');
    }
}
