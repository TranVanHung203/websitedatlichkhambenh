package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.security.CustomUserDetails;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.SlotThoiGianService;

@Controller
public class NhanVienController {

	@Autowired
	private BenhNhanService benhNhanService;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private ChuyenKhoaService chuyenKhoaService;

	@Autowired
	private BacSiService bacSiService;

	@Autowired
	private LichKhamBenhService lichKhamBenhService;

	@Autowired
	SlotThoiGianService slotThoiGianService;
	
	
	
	public String getNguoiDungId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = (CustomUserDetails) principal;
				return customUserDetails.getNguoiDungId(); // Get the NguoiDungId as String

			} else
				return null;
		} else
			return null;
	}
	
	String nguoiDungId;
	

	@GetMapping("/nhanvien/dangkylichkham")
	public String registerSchedule(Model model) {
		// Fetch list of specialties and pass it to the view
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);
		return "nhanvien/dangkylichkhamchobenhnhan/dkbuoc1"; // Return to Step 1 view
	}

	@PostMapping("/nhanvien/dangkylichkham/doctor")
	public String getDoctorsBySpecialty(@RequestParam("service") String serviceId, Model model) {

		List<BacSiDTO> bacSiList = bacSiService.getBacSiByChuyenKhoa(serviceId);
		model.addAttribute("bacSiList", bacSiList);
		model.addAttribute("selectedService", serviceId); // Add selected service to model
		return "nhanvien/dangkylichkhamchobenhnhan/dkbuoc1"; // Return to the same view with updated doctors list
	}

	@PostMapping("/nhanvien/dangkylichkham/next")
	public String proceedToStep2(@RequestParam("doctor") String doctorId, @RequestParam("service") String serviceId,
			Model model) {

		model.addAttribute("doctorId", doctorId);

		return "/nhanvien/dangkylichkhamchobenhnhan/dkbuoc2";
	}

	public LocalDate parseDate(String dateStr) {
	    if (dateStr != null && !dateStr.isEmpty()) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
	        return LocalDate.parse(dateStr, formatter);
	    }
	    return null;
	}

	@PostMapping("/nhanvien/dangkylichkham/getAvailableSlots")
	@ResponseBody
	public Map<String, Object> getAvailableSlots(@RequestBody Map<String, String> requestData) {
		String doctorId = requestData.get("doctorId");
		String date = requestData.get("date");

		LocalDate selectedDate = parseDate(date);

		List<LichKhamBenh> lichKhamBenhList = lichKhamBenhService.getLichKhamBenhByNgayAndBacSi(doctorId, selectedDate);

		List<String> caList = new ArrayList<>();
		if (lichKhamBenhList != null && !lichKhamBenhList.isEmpty()) {
			for (LichKhamBenh lichKhamBenh : lichKhamBenhList) {
				caList.add(lichKhamBenh.getCa());

			}
		}

		// Danh sách kết quả trả về
		Map<String, Object> result = new HashMap<>();
		result.put("doctorId", doctorId);
		result.put("date", selectedDate);
		result.put("caList", caList);

		// Lấy danh sách các slot đã được đặt cho bác sĩ
		List<String> bookedSlots = new ArrayList<>();
		for (LichKhamBenh lichKhamBenh : lichKhamBenhList) {
			List<SlotThoiGian> slots = slotThoiGianService.findByLichKhamBenh(lichKhamBenh.getMaLichKhamBenh());

			for (SlotThoiGian slot : slots) {
				System.out.println(slot.getThoiGianBatDau().toString() + "hihi");
				bookedSlots.add(slot.getThoiGianBatDau().toString()); // Thêm giờ bắt đầu của slot vào danh sách
			}
		}

		// Trả về thông tin slot đã đặt và các ca khám
		result.put("bookedSlots", bookedSlots);

		return result;
	}

	@GetMapping("/nhanvien/dangkylichkham/buoc3")
	public String proceedToStep3(@RequestParam("doctorId") String doctorId,
			@RequestParam("selectedDate") String selectedDate, @RequestParam("selectedTime") String selectedTime,
			@RequestParam("ca") String ca, Model model) {

		// Fetch the doctor information using doctorId
		BacSi doctor = bacSiService.findById(doctorId);

		// Add the data to the model to be rendered in the view
		model.addAttribute("doctor", doctor);
		model.addAttribute("date", selectedDate);
		model.addAttribute("time", selectedTime);
		model.addAttribute("ca", ca);

		// Return the view for Step 3
		return "/nhanvien/dangkylichkhamchobenhnhan/dkbuoc3";
	}

	public LocalTime calculateStartTime(String selectedTime) {
		// Chuyển đổi selectedTime (dạng String) thành LocalTime
		return LocalTime.parse(selectedTime); // Giả sử selectedTime là một chuỗi như "07:30", "13:00", "17:00"
	}

	public LocalTime calculateEndTime(String selectedTime) {
		// Chuyển đổi selectedTime (dạng String) thành LocalTime
		LocalTime startTime = LocalTime.parse(selectedTime); // Giả sử selectedTime là "07:30", "13:00", "17:00"

		// Cộng thêm 30 phút vào thời gian bắt đầu
		return startTime.plusMinutes(30); // Cộng thêm 30 phút vào giờ bắt đầu
	}



	@GetMapping("/nhanvien/dangkylichkham/buoc4")
	public String showPatientForm(Model model) {
	    model.addAttribute("benhNhan", new BenhNhan());
	    return "/nhanvien/dangkylichkhamchobenhnhan/dkbuoc4"; // View name phải khớp với tên tệp
	}
	
	@PostMapping("/nhanvien/dangkylichkham/confirm")
	@ResponseBody
	public ResponseEntity<Map<String, String>> confirmBooking(@RequestBody Map<String, String> requestData) {
	    String doctorId = requestData.get("doctorId");
	    String selectedDate = requestData.get("selectedDate");
	    String selectedTime = requestData.get("selectedTime");
	    String ca = requestData.get("selectedCa");

	    // Thông tin bệnh nhân từ yêu cầu
	    String patientName = requestData.get("patientName");
	    String patientPhone = requestData.get("patientPhone");
	    String patientAddress = requestData.get("patientAddress");
	    String patientGender = requestData.get("patientGender");
	    String patientBirthDate = requestData.get("patientBirthDate");

	    LocalDate selectedDates;
	    try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        selectedDates = LocalDate.parse(selectedDate, formatter);
	    } catch (DateTimeParseException e) {
	        DateTimeFormatter fallbackFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
	        selectedDates = LocalDate.parse(selectedDate, fallbackFormatter);
	    }

	    // Kiểm tra xem bệnh nhân đã tồn tại chưa
	    BenhNhan existingPatient = benhNhanService.findByPhone(patientPhone);
	    BenhNhan benhnhan;
	    if (existingPatient == null) {
	        // Tạo bệnh nhân mới
	        benhnhan = new BenhNhan();
	        benhnhan.setBenhNhanId(UUID.randomUUID().toString());
	        benhnhan.setTen(patientName);
	        benhnhan.setDienThoai(patientPhone);
	        benhnhan.setDiaChi(patientAddress);
	        benhnhan.setGioiTinh(patientGender);
	        benhnhan.setNgaySinh(LocalDate.parse(patientBirthDate, DateTimeFormatter.ISO_DATE));

	        // Lưu thông tin bệnh nhân vào cơ sở dữ liệu
	        benhNhanService.save(benhnhan);
	    } else {
	        // Sử dụng thông tin bệnh nhân đã tồn tại
	        benhnhan = existingPatient;
	    }

	    // Tìm lịch khám bệnha
	    MaLichKhamBenhDTO lichKhamBenh = lichKhamBenhService.findByDoctorIdAndDateAndCa(doctorId, selectedDates, ca);
	    
	    
	    Optional<LichKhamBenh> lichkham = lichKhamBenhService.findById(lichKhamBenh.getMaLichKhamBenh());

	    SlotThoiGian existingSlot = slotThoiGianService.findExist(
	        calculateStartTime(selectedTime), 
	        benhnhan.getBenhNhanId(),
	        lichKhamBenh.getMaLichKhamBenh()
	    );

	    if (lichKhamBenh != null && existingSlot == null) {
	        // Tạo slot thời gian mới
	        SlotThoiGian slotThoiGian = new SlotThoiGian();
	        slotThoiGian.setSlotId(UUID.randomUUID().toString());
	        slotThoiGian.setLichKhamBenh(lichkham.get());
	        slotThoiGian.setThoiGianBatDau(calculateStartTime(selectedTime));
	        slotThoiGian.setThoiGianKetThuc(calculateEndTime(selectedTime));
	        slotThoiGian.setTrangThai("pending");
	        slotThoiGian.setBenhNhan(benhnhan);

	        // Lưu slot vào cơ sở dữ liệu
	        slotThoiGianService.save(slotThoiGian);

	        // Phản hồi thành công
	        Map<String, String> response = new HashMap<>();
	        response.put("status", "success");
	        response.put("message", "Lịch khám đã được xác nhận thành công!");
	        return ResponseEntity.ok(response);
	    } else {
	        // Phản hồi thất bại
	        Map<String, String> response = new HashMap<>();
	        response.put("status", "failure");
	        response.put("message", "Khung giờ này đã được đặt, vui lòng chọn khung giờ khác.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}


	
}
