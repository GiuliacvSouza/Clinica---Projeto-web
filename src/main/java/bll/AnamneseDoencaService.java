package bll;

import dal.AnamneseDoencaRepository;
import model.AnamneseDoenca;
import model.AnamneseDoencaId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnamneseDoencaService {

    private final AnamneseDoencaRepository repository;

    public AnamneseDoencaService(AnamneseDoencaRepository repository) {
        this.repository = repository;
    }

    public AnamneseDoenca adicionar(AnamneseDoenca item){
        return repository.save(item);
    }

    public void remover(AnamneseDoencaId id){

        if(!repository.existsById(id)){
            throw new RuntimeException("Registro não encontrado.");
        }

        repository.deleteById(id);
    }

    public List<AnamneseDoenca> listar(){
        return repository.findAll();
    }

    public List<AnamneseDoenca> buscarPorAnamnese(Integer idAnamnese){
        return repository.findByIdAnamnese_Id(idAnamnese);
    }

    public List<AnamneseDoenca> buscarPorDoenca(Integer idDoenca){
        return repository.findByIdDoenca_Id(idDoenca);
    }
}