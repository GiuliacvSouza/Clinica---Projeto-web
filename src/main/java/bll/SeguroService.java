package bll;

import dal.SeguroRepository;
import model.Seguro;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeguroService {

    private final SeguroRepository repository;

    public SeguroService(SeguroRepository repository) {
        this.repository = repository;
    }

    public Seguro salvar(Seguro seguro) {
        if (seguro.getNomeSeguro() == null || seguro.getNomeSeguro().isBlank()) {
            throw new RuntimeException("Nome do seguro é obrigatório.");
        }

        if (seguro.getValidoAte() != null && seguro.getValidoAte().isBefore(LocalDate.now())) {
            throw new RuntimeException("Seguro já expirado.");
        }

        return repository.save(seguro);
    }

    public List<Seguro> listarTodos() {
        return repository.findAll();
    }

    public List<Seguro> listarDisponiveisOuCriarPadrao() {
        List<Seguro> disponiveis = listarSegurosUnicosValidos();
        if (disponiveis.size() >= 3) {
            return disponiveis;
        }

        criarSeguroPadrao("Médis Dental", "Premium Oral", "MD-DENT-01", "apoio@medisdental.pt");
        criarSeguroPadrao("Multicare Dental", "Rede Essencial", "MC-DENT-02", "suporte@multicaredental.pt");
        criarSeguroPadrao("AdvanceCare Dental", "Plus Sorriso", "AC-DENT-03", "cliente@advancecaredental.pt");
        criarSeguroPadrao("Allianz Dental", "Protecao Familiar", "AZ-DENT-04", "servico@allianzdental.pt");

        return listarSegurosUnicosValidos();
    }

    public Seguro buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguro não encontrado."));
    }

    public void excluir(Integer id) {
        Seguro seguro = buscarPorId(id);
        repository.delete(seguro);
    }

    private List<Seguro> listarSegurosUnicosValidos() {
        Map<String, Seguro> unicos = new LinkedHashMap<>();
        LocalDate hoje = LocalDate.now();

        for (Seguro seguro : repository.findAll()) {
            if (seguro == null) {
                continue;
            }
            if (seguro.getValidoAte() != null && seguro.getValidoAte().isBefore(hoje)) {
                continue;
            }

            String chave = ((seguro.getNomeSeguro() == null ? "" : seguro.getNomeSeguro().trim()) + "|" +
                    (seguro.getTipoPlano() == null ? "" : seguro.getTipoPlano().trim())).toLowerCase();
            if (chave.isBlank()) {
                continue;
            }
            unicos.putIfAbsent(chave, seguro);
        }

        List<Seguro> seguros = new ArrayList<>(unicos.values());
        seguros.sort(Comparator.comparing(seguro -> seguro.getNomeSeguro() == null ? "" : seguro.getNomeSeguro().toLowerCase()));
        return seguros;
    }

    private void criarSeguroPadrao(String nome, String plano, String codigoPlano, String contacto) {
        boolean jaExiste = repository.findAll().stream()
                .anyMatch(seguro -> seguro != null
                        && seguro.getNomeSeguro() != null
                        && seguro.getNomeSeguro().trim().equalsIgnoreCase(nome));
        if (jaExiste) {
            return;
        }

        Seguro seguro = new Seguro();
        seguro.setNomeSeguro(nome);
        seguro.setTipoPlano(plano);
        seguro.setCodigoPlano(codigoPlano);
        seguro.setContactoSeguradora(contacto);
        seguro.setValidoAte(LocalDate.now().plusYears(3));
        salvar(seguro);
    }
}
