package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tlcn.quanlyphongkham.entities.VitalSigns;

import java.util.Optional;

public interface VitalSignsRepository extends JpaRepository<VitalSigns, Long> {
    Optional<VitalSigns> findByHoSoBenhHoSoId(String hoSoId);
}