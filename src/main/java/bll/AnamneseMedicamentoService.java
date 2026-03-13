package bll;

import dal.AnamneseMedicamentoRepository;
import model.AnamneseMedicamento;
import model.AnamneseMedicamentoId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnamneseMedicamentoService {

    private final AnamneseMedicamentoRepository repository;

    public AnamneseMedicamentoService(AnamneseMedicamentoRepository repository) {
        this.repository = repository;
    }

    public AnamneseMedicamento adicionar(AnamneseMedicamento item){
        return repository.save(item);
    }

    public void remover(AnamneseMedicamentoId id){

        if(!repository.existsById(id)){
            throw new RuntimeException("Registro não encontrado.");
        }

        repository.deleteById(id);
    }

    public List<AnamneseMedicamento> listar(){
        return repository.findAll();
    }

    public List<AnamneseMedicamento> buscarPorAnamnese(Integer idAnamnese){
        return repository.findByIdAnamnese_Id(idAnamnese);
    }

    public List<AnamneseMedicamento> buscarPorMedicamento(Integer idMedicamento){
        return repository.findByIdMedicamento_Id(idMedicamento);
    }
}