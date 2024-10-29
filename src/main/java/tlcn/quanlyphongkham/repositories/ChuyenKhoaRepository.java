package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tlcn.quanlyphongkham.entities.ChuyenKhoa;

@Repository
public interface ChuyenKhoaRepository extends JpaRepository<ChuyenKhoa, String> {
}
