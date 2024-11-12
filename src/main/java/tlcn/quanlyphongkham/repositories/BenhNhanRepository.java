package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import tlcn.quanlyphongkham.entities.BenhNhan;

public interface BenhNhanRepository extends JpaRepository<BenhNhan, String> {
	 BenhNhan findByNguoiDung_NguoiDungId(String nguoiDungId);
	 

	List<BenhNhan> findByDienThoai(String dienThoai);


}
