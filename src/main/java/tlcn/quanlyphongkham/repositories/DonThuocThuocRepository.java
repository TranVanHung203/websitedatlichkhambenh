package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.DonThuocThuoc;

public interface DonThuocThuocRepository extends JpaRepository<DonThuocThuoc, Long> { 
	
	 
}
