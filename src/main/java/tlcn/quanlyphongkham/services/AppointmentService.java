package tlcn.quanlyphongkham.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.repositories.AppointmentRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public boolean updateStatus(String slotId, String date, String time, String status) {
        // Assuming you have an Appointment entity and repository
        Optional<SlotThoiGian> slot = appointmentRepository.findBySlotId(slotId); // Find slot by slotId
        
        if (slot.isPresent()) {
        	SlotThoiGian s = slot.get();
            s.setTrangThai(status); // Set the new status
            appointmentRepository.save(s); // Save the updated status
            return true;
        } else {
            return false;
        }
    }
}

