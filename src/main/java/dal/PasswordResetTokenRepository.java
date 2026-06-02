package dal;

import model.PasswordResetToken;
import model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /** Apaga todos os tokens anteriores do utilizador antes de criar um novo. */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.utilizador = :utilizador")
    void deleteByUtilizador(@Param("utilizador") Utilizador utilizador);
}
