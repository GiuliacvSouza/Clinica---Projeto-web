package bll;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo envio de e-mails transacionais.
 *
 * As credenciais SMTP são injectadas a partir de variáveis de ambiente
 * MAIL_USERNAME e MAIL_PASSWORD — nunca em código.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia o e-mail de recuperação de palavra-passe.
     *
     * @param destinatario E-mail do utilizador.
     * @param token        Token UUID gerado para este pedido.
     */
    public void enviarRecuperacaoSenha(String destinatario, String token) {
        String link = baseUrl + "/redefinir-senha?token=" + token;

        String corpo = """
                Olá,

                Recebemos um pedido de recuperação de palavra-passe para a sua conta na Clínica Dentária.

                Clique no link abaixo para redefinir a sua palavra-passe:

                %s

                Este link é válido por 30 minutos. Após esse prazo, terá de realizar um novo pedido.

                Se não solicitou esta recuperação, ignore este e-mail — a sua conta permanece segura.

                Cumprimentos,
                Equipa da Clínica Dentária
                """.formatted(link);

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(destinatario);
        mensagem.setSubject("Redefinição da palavra-passe - Clínica Dentária");
        mensagem.setText(corpo);

        try {
            mailSender.send(mensagem);
        } catch (MailException ex) {
            // Registar o erro sem revelar detalhes ao utilizador
            System.err.println("[EmailService] Falha ao enviar e-mail para " + destinatario + ": " + ex.getMessage());
            throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.");
        }
    }
}
