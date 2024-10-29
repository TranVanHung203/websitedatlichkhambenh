package tlcn.quanlyphongkham.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;

@Repository
public interface LichKhamBenhRepository extends JpaRepository<LichKhamBenh, String> {
    // Tìm tất cả lịch khám theo ngày
    List<LichKhamBenh> findByNgayThangNam(LocalDate ngayThangNam);
    boolean existsByNgayThangNamAndCaAndBacSi(LocalDate ngayThangNam, String ca, BacSi bacSi);
	
}
