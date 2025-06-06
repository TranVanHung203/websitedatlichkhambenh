package tlcn.quanlyphongkham.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.DonThuoc;

public interface DonThuocRepository extends JpaRepository<DonThuoc, Long> {
	boolean existsByHoSoBenh_HoSoId(String hoSoId);



	@Query(value = "SELECT dt.* FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "JOIN benh_nhan bn ON hs.benh_nhan_id = bn.benh_nhan_id " +
	        "WHERE hs.bac_si_id = :bacSiId AND LOWER(bn.ten) LIKE LOWER(CONCAT('%', :name, '%'))",
	        countQuery = "SELECT COUNT(dt.don_thuoc_id) FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "JOIN benh_nhan bn ON hs.benh_nhan_id = bn.benh_nhan_id " +
	        "WHERE hs.bac_si_id = :bacSiId AND LOWER(bn.ten) LIKE LOWER(CONCAT('%', :name, '%'))",
	        nativeQuery = true)
	Page<DonThuoc> findByBacSiIdAndBenhNhanNameContaining(@Param("bacSiId") String bacSiId, @Param("name") String name,
	                                                      Pageable pageable);

	@Query(value = "SELECT dt.* FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "WHERE hs.bac_si_id = :bacSiId",
	        countQuery = "SELECT COUNT(dt.don_thuoc_id) FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "WHERE hs.bac_si_id = :bacSiId",
	        nativeQuery = true)
	Page<DonThuoc> findAllByBacSiId(@Param("bacSiId") String bacSiId, Pageable pageable);
	
	@Query(value = "SELECT dt.* FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "WHERE hs.benh_nhan_id = :benhNhanId",
	        countQuery = "SELECT COUNT(dt.don_thuoc_id) FROM don_thuoc dt " +
	        "JOIN ho_so_benh hs ON dt.ho_so_id = hs.ho_so_id " +
	        "WHERE hs.benh_nhan_id = :benhNhanId",
	        nativeQuery = true)
	Page<DonThuoc> findByBenhNhanId(@Param("benhNhanId") String benhNhanId, Pageable pageable);

	
	@Query("SELECT d FROM DonThuoc d WHERE d.hoSoBenh.benhNhan.benhNhanId = :benhNhanId AND DATE(d.hoSoBenh.thoiGianTao) = :filterDate")
	Page<DonThuoc> findByBenhNhanIdAndDate(@Param("benhNhanId") String benhNhanId, 
	                                       @Param("filterDate") LocalDate filterDate, 
	                                       Pageable pageable);


}