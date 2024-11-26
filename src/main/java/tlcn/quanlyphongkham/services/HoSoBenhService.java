package tlcn.quanlyphongkham.services;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.dtos.BenhNhanOfTaoDonThuocDTO;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamDTO;
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

	public List<BenhNhanOfTaoDonThuocDTO> getBenhNhanInfoByDienThoai(String dienThoai) {
		List<Object[]> results = benhNhanRepository.findBenhNhanInfoByDienThoai(dienThoai);
		return results.stream().map(obj -> new BenhNhanOfTaoDonThuocDTO((String) obj[0], // benhnhanid
				(String) obj[1], // ten
				((java.sql.Date) obj[2]).toLocalDate(), // Chuyển đổi sang LocalDate
				(String) obj[3] // gioitinh
		)).collect(Collectors.toList());
	}


	public List<HoSoBenhDTO> getHoSoBenhWithDonThuocByDateRangeAndDoctor(
	        LocalDateTime startDateTime,
	        LocalDateTime endDateTime,
	        String bacSiId) {

	    // Định dạng thời gian theo yêu cầu
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	    // Lấy dữ liệu thô từ repository
	    List<Object[]> rawResults = hoSoBenhRepository.findHoSoBenhWithGroupedThuoc(startDateTime, endDateTime, bacSiId);

	    // Chuyển đổi Object[] sang HoSoBenhDTO
	    return rawResults.stream().map(obj -> {
	        String hoSoId = (String) obj[0];
	        String tenBenhNhan = (String) obj[1];
	        String chanDoan = (String) obj[2];
	        LocalDateTime thoiGianTaoRaw = LocalDateTime.parse((String) obj[3], formatter); // Parse từ String sang LocalDateTime nếu cần
	        String thoiGianTao = thoiGianTaoRaw.format(formatter); // Convert sang String định dạng
	        String tenThuoc = (String) obj[4];

	        // Trả về DTO
	        return new HoSoBenhDTO(hoSoId, tenBenhNhan, chanDoan, thoiGianTao, tenThuoc);
	    }).collect(Collectors.toList());
	}




	// Ánh xạ thủ công trong Service
	public List<LichSuKhamDTO> getLichSuKhamByBenhNhanId(String benhNhanId) {
	    List<Object[]> rawData = hoSoBenhRepository.findLichSuKhamByBenhNhanIdRaw(benhNhanId);
	    return rawData.stream()
	                  .map(obj -> new LichSuKhamDTO(
	                      (String) obj[0],  // tenBacSi
	                      (String) obj[1],  // ngayKham
	                      (String) obj[2],  // chanDoan
	                      (String) obj[3],  // thuoc
	                      (String) obj[4],  // lieu
	                      (String) obj[5]   // tanSuat
	                  ))
	                  .toList();
	}
	
}
