package tlcn.quanlyphongkham.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.XetNghiem;

public interface XetNghiemRepository extends JpaRepository<XetNghiem, Long> {
	List<XetNghiem> findByHoSoBenhHoSoId(String hoSoId);

	@Query("SELECT xn.loaiXetNghiem.tenXetNghiem, COUNT(xn) " + "FROM XetNghiem xn "
			+ "WHERE (:date IS NULL OR xn.hoSoBenh.slotThoiGian.lichKhamBenh.ngayThangNam = :date) "
			+ "GROUP BY xn.loaiXetNghiem.tenXetNghiem")
	List<Object[]> findTestUsageByDay(@Param("date") LocalDate date);

	@Query("SELECT xn.loaiXetNghiem.tenXetNghiem, COUNT(xn), " +
		       "MONTH(xn.hoSoBenh.slotThoiGian.lichKhamBenh.ngayThangNam) " +
		       "FROM XetNghiem xn " +
		       "WHERE (:year IS NULL OR CAST(YEAR(xn.hoSoBenh.slotThoiGian.lichKhamBenh.ngayThangNam) AS string) = :year) " +
		       "GROUP BY xn.loaiXetNghiem.tenXetNghiem, MONTH(xn.hoSoBenh.slotThoiGian.lichKhamBenh.ngayThangNam)")
		List<Object[]> findTestUsageByYearAndMonth(@Param("year") String year);

	@Query("SELECT xn.loaiXetNghiem.tenXetNghiem, COUNT(xn) " + "FROM XetNghiem xn "
			+ "WHERE (:year IS NULL OR CAST(YEAR(xn.hoSoBenh.slotThoiGian.lichKhamBenh.ngayThangNam) AS string) = :year) "
			+ "GROUP BY xn.loaiXetNghiem.tenXetNghiem")
	List<Object[]> findTestUsageByYear(@Param("year") String year);

    @Query("SELECT x FROM XetNghiem x " +
            "JOIN x.hoSoBenh h " +
            "JOIN h.benhNhan b " +
            "WHERE (:tenBenhNhan IS NULL OR LOWER(b.ten) LIKE :tenBenhNhan) " +
            "AND (:dienThoai IS NULL OR b.dienThoai LIKE :dienThoai) " +
            "AND (:trangThai IS NULL OR x.trangThai = :trangThai) " +
            "AND (:thoiGianTao IS NULL OR CAST(h.thoiGianTao AS date) = :thoiGianTao)")
     List<XetNghiem> findByFilters(
         @Param("tenBenhNhan") String tenBenhNhan,
         @Param("dienThoai") String dienThoai,
         @Param("trangThai") String trangThai,
         @Param("thoiGianTao") LocalDate thoiGianTao
     );
 
}