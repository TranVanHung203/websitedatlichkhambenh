package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.dtos.BacSiInfo;
import tlcn.quanlyphongkham.dtos.BenhNhanInfo;
import tlcn.quanlyphongkham.entities.TinNhan;

public interface TinNhanRepository extends JpaRepository<TinNhan, String> {
	List<TinNhan> findByBenhNhanIdAndBacSiIdOrderByThoiGianAsc(String benhNhanId, String bacSiId);

	@Query(value = "SELECT b.bac_si_id as bacSiId, b.ten as ten, b.dien_thoai as dienThoai, b.url_avatar as urlAvatar FROM bac_si b WHERE b.bac_si_id IN (SELECT DISTINCT tn.bac_si_id FROM tin_nhan tn WHERE tn.benh_nhan_id = :benhNhanId)", nativeQuery = true)
	List<BacSiInfo> findBacSiInfoByBenhNhanId(@Param("benhNhanId") String benhNhanId);

	@Query(value = "SELECT bn.benh_nhan_id as id, bn.ten as ten, bn.dien_thoai as dienThoai " + "FROM benh_nhan bn "
			+ "WHERE bn.benh_nhan_id IN (" + "   SELECT DISTINCT tn.benh_nhan_id " + "   FROM tin_nhan tn "
			+ "   WHERE tn.bac_si_id = :bacSiId)", nativeQuery = true)
	List<BenhNhanInfo> findBenhNhanInfoByBacSiId(@Param("bacSiId") String bacSiId);

}
