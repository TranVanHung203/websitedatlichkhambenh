package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.Thuoc;

public interface ThuocRepository extends JpaRepository<Thuoc, Long> {

	 @Query(value = "SELECT thuoc_id,gia,mo_ta,ten FROM Thuoc WHERE ten LIKE %:ten%", nativeQuery = true)
	   List<Thuoc> findByTenContaining(@Param("ten") String ten);

	List<Thuoc> findByTen(String ten);
}

