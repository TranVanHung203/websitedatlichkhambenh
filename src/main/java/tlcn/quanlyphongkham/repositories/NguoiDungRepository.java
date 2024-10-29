package tlcn.quanlyphongkham.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.NguoiDung;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
	@Query(value = "SELECT * FROM nguoi_dung WHERE ten_dang_nhap = :tenDangNhap", nativeQuery = true)
    NguoiDung findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);
	
	
    NguoiDung findByToken(String token);
    NguoiDung findByEmail(String email);
    NguoiDung findByTenDangNhapOrEmail(String tenDangNhap, String email);
    NguoiDung findByNguoiDungId(String nguoiDungId);
    // Check if email exists for any user except the one with the given ID
    boolean existsByEmailAndNguoiDungIdNot(String email, String nguoiDungId);

    // Check if username exists for any user except the one with the given ID
    boolean existsByTenDangNhapAndNguoiDungIdNot(String tenDangNhap, String nguoiDungId);

    boolean existsByEmail(String email);
    boolean existsByTenDangNhap(String tenDangNhap);

}
