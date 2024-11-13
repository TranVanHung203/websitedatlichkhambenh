package tlcn.quanlyphongkham.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;

@Repository
public interface ChiTietBacSiRepository extends JpaRepository<ChiTietBacSi, Long> {

	ChiTietBacSi findByBacSiBacSiId(String bacSiId);

	Optional<ChiTietBacSi> findByBacSi(BacSi bacsi);
}

