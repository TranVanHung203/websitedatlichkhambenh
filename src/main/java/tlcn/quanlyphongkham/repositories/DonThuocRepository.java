package tlcn.quanlyphongkham.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tlcn.quanlyphongkham.entities.DonThuoc;

public interface DonThuocRepository extends JpaRepository<DonThuoc, String> {
	
	

}

