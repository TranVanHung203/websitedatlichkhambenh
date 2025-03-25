package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.Thuoc;

public interface ThuocRepository extends JpaRepository<Thuoc, Long> {

	@Query(value = "SELECT thuoc_id,gia,mo_ta,ten,so_luong,nha_cung_cap FROM thuoc WHERE ten LIKE %:ten%", nativeQuery = true)
	List<Thuoc> findByTenContaining(@Param("ten") String ten);

	List<Thuoc> findByTen(String ten);

	@Query("SELECT t FROM Thuoc t WHERE t.ten LIKE %:ten%")
	Page<Thuoc> findByTenContaining(@Param("ten") String ten, Pageable pageable);
	
	@Query("SELECT t FROM Thuoc t WHERE t.nhaCungCap LIKE %:nhaCungCap%")
    Page<Thuoc> findByNhaCungCapContaining(@Param("nhaCungCap") String nhaCungCap, Pageable pageable);

}
