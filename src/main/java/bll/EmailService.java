package bll;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável pelo envio de e-mails transacionais.
 *
 * As credenciais SMTP são injectadas a partir de variáveis de ambiente
 * MAIL_USERNAME e MAIL_PASSWORD.
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

        String assunto = "Redefinição da palavra-passe | Clínica Dentária";

        String corpoHtml = """
                <!DOCTYPE html>
                <html lang="pt-PT">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Redefinição da palavra-passe</title>
                </head>
                <body style="margin:0; padding:0; background-color:#f7faf9; font-family: Arial, Helvetica, sans-serif; color:#2c3434;">
                
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color:#f7faf9; padding:32px 16px;">
                        <tr>
                            <td align="center">
                
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="max-width:560px; background-color:#ffffff; border-radius:18px; overflow:hidden; box-shadow:0 8px 28px rgba(44,52,52,0.08);">
                
                                    <tr>
                                        <td style="padding:32px 32px 16px 32px; text-align:center;">
                                            <h2 style="margin:0; color:#2e6861; font-size:24px; font-weight:700;">
                                                Clínica Dentária
                                            </h2>
                                            <p style="margin:6px 0 0 0; color:#586160; font-size:14px;">
                                                Sorrisos saudáveis começam aqui
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding:16px 32px 8px 32px; text-align:center;">
                                            <h1 style="margin:0; color:#2c3434; font-size:28px; font-weight:700;">
                                                Redefinição da palavra-passe
                                            </h1>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding:16px 32px 0 32px;">
                                            <p style="margin:0 0 16px 0; font-size:16px; line-height:1.6; color:#2c3434;">
                                                Olá,
                                            </p>
                
                                            <p style="margin:0 0 16px 0; font-size:16px; line-height:1.6; color:#2c3434;">
                                                Recebemos um pedido para redefinir a palavra-passe da sua conta.
                                            </p>
                
                                            <p style="margin:0 0 24px 0; font-size:16px; line-height:1.6; color:#2c3434;">
                                                Para criar uma nova palavra-passe, clique no botão abaixo:
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td align="center" style="padding:8px 32px 28px 32px;">
                                            <a href="%s"
                                               style="display:inline-block; background-color:#2e6861; color:#ffffff; text-decoration:none; padding:14px 28px; border-radius:12px; font-size:16px; font-weight:700;">
                                                Redefinir palavra-passe
                                            </a>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding:0 32px 24px 32px;">
                                            <p style="margin:0 0 14px 0; font-size:14px; line-height:1.6; color:#586160;">
                                                Por motivos de segurança, este link é válido durante <strong>30 minutos</strong>.
                                                Após esse período, será necessário efetuar um novo pedido de recuperação.
                                            </p>
                
                                            <p style="margin:0; font-size:14px; line-height:1.6; color:#586160;">
                                                Caso não tenha solicitado esta alteração, ignore este e-mail.
                                                A sua palavra-passe atual manter-se-á inalterada e a sua conta continuará protegida.
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding:20px 32px 28px 32px; background-color:#f0f5f4;">
                                            <p style="margin:0 0 8px 0; font-size:13px; line-height:1.5; color:#586160;">
                                                Se o botão não funcionar, copie e cole este endereço no seu navegador:
                                            </p>
                
                                            <p style="margin:0; font-size:12px; line-height:1.5; color:#2e6861; word-break:break-all;">
                                                %s
                                            </p>
                                        </td>
                                    </tr>
                
                                    <tr>
                                        <td style="padding:24px 32px 32px 32px; text-align:center;">
                                            <p style="margin:0; font-size:13px; color:#586160;">
                                                Com os melhores cumprimentos,<br>
                                                <strong>Equipa da Clínica Dentária</strong>
                                            </p>
                                        </td>
                                    </tr>
                
                                </table>
                
                            </td>
                        </tr>
                    </table>
                
                </body>
                </html>
                """.formatted(link, link);

        try {
            MimeMessage mensagem = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(mensagem);

        } catch (MessagingException | MailException ex) {
            System.err.println("[EmailService] Falha ao enviar e-mail para " + destinatario + ": " + ex.getMessage());
            throw new RuntimeException("Não foi possível enviar o e-mail. Tente novamente mais tarde.");
        }
    }
}