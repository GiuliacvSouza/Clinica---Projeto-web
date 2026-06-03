package dal;
import model.Fatura;
import model.enums.EstadoFatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FaturaRepository extends JpaRepository<Fatura, Integer> {

    // Retornar possivelmente multiplas faturas para um mesmo atendimento (protege contra dados duplicados)
    List<Fatura> findByIdAtendimento_IdOrderByDataEmissaoDesc(Integer idAtendimento);

    List<Fatura> findByEstado(EstadoFatura estado);

    /**
     * Devolve todas as faturas de um paciente específico, navegando a cadeia
     * Fatura → Atendimento → Consulta → Paciente.
     * Usada na área do paciente para listar e descarregar faturas.
     */
    @Query("""
           SELECT f FROM Fatura f
           JOIN FETCH f.idAtendimento a
           JOIN FETCH a.idConsulta c
           JOIN FETCH c.idPaciente p
           WHERE p.id = :pacienteId
           ORDER BY f.dataEmissao DESC
           """)
    List<Fatura> findByPacienteId(@Param("pacienteId") Integer pacienteId);

    /**
     * Devolve uma fatura específica garantindo que pertence ao paciente indicado.
     * JOIN FETCH garante que todos os dados são carregados na mesma query —
     * evita LazyInitializationException ao aceder a relações fora da sessão JPA.
     */
    @Query("""
           SELECT f FROM Fatura f
           JOIN FETCH f.idAtendimento a
           JOIN FETCH a.idConsulta c
           JOIN FETCH c.idPaciente p
           WHERE f.id = :faturaId AND p.id = :pacienteId
           """)
    Optional<Fatura> findByIdAndPacienteId(
            @Param("faturaId") Integer faturaId,
            @Param("pacienteId") Integer pacienteId
    );
}