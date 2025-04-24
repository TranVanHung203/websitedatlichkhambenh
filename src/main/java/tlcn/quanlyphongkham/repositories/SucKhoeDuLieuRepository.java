package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tlcn.quanlyphongkham.entities.SucKhoeDuLieu;

import java.time.LocalDateTime;
import java.util.List;

public interface SucKhoeDuLieuRepository extends JpaRepository<SucKhoeDuLieu, Long> {
    List<SucKhoeDuLieu> findByNguoiDungIdAndChiSoAndThoiGianAfterOrderByThoiGianAsc(
            String nguoiDungId, String chiSo, LocalDateTime thoiGian);
}