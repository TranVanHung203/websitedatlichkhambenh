package tlcn.quanlyphongkham.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, String> {

	List<BacSi> findByChuyenKhoa(ChuyenKhoa chuyenKhoa);

	 BacSi findByBacSiId(String bacSiId);

	Page<BacSi> findByChuyenKhoa_ChuyenKhoaId(String chuyenKhoaId, Pageable pageable);


	Page<BacSi> findByChuyenKhoa_Ten(String tenChuyenKhoa, Pageable pageable);

	Page<BacSi> findByTenContainingIgnoreCase(String doctorName, Pageable pageable);

	Page<BacSi> findByTenContainingIgnoreCaseAndChuyenKhoa_Ten(String doctorName, String section, Pageable pageable);

	Page<BacSi> findByDienThoaiContaining(String phone, Pageable pageable);

	BacSi findByNguoiDung_NguoiDungId(String nguoiDungId);

	List<BacSi> findByChuyenKhoaChuyenKhoaId(String chuyenKhoaId);

	  List<BacSi> findByChuyenKhoa_TenContainingIgnoreCaseAndGiaKhamBetween(String tenChuyenKhoa, BigDecimal giaKhamMin, BigDecimal giaKhamMax);

	

	  List<BacSi> findByTenContainingIgnoreCaseAndGiaKhamBetween(String ten, BigDecimal minPrice, BigDecimal maxPrice);
	  long count();
}
