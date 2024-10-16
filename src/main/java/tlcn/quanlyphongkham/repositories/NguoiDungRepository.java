package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import tlcn.quanlyphongkham.entities.NguoiDung;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    // Các phương thức tùy chỉnh nếu cần
}