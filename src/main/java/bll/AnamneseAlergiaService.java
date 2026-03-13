package bll;

import dal.AnamneseAlergiaRepository;
import model.AnamneseAlergia;
import model.AnamneseAlergiaId;
import model.enums.Gravidade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnamneseAlergiaService {

    private final AnamneseAlergiaRepository repository;

    public AnamneseAlergiaService(AnamneseAlergiaRepository repository) {
        this.repository = repository;
    }

    public AnamneseAlergia adicionar(AnamneseAlergia item){

        if(item.getGravidade() == null){
            throw new RuntimeException("Gravidade da alergia deve ser informada.");
        }

        return repository.save(item);
    }

    public void remover(AnamneseAlergiaId id){

        if(!repository.existsById(id)){
            throw new RuntimeException("Registro não encontrado.");
        }

        repository.deleteById(id);
    }

    public List<AnamneseAlergia> listar(){
        return repository.findAll();
    }

    public List<AnamneseAlergia> buscarPorAnamnese(Integer idAnamnese){
        return repository.findByIdAnamnese_Id(idAnamnese);
    }

    public List<AnamneseAlergia> buscarPorAlergia(Integer idAlergia){
        return repository.findByIdAlergia_Id(idAlergia);
    }

    public List<AnamneseAlergia> buscarPorGravidade(Gravidade gravidade){
        return repository.findByGravidade(gravidade);
    }
}