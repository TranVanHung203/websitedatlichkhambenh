package tlcn.quanlyphongkham.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<HoSoBenh, String> {


}