package dal;
import model.PacientexSeguro;
import model.PacientexSeguroId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PacientexSeguroRepository
        extends JpaRepository<PacientexSeguro, PacientexSeguroId> {
    @Query("SELECT ps FROM PacientexSeguro ps " +
           "LEFT JOIN FETCH ps.idUtilizador p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH ps.idSeguro")
    List<PacientexSeguro> findAllComRelacionamentos();

    List<PacientexSeguro> findByIdUtilizador_Id(Integer idPaciente);
    List<PacientexSeguro> findByIdSeguro_Id(Integer idSeguro);
}
