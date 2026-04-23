package dal;

import model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.utilizador ORDER BY p.utilizador.primeiroNome, p.utilizador.ultimoNome")
    List<Paciente> findAllComUtilizador();

    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.utilizador WHERE p.id = :id")
    Optional<Paciente> findByIdComUtilizador(@Param("id") Integer id);

    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.utilizador WHERE p.utilizador.nif = :nif")
    Optional<Paciente> findByNif(@Param("nif") String nif);

    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.utilizador WHERE p.utilizador.telemovel = :telemovel")
    Optional<Paciente> findByTelemovel(@Param("telemovel") String telemovel);

    @Query("SELECT p FROM Paciente p LEFT JOIN FETCH p.utilizador WHERE LOWER(p.utilizador.primeiroNome) LIKE LOWER(CONCAT('%', :nome, '%')) OR LOWER(p.utilizador.ultimoNome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Paciente> findByNome(@Param("nome") String nome);

    @Query("""
            SELECT DISTINCT p
            FROM Paciente p
            LEFT JOIN FETCH p.utilizador u
            WHERE LOWER(u.primeiroNome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(u.ultimoNome) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR LOWER(CONCAT(COALESCE(u.primeiroNome, ''), ' ', COALESCE(u.ultimoNome, ''))) LIKE LOWER(CONCAT('%', :termo, '%'))
               OR u.nif LIKE CONCAT('%', :termo, '%')
            ORDER BY u.primeiroNome, u.ultimoNome
            """)
    List<Paciente> pesquisarPorNomeOuNif(@Param("termo") String termo);

    List<Paciente> findByStatus(String status);
}
