import bll.UtilizadorService;
import bll.PacienteService;
import bll.SeguroService;
import bll.PacientexSeguroService;
import model.Utilizador;
import model.Paciente;
import model.Seguro;
import model.PacientexSeguro;

public class Main {

    public static void main(String[] args) {

        UtilizadorService utilizadorService = new UtilizadorService();
        PacienteService pacienteService = new PacienteService();
        SeguroService seguroService = new SeguroService();
        PacientexSeguroService pacientexSeguroService = new PacientexSeguroService();

        try {

            // Criar utilizador
            Utilizador u = new Utilizador();
            u.setPrimeiroNome("João");
            u.setUltimoNome("Silva");
            u.setEmail("joao@email.com");

            utilizadorService.salvar(u);

            System.out.println("Utilizador criado");

            // Criar paciente
            Paciente p = new Paciente();
            p.setUtilizador(u);

            pacienteService.salvar(p);

            System.out.println("Paciente criado");

            // Criar seguro
            Seguro s = new Seguro();
            s.setNomeSeguro("Medicare");

            seguroService.salvar(s);

            System.out.println("Seguro criado");

            // Associar paciente ao seguro
            PacientexSeguro ps = new PacientexSeguro();
            ps.setIdUtilizador(p);
            ps.setIdSeguro(s);

            pacientexSeguroService.salvar(ps);

            System.out.println("Seguro associado ao paciente");

        } catch (Exception e) {

            System.out.println("Erro: " + e.getMessage());

        }

    }

}