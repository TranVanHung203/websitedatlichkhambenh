package tlcn.quanlyphongkham.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.ChuyenKhoa;

@Repository
public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, String> {

	ChuyenKhoa getChuyenKhoaBychuyenKhoaId(String chuyenKhoaId);

	@Query("SELECT ck FROM ChuyenKhoa ck WHERE ck.ten LIKE %:ten%")
	Page<ChuyenKhoa> findByTenContaining(@Param("ten") String ten, Pageable pageable);

}
