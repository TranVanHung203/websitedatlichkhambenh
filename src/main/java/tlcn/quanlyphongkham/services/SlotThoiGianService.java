package tlcn.quanlyphongkham.services;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.repositories.SlotThoiGianRepository;

@Service
public class SlotThoiGianService {

    @Autowired
    private SlotThoiGianRepository slotThoiGianRepository;

    

	public List<SlotThoiGian> findByLichKhamBenh(String lichKhamBenh) {
	
		return slotThoiGianRepository.findByLichKhamBenh_MaLichKhamBenh(lichKhamBenh);
	}



	public void save(SlotThoiGian slotThoiGian) {
		slotThoiGianRepository.save(slotThoiGian);
		
	}



	public SlotThoiGian findExist(LocalTime calculateStartTime, String benhNhanId, String maLichKhamBenh) {
		
		
		String formattedTime = calculateStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		
		// Gọi repository
		return slotThoiGianRepository.findExist(formattedTime, benhNhanId, maLichKhamBenh);

	}



	public void deleteById(String slotId) {
		slotThoiGianRepository.deleteById(slotId);
		
	}
}
