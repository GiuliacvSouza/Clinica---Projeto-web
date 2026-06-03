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
    List<Consulta> findByIdPaciente_Id(Integer idPaciente);

    List<Consulta> findByIdDentista_Id(Integer idDentista);

    @Query("SELECT new model.dto.ConsultaAgendadaDTO(" +
           "c.id, " +
           "CONCAT(p.utilizador.primeiroNome, ' ', p.utilizador.ultimoNome), " +
           "CONCAT(d.utilizador.primeiroNome, ' ', d.utilizador.ultimoNome), " +
           "c.tipo, " +
           "c.dataHoraInicio, " +
           "c.status, " +
           "p.utilizador.nif, " +
           "p.id) " +
           "FROM Consulta c " +
           "LEFT JOIN c.idPaciente p " +
           "LEFT JOIN c.idDentista d " +
           "ORDER BY c.dataHoraInicio DESC")
    List<ConsultaAgendadaDTO> findAllAgendadas();

    @Query("SELECT new model.dto.ConsultaAgendadaDTO(" +
           "c.id, " +
           "CONCAT(p.utilizador.primeiroNome, ' ', p.utilizador.ultimoNome), " +
           "CONCAT(d.utilizador.primeiroNome, ' ', d.utilizador.ultimoNome), " +
           "c.tipo, " +
           "c.dataHoraInicio, " +
           "c.status, " +
           "p.utilizador.nif, " +
           "p.id) " +
           "FROM Consulta c " +
           "LEFT JOIN c.idPaciente p " +
           "LEFT JOIN c.idDentista d " +
           "WHERE c.status = :status " +
           "ORDER BY c.dataHoraInicio DESC")
    List<ConsultaAgendadaDTO> findByStatusAgendadas(@Param("status") EstadoConsulta status);

    @Query("SELECT new model.dto.ConsultaAgendadaDTO(" +
           "c.id, " +
           "CONCAT(p.utilizador.primeiroNome, ' ', p.utilizador.ultimoNome), " +
           "CONCAT(d.utilizador.primeiroNome, ' ', d.utilizador.ultimoNome), " +
           "c.tipo, " +
           "c.dataHoraInicio, " +
           "c.status, " +
           "p.utilizador.nif, " +
           "p.id) " +
           "FROM Consulta c " +
           "LEFT JOIN c.idPaciente p " +
           "LEFT JOIN p.utilizador u " +
           "LEFT JOIN c.idDentista d " +
           "WHERE (:status IS NULL OR c.status = :status) " +
           "AND (:dentistaId IS NULL OR d.id = :dentistaId) " +
           "AND (:pacienteId IS NULL OR p.id = :pacienteId) " +
           "AND (:filtrarDataInicio = false OR c.dataHoraInicio >= :dataInicio) " +
           "AND (:filtrarDataFim = false OR c.dataHoraInicio <= :dataFim) " +
           "AND (:filtrarTipo = false OR LOWER(c.tipo) = :tipo) " +
           "AND (:filtrarPesquisa = false OR " +
           "LOWER(u.primeiroNome) LIKE :pesquisa OR " +
           "LOWER(u.ultimoNome) LIKE :pesquisa OR " +
           "LOWER(CONCAT(u.primeiroNome, ' ', u.ultimoNome)) LIKE :pesquisa OR " +
           "LOWER(u.email) LIKE :pesquisa OR " +
           "u.telemovel LIKE :pesquisa OR " +
           "u.telefone LIKE :pesquisa) " +
           "ORDER BY c.dataHoraInicio DESC")
    List<ConsultaAgendadaDTO> filtrarConsultasAgendadas(
            @Param("status") EstadoConsulta status,
            @Param("dentistaId") Integer dentistaId,
            @Param("pacienteId") Integer pacienteId,
            @Param("filtrarDataInicio") boolean filtrarDataInicio,
            @Param("dataInicio") Instant dataInicio,
            @Param("filtrarDataFim") boolean filtrarDataFim,
            @Param("dataFim") Instant dataFim,
            @Param("filtrarTipo") boolean filtrarTipo,
            @Param("tipo") String tipo,
            @Param("filtrarPesquisa") boolean filtrarPesquisa,
            @Param("pesquisa") String pesquisa
    );

    @Query("SELECT DISTINCT c.tipo FROM Consulta c WHERE c.tipo IS NOT NULL AND TRIM(c.tipo) <> '' ORDER BY c.tipo")
    List<String> findTiposConsulta();

    @Query("SELECT DISTINCT c FROM Consulta c " +
           "LEFT JOIN FETCH c.idPaciente p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH c.idDentista d " +
           "LEFT JOIN FETCH d.utilizador " +
           "WHERE c.status = :status")
    List<Consulta> findByStatus(@Param("status") EstadoConsulta status);

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

    @Query("SELECT DISTINCT c FROM Consulta c " +
           "LEFT JOIN FETCH c.idPaciente p " +
           "LEFT JOIN FETCH p.utilizador " +
           "LEFT JOIN FETCH c.idDentista d " +
           "LEFT JOIN FETCH d.utilizador " +
           "WHERE p.id = :idPaciente " +
           "ORDER BY c.dataHoraInicio DESC")
    List<Consulta> findByPacienteIdEager(@Param("idPaciente") Integer idPaciente);

    @Query("SELECT c FROM Consulta c WHERE c.idDentista.id = :idDentista AND c.dataHoraInicio BETWEEN :inicio AND :fim")
    List<Consulta> findByDentistaEDia(
            @Param("idDentista") Integer idDentista,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim
    );

    @Query("SELECT c FROM Consulta c " +
           "WHERE c.idDentista.id = :idDentista " +
           "AND c.dataHoraInicio BETWEEN :inicio AND :fim " +
           "AND c.status <> :statusCancelada")
    List<Consulta> findOcupadasPorDentistaEntre(
            @Param("idDentista") Integer idDentista,
            @Param("inicio") Instant inicio,
            @Param("fim") Instant fim,
            @Param("statusCancelada") EstadoConsulta statusCancelada
    );

    @Query("SELECT c FROM Consulta c WHERE c.idDentista.id = :idDentista AND c.dataHoraInicio = :dataHora AND c.status != 'CANCELADA'")
    List<Consulta> findConflitoHorario(
            @Param("idDentista") Integer idDentista,
            @Param("dataHora") Instant dataHora
    );
}
