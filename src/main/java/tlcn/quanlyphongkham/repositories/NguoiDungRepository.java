package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tlcn.quanlyphongkham.entities.NguoiDung;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    NguoiDung findByTenDangNhap(String tenDangNhap);
    NguoiDung findByToken(String token);
    NguoiDung findByEmail(String email);
    
}
