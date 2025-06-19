package tlcn.quanlyphongkham.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.HoSoBenh;

@Repository
public interface HoSoBenhRepository extends JpaRepository<HoSoBenh, String> {

	@Query(value = """
			SELECT
			    hs.ho_so_id AS hoSoId,
			    bn.ten AS tenBenhNhan,
			    hs.chan_doan AS chanDoan,
			    DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') AS thoiGianTao,
			    GROUP_CONCAT(CONCAT('- ', t.ten) SEPARATOR '\\n') AS tenThuoc,
			    GROUP_CONCAT(dtt.lieu SEPARATOR '\\n') AS lieu,
			    GROUP_CONCAT(dtt.tan_suat SEPARATOR '\\n') AS tanSuat,
			    SUM(dtt.so_luong * t.gia) AS tongTienThuoc,
			    GROUP_CONCAT(CAST(dtt.so_luong AS CHAR) SEPARATOR '\\n') AS soLuong
			FROM ho_so_benh hs
			JOIN benh_nhan bn ON hs.benh_nhan_id = bn.benh_nhan_id
			JOIN don_thuoc dt ON hs.ho_so_id = dt.ho_so_id
			JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
			JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
			WHERE hs.bac_si_id = :bacSiId
			AND hs.thoi_gian_tao BETWEEN :startDateTime AND :endDateTime
			GROUP BY hs.ho_so_id, bn.ten, hs.chan_doan, hs.thoi_gian_tao
			""", countQuery = """
			SELECT COUNT(DISTINCT hs.ho_so_id)
			FROM ho_so_benh hs
			WHERE hs.bac_si_id = :bacSiId
			AND hs.thoi_gian_tao BETWEEN :startDateTime AND :endDateTime
			""", nativeQuery = true)
	Page<Object[]> findHoSoBenhWithGroupedThuoc(@Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime, @Param("bacSiId") String bacSiId, Pageable pageable);

	@Query(value = """
			SELECT
			    bs.ten AS tenBacSi,
			    DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') AS ngayKham,
			    hs.chan_doan AS chanDoan,
			    GROUP_CONCAT(CONCAT('- ', t.ten) SEPARATOR '\\n') AS thuoc,
			    GROUP_CONCAT(dtt.lieu SEPARATOR '\\n') AS lieu,
			    GROUP_CONCAT(dtt.tan_suat SEPARATOR '\\n') AS tanSuat,
			    hs.trieu_chung AS trieuChung,
			    SUM(dtt.so_luong * t.gia) AS tongTienThuoc,
			    GROUP_CONCAT(CAST(dtt.so_luong AS CHAR) SEPARATOR '\\n') AS soLuong
			FROM ho_so_benh hs
			JOIN bac_si bs ON hs.bac_si_id = bs.bac_si_id
			LEFT JOIN don_thuoc dt ON hs.ho_so_id = dt.ho_so_id
			LEFT JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
			LEFT JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
			WHERE hs.benh_nhan_id = :benhNhanId
			GROUP BY hs.ho_so_id, bs.ten, hs.thoi_gian_tao, hs.chan_doan, hs.trieu_chung
			ORDER BY hs.thoi_gian_tao DESC
			""", nativeQuery = true)
	Page<Object[]> findLichSuKhamByBenhNhanIdRaw(@Param("benhNhanId") String benhNhanId, Pageable pageable);

	@Query(value = """
			SELECT
			    bs.ten AS tenBacSi,
			    DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') AS ngayKham,
			    hs.chan_doan AS chanDoan,
			    GROUP_CONCAT(CONCAT('- ', t.ten) SEPARATOR '\\n') AS thuoc,
			    GROUP_CONCAT(dtt.lieu SEPARATOR '\\n') AS lieu,
			    GROUP_CONCAT(dtt.tan_suat SEPARATOR '\\n') AS tanSuat,
			    hs.trieu_chung AS trieuChung,
			    SUM(dtt.so_luong * t.gia) AS tongTienThuoc,
			    GROUP_CONCAT(CAST(dtt.so_luong AS CHAR) SEPARATOR '\\n') AS soLuong
			FROM ho_so_benh hs
			JOIN bac_si bs ON hs.bac_si_id = bs.bac_si_id
			LEFT JOIN don_thuoc dt ON hs.ho_so_id = dt.ho_so_id
			LEFT JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
			LEFT JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
			WHERE hs.benh_nhan_id = :benhNhanId
			AND DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') = :date
			GROUP BY hs.ho_so_id, bs.ten, hs.thoi_gian_tao, hs.chan_doan, hs.trieu_chung
			ORDER BY hs.thoi_gian_tao DESC
			""", nativeQuery = true)
	Page<Object[]> findLichSuKhamByBenhNhanIdAndDateRaw(@Param("benhNhanId") String benhNhanId,
			@Param("date") String date, Pageable pageable);

	@Query(value = "SELECT * FROM ho_so_benh WHERE benh_nhan_id = :benhNhanId", nativeQuery = true)
	List<HoSoBenh> findByBenhNhanId(@Param("benhNhanId") String benhNhanId);

	Optional<HoSoBenh> findByBenhNhan_BenhNhanIdAndBacSi_BacSiIdAndThoiGianTaoBetween(String benhNhanId, String bacSiId,
			LocalDateTime start, LocalDateTime end);

	@Query(value = """
			    SELECT
			        h.ho_so_id,
			        bn.ten AS ten_benh_nhan,
			        bs.ten AS ten_bac_si,
			        h.tong_tien,
			        h.chan_doan,
			        h.trieu_chung,
			        h.da_thanh_toan, -- Thêm cột này
			        GROUP_CONCAT(DISTINCT dt.don_thuoc_id) AS don_thuoc_ids,
			        GROUP_CONCAT(DISTINCT CONCAT(
			            dtt.don_thuoc_id, ':', t.ten, ':', dtt.so_luong, ':', t.gia, ':',
			            COALESCE(dtt.lieu, ''), ':', COALESCE(dtt.tan_suat, '')
			        ) SEPARATOR '|') AS thuoc_list,
			        GROUP_CONCAT(DISTINCT xn.xet_nghiem_id) AS xet_nghiem_ids,
			        GROUP_CONCAT(DISTINCT CONCAT(
			            xn.xet_nghiem_id, ':', lxn.ten_xet_nghiem, ':', lxn.gia
			        ) SEPARATOR '|') AS xet_nghiem_list
			    FROM
			        ho_so_benh h
			        INNER JOIN slot_thoi_gian st ON h.slot_id = st.slot_id
			        INNER JOIN benh_nhan bn ON h.benh_nhan_id = bn.benh_nhan_id
			        INNER JOIN bac_si bs ON h.bac_si_id = bs.bac_si_id
			        LEFT JOIN don_thuoc dt ON h.ho_so_id = dt.ho_so_id
			        LEFT JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
			        LEFT JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
			        LEFT JOIN xet_nghiem xn ON h.ho_so_id = xn.ho_so_id
			        LEFT JOIN loai_xet_nghiem lxn ON xn.loai_xet_nghiem_id = lxn.loai_xet_nghiem_id
			    WHERE
			        st.slot_id = :slotId
			        AND h.trang_thai = true
			    GROUP BY
			        h.ho_so_id, bn.ten, bs.ten, h.tong_tien, h.chan_doan, h.trieu_chung, h.da_thanh_toan -- Thêm h.da_thanh_toan vào GROUP BY
			""", nativeQuery = true)
	List<Object[]> findPaymentDetailsBySlotId(@Param("slotId") String slotId);

	@Query(value = "SELECT * FROM ho_so_benh WHERE benh_nhan_id = :benhNhanId", countQuery = "SELECT COUNT(*) FROM ho_so_benh WHERE benh_nhan_id = :benhNhanId", nativeQuery = true)
	Page<HoSoBenh> findByBenhNhanId(@Param("benhNhanId") String benhNhanId, Pageable pageable);

	@Query(value = "SELECT * FROM ho_so_benh WHERE benh_nhan_id = :benhNhanId AND thoi_gian_tao BETWEEN :startDate AND :endDate", countQuery = "SELECT COUNT(*) FROM ho_so_benh WHERE benh_nhan_id = :benhNhanId AND thoi_gian_tao BETWEEN :startDate AND :endDate", nativeQuery = true)
	Page<HoSoBenh> findByBenhNhanIdAndThoiGianTaoBetween(@Param("benhNhanId") String benhNhanId,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

	@Query("SELECT h FROM HoSoBenh h WHERE h.benhNhan.benhNhanId = :benhNhanId AND h.hoSoId IN :hoSoIds "
			+ "AND (:startDateTime IS NULL OR h.thoiGianTao >= :startDateTime) "
			+ "AND (:endDateTime IS NULL OR h.thoiGianTao <= :endDateTime)")
	List<HoSoBenh> findByIdsAndBenhNhanIdAndDateRange(@Param("hoSoIds") List<String> hoSoIds,
			@Param("benhNhanId") String benhNhanId, @Param("startDateTime") LocalDateTime startDateTime,
			@Param("endDateTime") LocalDateTime endDateTime);

	@Query(value = "SELECT hsb.* FROM ho_so_benh hsb " + "JOIN benh_nhan bn ON hsb.benh_nhan_id = bn.benh_nhan_id "
			+ "JOIN bac_si bs ON hsb.bac_si_id = bs.bac_si_id "
			+ "WHERE (:startDate IS NULL OR hsb.thoi_gian_tao >= :startDate) "
			+ "AND (:endDate IS NULL OR hsb.thoi_gian_tao <= :endDate) "
			+ "AND (:patientName IS NULL OR bn.ten LIKE %:patientName%) "
			+ "AND (:doctorName IS NULL OR bs.ten LIKE %:doctorName%) "
			+ "AND (:phoneNumber IS NULL OR bn.dien_thoai LIKE %:phoneNumber%)", countQuery = "SELECT COUNT(*) FROM ho_so_benh hsb "
					+ "JOIN benh_nhan bn ON hsb.benh_nhan_id = bn.benh_nhan_id "
					+ "JOIN bac_si bs ON hsb.bac_si_id = bs.bac_si_id "
					+ "WHERE (:startDate IS NULL OR hsb.thoi_gian_tao >= :startDate) "
					+ "AND (:endDate IS NULL OR hsb.thoi_gian_tao <= :endDate) "
					+ "AND (:patientName IS NULL OR bn.ten LIKE %:patientName%) "
					+ "AND (:doctorName IS NULL OR bs.ten LIKE %:doctorName%) "
					+ "AND (:phoneNumber IS NULL OR bn.dien_thoai LIKE %:phoneNumber%)", nativeQuery = true)
	Page<HoSoBenh> findMedicalHistoryWithFilters(@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate, @Param("patientName") String patientName,
			@Param("doctorName") String doctorName, @Param("phoneNumber") String phoneNumber, Pageable pageable);

	List<HoSoBenh> findByHoSoIdIn(List<String> hoSoIds);

	@Query("SELECT CAST(hs.slotThoiGian.lichKhamBenh.ngayThangNam AS string) AS timePeriod, "
			+ "COUNT(DISTINCT hs.benhNhan.id) AS totalPatients, "
			+ "COUNT(DISTINCT CASE WHEN visitCounts.visitCount > 1 THEN hs.benhNhan.id ELSE NULL END) AS revisitPatients, "
			+ "COUNT(hs) AS totalVisits " + "FROM HoSoBenh hs "
			+ "LEFT JOIN (SELECT hs2.benhNhan.id AS benhNhanId, COUNT(hs2) AS visitCount "
			+ "           FROM HoSoBenh hs2 "
			+ "           WHERE (:date IS NULL OR hs2.slotThoiGian.lichKhamBenh.ngayThangNam = :date) "
			+ "           AND hs2.slotThoiGian IS NOT NULL AND hs2.slotThoiGian.lichKhamBenh IS NOT NULL "
			+ "           GROUP BY hs2.benhNhan.id) visitCounts ON hs.benhNhan.id = visitCounts.benhNhanId "
			+ "WHERE (:date IS NULL OR hs.slotThoiGian.lichKhamBenh.ngayThangNam = :date) "
			+ "AND hs.slotThoiGian IS NOT NULL AND hs.slotThoiGian.lichKhamBenh IS NOT NULL "
			+ "GROUP BY CAST(hs.slotThoiGian.lichKhamBenh.ngayThangNam AS string)")
	List<Object[]> findRevisitRateByDay(@Param("date") LocalDate date);

	@Query("SELECT CONCAT(CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "LPAD(CAST(MONTH(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0')) AS timePeriod, "
	        + "COUNT(DISTINCT hs.benhNhan.id) AS totalPatients, "
	        + "COUNT(DISTINCT CASE WHEN visitCounts.visitCount > 1 THEN hs.benhNhan.id ELSE NULL END) AS revisitPatients, "
	        + "COUNT(hs) AS totalVisits "
	        + "FROM HoSoBenh hs "
	        + "LEFT JOIN (SELECT hs2.benhNhan.id AS benhNhanId, COUNT(hs2) AS visitCount, "
	        + "                  CONCAT(CAST(YEAR(hs2.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "                         LPAD(CAST(MONTH(hs2.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0')) AS hs2TimePeriod "
	        + "           FROM HoSoBenh hs2 "
	        + "           WHERE hs2.slotThoiGian IS NOT NULL AND hs2.slotThoiGian.lichKhamBenh IS NOT NULL "
	        + "           GROUP BY hs2.benhNhan.id, "
	        + "                    CONCAT(CAST(YEAR(hs2.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "                           LPAD(CAST(MONTH(hs2.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0'))) visitCounts "
	        + "ON hs.benhNhan.id = visitCounts.benhNhanId "
	        + "AND CONCAT(CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "           LPAD(CAST(MONTH(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0')) = visitCounts.hs2TimePeriod "
	        + "WHERE (:month IS NULL OR "
	        + "       CONCAT(CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "              LPAD(CAST(MONTH(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0')) = :month) "
	        + "AND hs.slotThoiGian IS NOT NULL AND hs.slotThoiGian.lichKhamBenh IS NOT NULL "
	        + "GROUP BY CONCAT(CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), '-', "
	        + "                LPAD(CAST(MONTH(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string), 2, '0'))")
	List<Object[]> findRevisitRateByMonth(@Param("month") String month);

	@Query("SELECT CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string) AS timePeriod, "
			+ "COUNT(DISTINCT hs.benhNhan.id) AS totalPatients, "
			+ "COUNT(DISTINCT CASE WHEN visitCounts.visitCount > 1 THEN hs.benhNhan.id ELSE NULL END) AS revisitPatients, "
			+ "COUNT(hs) AS totalVisits " + "FROM HoSoBenh hs "
			+ "LEFT JOIN (SELECT hs2.benhNhan.id AS benhNhanId, COUNT(hs2) AS visitCount "
			+ "           FROM HoSoBenh hs2 "
			+ "           WHERE (:year IS NULL OR CAST(YEAR(hs2.slotThoiGian.lichKhamBenh.ngayThangNam) AS string) = :year) "
			+ "           AND hs2.slotThoiGian IS NOT NULL AND hs2.slotThoiGian.lichKhamBenh IS NOT NULL "
			+ "           GROUP BY hs2.benhNhan.id) visitCounts ON hs.benhNhan.id = visitCounts.benhNhanId "
			+ "WHERE (:year IS NULL OR CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string) = :year) "
			+ "AND hs.slotThoiGian IS NOT NULL AND hs.slotThoiGian.lichKhamBenh IS NOT NULL "
			+ "GROUP BY CAST(YEAR(hs.slotThoiGian.lichKhamBenh.ngayThangNam) AS string)")
	List<Object[]> findRevisitRateByYear(@Param("year") String year);
}
