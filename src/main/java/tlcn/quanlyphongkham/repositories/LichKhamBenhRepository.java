package tlcn.quanlyphongkham.repositories;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import tlcn.quanlyphongkham.dtos.LichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;

@Repository
public interface LichKhamBenhRepository extends JpaRepository<LichKhamBenh, String> {
	// Tìm tất cả lịch khám theo ngày
	List<LichKhamBenh> findByNgayThangNam(LocalDate ngayThangNam);

	boolean existsByNgayThangNamAndCaAndBacSi(LocalDate ngayThangNam, String ca, BacSi bacSi);

	@Query(value = "SELECT ma_lich_id,ca,ngay_thang_nam,bac_si_id FROM lich_kham_benh WHERE bac_si_id = :idbacsi AND ngay_thang_nam = :ngay", nativeQuery = true)
	List<LichKhamBenh> findByIdBacSiAndNgayThangNam(@Param("idbacsi") String idbacsi, @Param("ngay") LocalDate ngay);

	@Modifying  // Thêm @Modifying để chỉ rõ đây là câu lệnh thay đổi dữ liệu (INSERT, UPDATE, DELETE)
    @Transactional  // Đảm bảo phương thức được thực thi trong một transaction
    @Query(value = "INSERT INTO lich_kham_benh (ma_lich_id, bac_si_id, ngay_thang_nam, ca) "
            + "VALUES (:maLichId, :bacSiId, :ngayThangNam, :ca)", nativeQuery = true)
    void addNew(@Param("maLichId") String maLichId, @Param("bacSiId") String bacSiId,
                @Param("ngayThangNam") LocalDate ngayThangNam, @Param("ca") String ca);


	@Query(value = "SELECT * FROM lich_kham_benh WHERE bac_si_id = :bacSiId AND ngay_thang_nam = :ngayThangNam AND ca = :ca", nativeQuery = true)
	List<LichKhamBenh> findByIdBacSiAndNgayAndCa(@Param("bacSiId") String bacSiId,
	                                               @Param("ngayThangNam") LocalDate ngayThangNam,
	                                               @Param("ca") String ca);

	
	List<LichKhamBenh> findByBacSi_BacSiIdAndNgayThangNam(String bacSiId, LocalDate date);



	

	List<LichKhamBenh> findByNgayThangNamBetween(LocalDate startDate, LocalDate endDate);

	List<LichKhamBenh> findByBacSiTenContaining(String tenBacSi);
	
	
	
	  List<LichKhamBenh> findByBacSi_ChuyenKhoa_ChuyenKhoaIdAndNgayThangNam(
	            String chuyenKhoaId, LocalDate ngayThangNam);

	  @Query("SELECT new tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO(l.maLichKhamBenh) "
		       + "FROM LichKhamBenh l "
		       + "JOIN l.bacSi b " // Liên kết với thực thể BacSi qua đối tượng bacSi
		       + "WHERE b.bacSiId = :bacSiId " // Sử dụng bacSiId trong đối tượng BacSi
		       + "AND l.ngayThangNam = :ngayThangNam "
		       + "AND l.ca = :caKham")
		MaLichKhamBenhDTO findByBacSiAndNgayThangNamAndCa(@Param("bacSiId") String bacSiId, 
		                                                 @Param("ngayThangNam") LocalDate selectedDates, 
		                                                 @Param("caKham") String caKham);



	



}
