package dal;
import model.Fatura;
import model.enums.EstadoFatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Integer> {
    // Retornar possivelmente multiplas faturas para um mesmo atendimento (protege contra dados duplicados)
    List<Fatura> findByIdAtendimento_IdOrderByDataEmissaoDesc(Integer idAtendimento);
    List<Fatura> findByEstado(EstadoFatura estado);
}