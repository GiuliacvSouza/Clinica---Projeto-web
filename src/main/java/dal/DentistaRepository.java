package dal;
import model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Integer> {
    @Query("SELECT d FROM Dentista d LEFT JOIN FETCH d.utilizador ORDER BY d.utilizador.primeiroNome, d.utilizador.ultimoNome")
    List<Dentista> findAllComUtilizador();

    List<Dentista> findByAtivo(Boolean ativo);
    // dentistas disponíveis num horário (RF02)
    @Query("SELECT d FROM Dentista d WHERE d.horarioEntrada <= :hora AND d.horarioSaida >= :hora AND d.ativo = true")
    List<Dentista> findDisponivelNoHorario(@Param("hora") LocalTime hora);
}
