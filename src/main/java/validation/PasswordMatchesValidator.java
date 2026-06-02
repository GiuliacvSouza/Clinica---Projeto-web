package validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import model.dto.CadastroForm;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, CadastroForm> {

    @Override
    public boolean isValid(CadastroForm form, ConstraintValidatorContext context) {
        if (form == null) return true;

        String pw  = form.getPassword();
        String cpw = form.getConfirmPassword();

        if (pw == null || cpw == null) return true; // @NotBlank trata o caso vazio

        boolean match = pw.equals(cpw);
        if (!match) {
            // Associar o erro ao campo confirmPassword para o Thymeleaf o mostrar
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                   .addPropertyNode("confirmPassword")
                   .addConstraintViolation();
        }
        return match;
    }
}
