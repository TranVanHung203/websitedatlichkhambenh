package tlcn.quanlyphongkham.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.DonThuocRepository;
import tlcn.quanlyphongkham.repositories.DonThuocThuocRepository;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.ThuocRepository;

@Service
public class HoSoBenhService {
	@Autowired
	private HoSoBenhRepository hoSoBenhRepository;
	@Autowired
	private DonThuocRepository donThuocRepository;
	@Autowired
	private ThuocRepository thuocRepository;
	@Autowired
	private DonThuocThuocRepository donThuocThuocRepository;

	@Autowired
	private BenhNhanRepository benhNhanRepository;

	public BenhNhan findPatientById(String patientId) {
		return benhNhanRepository.findById(patientId).orElse(null);
	}

//    public HoSoBenh findHoSoById(String hoSoId) {
//        return hoSoBenhRepository.findById(hoSoId).orElse(null);
//    }
	public HoSoBenh save(HoSoBenh hoSoBenh) {
		return hoSoBenhRepository.save(hoSoBenh);
	}

	// Tìm hồ sơ bệnh theo ID
	public HoSoBenh findById(String hoSoId) {
		return hoSoBenhRepository.findById(hoSoId).orElse(null);
	}

	public List<BenhNhan> findPatientsByPhoneNumber(String phone) {
		return benhNhanRepository.findByDienThoai(phone);
	}

	 public List<HoSoBenhDTO> getHoSoBenhWithDonThuocByDateRangeAndDoctor(LocalDateTime startDateTime, LocalDateTime endDateTime, String bacSiId) {
	        return hoSoBenhRepository.findHoSoBenhWithDetails(startDateTime, endDateTime, bacSiId);
	    }


}
