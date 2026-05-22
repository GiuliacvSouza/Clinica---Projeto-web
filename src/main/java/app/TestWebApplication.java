package app;

import bll.UtilizadorService;
import dal.UtilizadorRepository;
import model.Utilizador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.HashMap;
import java.util.Map;

public class TestWebApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(WebTestConfig.class);
        app.setDefaultProperties(defaultProperties());
        app.run(args);

        System.out.println();
        System.out.println("Aplicacao web de teste iniciada com base de dados.");
        System.out.println("Abra no navegador: http://localhost:8080/login");
        System.out.println();
    }

    private static Map<String, Object> defaultProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("server.port", "8080");
        properties.put("spring.thymeleaf.cache", "false");
        properties.put("app.skipSeed", "true");
        return properties;
    }

    @SpringBootApplication(scanBasePackages = {"controller", "bll", "dal"})
    @EntityScan(basePackages = "model")
    @EnableJpaRepositories(basePackages = "dal")
    @Import(SecurityConfig.class)
    static class WebTestConfig {

        @Bean
        CommandLineRunner utilizadorTeste(UtilizadorRepository utilizadorRepository, UtilizadorService utilizadorService) {
            return args -> {
                String email = "admin@clinica.pt";
                if (utilizadorRepository.existsByEmail(email)) {
                    return;
                }

                Utilizador admin = new Utilizador();
                admin.setPrimeiroNome("Admin");
                admin.setUltimoNome("Clinica");
                admin.setEmail(email);
                admin.setTipoUtilizador("RECEPCIONISTA");
                admin.setSenha("Clinica2025!");
                admin.setStatus("ATIVO");
                utilizadorService.salvar(admin);

                System.out.println("Utilizador de teste criado: admin@clinica.pt / Clinica2025!");
            };
        }
    }
}
