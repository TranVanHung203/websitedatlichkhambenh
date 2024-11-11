package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import tlcn.quanlyphongkham.entities.SlotThoiGian;

public interface SlotThoiGianRepository extends JpaRepository<SlotThoiGian, String> {
    List<SlotThoiGian> findByLichKhamBenh_MaLichKhamBenh(String maLichKhamBenh);
}