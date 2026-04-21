package dal;
import model.Recepcionista;
import model.enums.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Integer> {
    List<Recepcionista> findByTurno(Turno turno);

    Optional<Recepcionista> findByUtilizadorId(Integer utilizadorId);

    // Opcional: buscar por data de admissão
    List<Recepcionista> findByDataAdmissaoBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
}
