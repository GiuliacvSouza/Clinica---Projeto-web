package bll;

import dal.FaturaRepository;
import model.Fatura;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FaturaService {

    private final FaturaRepository repository;

    public FaturaService(FaturaRepository repository) {
        this.repository = repository;
    }

    public Fatura emitirFatura(Fatura fatura) {

        if (fatura.getIdAtendimento() == null) {
            throw new RuntimeException("Fatura precisa de atendimento.");
        }

        if (fatura.getValorFinal() == null ||
                fatura.getValorFinal().doubleValue() <= 0) {

            throw new RuntimeException("Valor da fatura inválido.");
        }

        fatura.setDataEmissao(LocalDate.now());

        return repository.save(fatura);
    }

    public List<Fatura> listarTodos() {
        return repository.findAll();
    }

    public Fatura buscarPorId(Integer id) {

        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada."));
    }

    public void excluir(Integer id) {
        repository.deleteById(id);
    }
}