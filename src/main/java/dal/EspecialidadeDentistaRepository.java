package dal;

import model.EspecialidadeDentista;
import model.EspecialidadeDentistaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecialidadeDentistaRepository
        extends JpaRepository<EspecialidadeDentista, EspecialidadeDentistaId> {
}