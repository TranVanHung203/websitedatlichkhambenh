package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tlcn.quanlyphongkham.entities.XetNghiem;

import java.util.List;

public interface XetNghiemRepository extends JpaRepository<XetNghiem, Long> {
    List<XetNghiem> findByHoSoBenhHoSoId(String hoSoId);
}