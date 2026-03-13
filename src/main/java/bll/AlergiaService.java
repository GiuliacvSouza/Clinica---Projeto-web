package bll;

import dal.AlergiaRepository;
import model.Alergia;
import model.enums.TipoAlergia;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlergiaService {

    private final AlergiaRepository alergiaRepository;

    public AlergiaService(AlergiaRepository alergiaRepository) {
        this.alergiaRepository = alergiaRepository;
    }

    public Alergia criar(Alergia alergia) {

        if(alergia.getSubstancia() == null || alergia.getSubstancia().trim().isEmpty()){
            throw new RuntimeException("A substância da alergia é obrigatória.");
        }

        return alergiaRepository.save(alergia);
    }

    public Alergia atualizar(Integer id, Alergia dados){

        Alergia alergia = alergiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alergia não encontrada."));

        alergia.setSubstancia(dados.getSubstancia());
        alergia.setTipo(dados.getTipo());

        return alergiaRepository.save(alergia);
    }

    public void remover(Integer id){

        if(!alergiaRepository.existsById(id)){
            throw new RuntimeException("Alergia não encontrada.");
        }

        alergiaRepository.deleteById(id);
    }

    public List<Alergia> listar(){
        return alergiaRepository.findAll();
    }

    public Optional<Alergia> buscarPorId(Integer id){
        return alergiaRepository.findById(id);
    }

    public List<Alergia> buscarPorTipo(TipoAlergia tipo){
        return alergiaRepository.findByTipo(tipo);
    }

    public List<Alergia> buscarPorSubstancia(String substancia){
        return alergiaRepository.findBySubstanciaContainingIgnoreCase(substancia);
    }
}