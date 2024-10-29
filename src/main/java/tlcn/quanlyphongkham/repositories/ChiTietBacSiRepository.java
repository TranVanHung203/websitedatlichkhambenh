package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.ChiTietBacSi;

@Repository
public interface ChiTietBacSiRepository extends JpaRepository<ChiTietBacSi, Long> {
}

