package tlcn.quanlyphongkham.repositories;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.SlotThoiGian;

public interface SlotThoiGianRepository extends JpaRepository<SlotThoiGian, String> {
	List<SlotThoiGian> findByLichKhamBenh_MaLichKhamBenh(String maLichKhamBenh);

	@Query(value = "SELECT * FROM slot_thoi_gian st "
			+ "WHERE CAST(st.thoi_gian_bat_dau AS TIME) = :calculateStartTime " + "AND st.benh_nhan_id = :benhNhanId "
			+ "AND st.ma_lich_kham_benh = :maLichKhamBenh", nativeQuery = true)
	SlotThoiGian findExist(@Param("calculateStartTime") String calculateStartTime,
			@Param("benhNhanId") String benhNhanId, @Param("maLichKhamBenh") String maLichKhamBenh);

}