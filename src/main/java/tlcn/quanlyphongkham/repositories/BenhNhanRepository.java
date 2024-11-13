package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.Thuoc;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, String> {
	 BenhNhan findByNguoiDung_NguoiDungId(String nguoiDungId);
	 

	 
	 @Query(value = "SELECT benh_nhan_id, ten, ngay_sinh, gioi_tinh FROM benh_nhan WHERE dien_thoai = :dienThoai", nativeQuery = true)
	 List<Object[]> findBenhNhanInfoByDienThoai(@Param("dienThoai") String dienThoai);



	
	

}
