package tlcn.quanlyphongkham.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamDTO;

@Repository
public interface HoSoBenhRepository extends JpaRepository<HoSoBenh, String> {

	@Query(value = """
	        SELECT
	            hs.ho_so_id AS hoSoId,
	            bn.ten AS tenBenhNhan,
	            hs.chan_doan AS chanDoan,
	            DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d %H:%i:%s') AS thoiGianTao,
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
	        """,
	        countQuery = """
	        SELECT COUNT(DISTINCT hs.ho_so_id)
	        FROM ho_so_benh hs
	        WHERE hs.bac_si_id = :bacSiId
	        AND hs.thoi_gian_tao BETWEEN :startDateTime AND :endDateTime
	        """, nativeQuery = true)
	Page<Object[]> findHoSoBenhWithGroupedThuoc(@Param("startDateTime") LocalDateTime startDateTime,
	                                           @Param("endDateTime") LocalDateTime endDateTime,
	                                           @Param("bacSiId") String bacSiId,
	                                           Pageable pageable);


	
	
	
	

	@Query(value = """
	        SELECT
	            bs.ten AS tenBacSi,
	            DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') AS ngayKham,
	            hs.chan_doan AS chanDoan,
	            GROUP_CONCAT(CONCAT('- ', t.ten) SEPARATOR '\\n') AS thuoc,
	            GROUP_CONCAT(dtt.lieu SEPARATOR '\\n') AS lieu,
	            GROUP_CONCAT(dtt.tan_suat SEPARATOR '\\n') AS tanSuat,
	            SUM(dtt.so_luong * t.gia) AS tongTienThuoc,
	            GROUP_CONCAT(CAST(dtt.so_luong AS CHAR) SEPARATOR '\\n') AS soLuong
	        FROM ho_so_benh hs
	        JOIN bac_si bs ON hs.bac_si_id = bs.bac_si_id
	        LEFT JOIN don_thuoc dt ON hs.ho_so_id = dt.ho_so_id
	        LEFT JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
	        LEFT JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
	        WHERE hs.benh_nhan_id = :benhNhanId
	        GROUP BY hs.ho_so_id, bs.ten, hs.thoi_gian_tao, hs.chan_doan
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
	            SUM(dtt.so_luong * t.gia) AS tongTienThuoc,
	            GROUP_CONCAT(CAST(dtt.so_luong AS CHAR) SEPARATOR '\\n') AS soLuong
	        FROM ho_so_benh hs
	        JOIN bac_si bs ON hs.bac_si_id = bs.bac_si_id
	        LEFT JOIN don_thuoc dt ON hs.ho_so_id = dt.ho_so_id
	        LEFT JOIN don_thuoc_thuoc dtt ON dt.don_thuoc_id = dtt.don_thuoc_id
	        LEFT JOIN thuoc t ON dtt.thuoc_id = t.thuoc_id
	        WHERE hs.benh_nhan_id = :benhNhanId
	        AND DATE_FORMAT(hs.thoi_gian_tao, '%Y-%m-%d') = :date
	        GROUP BY hs.ho_so_id, bs.ten, hs.thoi_gian_tao, hs.chan_doan
	        ORDER BY hs.thoi_gian_tao DESC
	        """, nativeQuery = true)
	Page<Object[]> findLichSuKhamByBenhNhanIdAndDateRaw(@Param("benhNhanId") String benhNhanId,
	                                                   @Param("date") String date,
	                                                   Pageable pageable);


}
