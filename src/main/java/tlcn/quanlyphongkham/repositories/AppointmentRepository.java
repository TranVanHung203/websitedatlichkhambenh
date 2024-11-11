package tlcn.quanlyphongkham.repositories;

import java.util.Optional;

import org.aspectj.weaver.patterns.ConcreteCflowPointcut.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import tlcn.quanlyphongkham.entities.SlotThoiGian;

public interface AppointmentRepository extends JpaRepository<SlotThoiGian, String> {

    Optional<SlotThoiGian> findBySlotId(String slotId);  // Assuming slotId is the primary key
}