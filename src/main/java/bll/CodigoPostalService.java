package bll;

import dal.CodigoPostalRepository;
import model.CodigoPostal;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodigoPostalService {

    private final CodigoPostalRepository repository;

    public CodigoPostalService(CodigoPostalRepository repository) {
        this.repository = repository;
    }

    public CodigoPostal salvar(CodigoPostal codigoPostal) {

        if (codigoPostal.getCodigoPostal() == null || codigoPostal.getCodigoPostal().isBlank()) {
            throw new RuntimeException("Código postal é obrigatório.");
        }

        if (codigoPostal.getLocalidade() == null || codigoPostal.getLocalidade().isBlank()) {
            throw new RuntimeException("Localidade é obrigatória.");
        }

        boolean existe = repository.existsById(codigoPostal.getCodigoPostal());

        if (existe) {
            throw new RuntimeException("Código postal já cadastrado.");
        }

        return repository.save(codigoPostal);
    }

    public List<CodigoPostal> listarTodos() {
        return repository.findAll();
    }

    public CodigoPostal buscarPorId(String codigo) {

        return repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Código postal não encontrado."));
    }

    public void excluir(String codigo) {

        CodigoPostal cp = buscarPorId(codigo);

        repository.delete(cp);
    }
}