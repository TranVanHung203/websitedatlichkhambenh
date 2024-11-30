package tlcn.quanlyphongkham.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;

@Repository
public interface BacSiRepository extends JpaRepository<BacSi, String> {

	List<BacSi> findByChuyenKhoa(ChuyenKhoa chuyenKhoa);

	BacSi findByBacSiId(String bacSiId);

	Page<BacSi> findAll(Pageable pageable);
	
	
	Page<BacSi> findByDienThoaiContaining(String phone, Pageable pageable);

}
