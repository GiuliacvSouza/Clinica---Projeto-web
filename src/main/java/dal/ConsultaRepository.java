package dal;
import model.Consulta;
import model.dto.ConsultaAgendadaDTO;
import model.enums.EstadoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
    // consultas de um paciente (RF36)
    List<Consulta> findByIdPaciente_Id(Integer idPaciente);
    // agenda do dentista (RF18)
    List<Consulta> findByIdDentista_Id(Integer idDentista);
    
    // Listar todas as consultas com dados corrigidos
    @Query("SELECT new model.dto.ConsultaAgendadaDTO(" +
           "c.id, " +
           "CONCAT(p.utilizador.primeiroNome, ' ', p.utilizador.ultimoNome), " +
           "CONCAT(d.utilizador.primeiroNome, ' ', d.utilizador.ultimoNome), " +
           "c.tipo, " +
           "c.dataHoraInicio, " +
           "c.status) " +
           "FROM Consulta c " +
           "LEFT JOIN c.idPaciente p " +
           "LEFT JOIN c.idDentista d " +
           "ORDER BY c.dataHoraInicio DESC")
    List<ConsultaAgendadaDTO> findAllAgendadas();
    
    // Listar por status com dados corrigidos
    @Query("SELECT new model.dto.ConsultaAgendadaDTO(" +
           "c.id, " +
           "CONCAT(p.utilizador.primeiroNome, ' ', p.utilizador.ultimoNome), " +
           "CONCAT(d.utilizador.primeiroNome, ' ', d.utilizador.ultimoNome), " +
           "c.tipo, " +
           "c.dataHoraInicio, " +
           "c.status) " +
           "FROM Consulta c " +
           "LEFT JOIN c.idPaciente p " +
           "LEFT JOIN c.idDentista d " +
           "WHERE c.status = :status " +
           "ORDER BY c.dataHoraInicio DESC")
    List<ConsultaAgendadaDTO> findByStatusAgendadas(@Param("status") EstadoConsulta status);
    
    // consultas por estado (RF16) - com eager loading
    @Query("SELECT DISTINCT c FROM Consulta c " +
           "LEFT JOIN FETCH c.idPaciente p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH c.idDentista d " +
           "LEFT JOIN FETCH d.utilizador " +
           "WHERE c.status = :status")
    List<Consulta> findByStatus(@Param("status") EstadoConsulta status);
    
    // listar todas com eager loading
    @Query("SELECT DISTINCT c FROM Consulta c " +
           "LEFT JOIN FETCH c.idPaciente p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH c.idDentista d " +
           "LEFT JOIN FETCH d.utilizador")
    List<Consulta> findAllEager();

    @Query("SELECT c FROM Consulta c " +
           "LEFT JOIN FETCH c.idPaciente p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH c.idDentista d " +
           "LEFT JOIN FETCH d.utilizador " +
           "WHERE c.id = :id")
    Optional<Consulta> findByIdEager(@Param("id") Integer id);
    
    // consultas de um dentista num dia (RF16, RF18)
    @Query("SELECT c FROM Consulta c WHERE c.idDentista.id = :idDentista AND c.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Consulta> findByDentistaEDia(
            @Param("idDentista") Integer idDentista,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );
    // verificar disponibilidade de horário (RF02)
    @Query("SELECT c FROM Consulta c WHERE c.idDentista.id = :idDentista AND c.dataHoraInicio = :dataHora AND c.status != 'CANCELADA'")
    List<Consulta> findConflitoHorario(
            @Param("idDentista") Integer idDentista,
            @Param("dataHora") Instant dataHora
    );
}
