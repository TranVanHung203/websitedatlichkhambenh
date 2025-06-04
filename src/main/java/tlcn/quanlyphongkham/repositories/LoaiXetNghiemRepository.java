package tlcn.quanlyphongkham.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import tlcn.quanlyphongkham.entities.LoaiXetNghiem;

public interface LoaiXetNghiemRepository extends JpaRepository<LoaiXetNghiem, Long> {
	
    Page<LoaiXetNghiem> findByTenXetNghiemContainingIgnoreCase(String ten, Pageable pageable);

}