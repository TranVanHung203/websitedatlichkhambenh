package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.LichBacSiDTO;
import tlcn.quanlyphongkham.dtos.LichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamNhanVienDTO;
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
import tlcn.quanlyphongkham.services.HoSoBenhService;
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
    private HoSoBenhService hoSoBenhService;

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
	@ResponseBody
	public String getDoctorsBySpecialty(@RequestParam("service") String serviceId) {

		List<BacSiDTO> bacSiList = bacSiService.getBacSiByChuyenKhoa(serviceId);

		if (bacSiList == null || bacSiList.isEmpty()) {
			System.out.println("Không tìm thấy bác sĩ cho chuyên khoa: " + serviceId);
			return "<option value=''>Không có bác sĩ</option>";
		}

		StringBuilder options = new StringBuilder();
		for (BacSiDTO bacSi : bacSiList) {
			options.append("<option value='").append(bacSi.getId()).append("'>").append(bacSi.getTen())
					.append("</option>");
		}

		return options.toString();
	}

	@PostMapping("/nhanvien/dangkylichkham/next")
	public String proceedToStep2(@RequestParam("service") String serviceId, @RequestParam("doctor") String doctorId,
			Model model) {
		if (serviceId == null || serviceId.isEmpty()) {
			throw new IllegalArgumentException("Chuyên khoa không được để trống.");
		}
		if (doctorId == null || doctorId.isEmpty()) {
			throw new IllegalArgumentException("Bác sĩ không được để trống.");
		}

		model.addAttribute("doctorId", doctorId);
		model.addAttribute("serviceId", serviceId);

		return "nhanvien/dangkylichkhamchobenhnhan/dkbuoc2";
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
	public String showPatientForm(@RequestParam(value = "doctorId", required = false) String doctorId,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "ca", required = false) String ca,
			@RequestParam(value = "startTime", required = false) String startTime, Model model) {

		// Tạo đối tượng BenhNhan để binding với form
		model.addAttribute("benhNhan", new BenhNhan());

		// Truyền các tham số vào model để sử dụng trong view
		model.addAttribute("doctorId", doctorId);
		model.addAttribute("selectedDate", date);
		model.addAttribute("selectedCa", ca);
		model.addAttribute("selectedTime", startTime);

		return "/nhanvien/dangkylichkhamchobenhnhan/dkbuoc4";
	}

	@PostMapping("/nhanvien/dangkylichkham/confirm")
	@ResponseBody
	public ResponseEntity<Map<String, String>> confirmBooking(@RequestBody Map<String, String> requestData) {
		Map<String, String> response = new HashMap<>();
		try {
			String doctorId = requestData.get("doctorId");
			String selectedDate = requestData.get("selectedDate");
			String selectedTime = requestData.get("selectedTime");
			String ca = requestData.get("selectedCa");

			// Thông tin bệnh nhân
			String patientName = requestData.get("patientName");
			String patientPhone = requestData.get("patientPhone");
			String patientAddress = requestData.get("patientAddress");
			String patientGender = requestData.get("patientGender");
			String patientBirthDate = requestData.get("patientBirthDate");

			// Xử lý định dạng ngày tháng linh hoạt
			LocalDate selectedDates;
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
				selectedDates = LocalDate.parse(selectedDate, formatter);
			} catch (DateTimeParseException e) {
				response.put("status", "error");
				response.put("message", "Định dạng ngày không hợp lệ!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Kiểm tra bệnh nhân
			BenhNhan existingPatient = benhNhanService.findByPhone(patientPhone);
			BenhNhan benhnhan;
			if (existingPatient == null) {
				benhnhan = new BenhNhan();
				benhnhan.setBenhNhanId(UUID.randomUUID().toString());
				benhnhan.setTen(patientName);
				benhnhan.setDienThoai(patientPhone);
				benhnhan.setDiaChi(patientAddress);
				benhnhan.setGioiTinh(patientGender);
				benhnhan.setNgaySinh(LocalDate.parse(patientBirthDate, DateTimeFormatter.ISO_DATE));

				benhNhanService.save(benhnhan);
			} else {
				benhnhan = existingPatient;
			}

			// Tìm lịch khám bệnh
			MaLichKhamBenhDTO lichKhamBenh = lichKhamBenhService.findByDoctorIdAndDateAndCa(doctorId, selectedDates,
					ca);
			if (lichKhamBenh == null) {
				response.put("status", "error");
				response.put("message", "Không tìm thấy lịch khám!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			Optional<LichKhamBenh> lichkham = lichKhamBenhService.findById(lichKhamBenh.getMaLichKhamBenh());

			// Kiểm tra xem slot có tồn tại không
			SlotThoiGian existingSlot = slotThoiGianService.findExist(calculateStartTime(selectedTime),
					benhnhan.getBenhNhanId(), lichKhamBenh.getMaLichKhamBenh());

			if (existingSlot == null) {
				SlotThoiGian slotThoiGian = new SlotThoiGian();
				slotThoiGian.setSlotId(UUID.randomUUID().toString());
				slotThoiGian.setLichKhamBenh(lichkham.get());
				slotThoiGian.setThoiGianBatDau(calculateStartTime(selectedTime));
				slotThoiGian.setThoiGianKetThuc(calculateEndTime(selectedTime));
				slotThoiGian.setTrangThai("pending");
				slotThoiGian.setBenhNhan(benhnhan);

				slotThoiGianService.save(slotThoiGian);

				response.put("status", "success");
				response.put("message", "Lịch khám đã được xác nhận thành công!");
				return ResponseEntity.ok(response);
			} else {
				response.put("status", "failure");
				response.put("message", "Khung giờ này đã được đặt, vui lòng chọn khung giờ khác.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Có lỗi xảy ra: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

//	@GetMapping("/nhanvien/xemlichbacsi")
//	public String viewDoctorSchedule(Model model) {
//	    try {
//	        List<BacSi> bacSiList = bacSiService.getAllDoctors();
//	        if (bacSiList == null) {
//	            model.addAttribute("errorMessage", "Không thể tải danh sách bác sĩ.");
//	            return "error";
//	        }
//	        model.addAttribute("bacSiList", bacSiList);
//	        return "nhanvien/xemlichbacsi/xemlichbacsi";
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
//	        return "error";
//	    }
//	}
//
//
//	@GetMapping("/nhanvien/xemlichbacsi/schedule")
//	@ResponseBody
//	public List<LichBacSiDTO> getDoctorScheduleSlots(
//	        @RequestParam("doctorId") String doctorId,
//	        @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
//
//	    BacSi bacSi = bacSiService.findById(doctorId);
//	    if (bacSi == null) {
//	        return Collections.emptyList();
//	    }
//
//	    List<LichKhamBenh> lichList = lichKhamBenhService.getLichKhamBenhByBacSi(doctorId);
//
//	    if (date != null) {
//	        lichList = lichList.stream()
//	                .filter(lich -> lich.getNgayThangNam().isEqual(date))
//	                .collect(Collectors.toList());
//	    }
//
//	    List<LichBacSiDTO> result = new ArrayList<>();
//
//	    for (LichKhamBenh lich : lichList) {
//	        for (SlotThoiGian slot : lich.getSlotThoiGian()) {
//	            String trangThai = (slot.getTrangThai() != null && !slot.getTrangThai().trim().isEmpty())
//	                    ? "Đã đặt" : "Chưa đặt";
//
//	            LichBacSiDTO dto = new LichBacSiDTO();
//	            dto.setNgayThangNam(lich.getNgayThangNam());
//	            dto.setCaKham(lich.getCa());
//	            dto.setThoiGianBatDau(slot.getThoiGianBatDau().toString());
//	            dto.setThoiGianKetThuc(slot.getThoiGianKetThuc().toString());
//	            dto.setTrangThai(trangThai);
//
//	            result.add(dto);
//	        }
//	    }
//
//	    result.sort(Comparator
//	        .comparingInt((LichBacSiDTO dto) -> getCaPriority(dto.getCaKham()))
//	        .thenComparing(dto -> LocalTime.parse(dto.getThoiGianBatDau())));
//
//	    return result;
//	}
//
//	private int getCaPriority(String ca) {
//	    return switch (ca.toLowerCase()) {
//	        case "sáng" -> 1;
//	        case "chiều" -> 2;
//	        case "ngoài giờ" -> 3;
//	        default -> 4;
//	    };
//	}
	@GetMapping("/nhanvien/xemlichbacsi")
	public String viewDoctorSchedule(Model model) {
		try {
			// Lấy danh sách bác sĩ
			List<BacSi> bacSiList = bacSiService.getAllDoctors();
			if (bacSiList == null) {
				model.addAttribute("errorMessage", "Không thể tải danh sách bác sĩ.");
				return "error";
			}

			// Lấy danh sách chuyên khoa
			List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa(); // Giả sử bạn có service này
			if (chuyenKhoaList == null) {
				model.addAttribute("errorMessage", "Không thể tải danh sách chuyên khoa.");
				return "error";
			}

			model.addAttribute("bacSiList", bacSiList);
			model.addAttribute("chuyenKhoaList", chuyenKhoaList);
			return "nhanvien/xemlichbacsi/xemlichbacsi";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
			return "error";
		}
	}

	@GetMapping("/nhanvien/xemlichbacsi/doctors-by-specialty")
	@ResponseBody
	public List<BacSi> getDoctorsBySpecialty1(@RequestParam("chuyenKhoaId") String chuyenKhoaId) {
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.findById(chuyenKhoaId); // Giả sử bạn có service này
		if (chuyenKhoa == null) {
			return Collections.emptyList();
		}
		return bacSiService.getDoctorsByChuyenKhoa(chuyenKhoaId); // Giả sử bạn có method này trong service
	}

	@GetMapping("/nhanvien/xemlichbacsi/schedule")
	@ResponseBody
	public List<LichBacSiDTO> getDoctorScheduleSlots(
	        @RequestParam("doctorId") String doctorId,
	        @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	    BacSi bacSi = bacSiService.findById(doctorId);
	    if (bacSi == null || date == null) {
	        return Collections.emptyList();
	    }

	    // Lấy lịch khám theo bác sĩ và ngày
	    List<LichKhamBenh> lichList = lichKhamBenhService.getLichKhamBenhByBacSi(doctorId).stream()
	            .filter(lich -> lich.getNgayThangNam().isEqual(date))
	            .collect(Collectors.toList());

	    // Khung giờ mặc định
	    Map<String, List<String>> timeFrames = Map.of(
	        "Sáng", List.of("07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"),
	        "Chiều", List.of("13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"),
	        "Ngoài Giờ", List.of("17:00", "17:30", "18:00", "18:30", "19:00", "19:30")
	    );

	    List<LichBacSiDTO> result = new ArrayList<>();

	    for (LichKhamBenh lich : lichList) {
	        String ca = lich.getCa();
	        List<String> timeList = timeFrames.getOrDefault(ca, new ArrayList<>());
	        List<SlotThoiGian> existingSlots = lich.getSlotThoiGian();

	        if (existingSlots == null || existingSlots.isEmpty()) {
	            // Có ca nhưng chưa tạo slot nào
	            LichBacSiDTO dto = new LichBacSiDTO();
	            dto.setNgayThangNam(date);
	            dto.setCaKham(ca);
	            dto.setThoiGianBatDau("---");
	            dto.setThoiGianKetThuc("---");
	            dto.setTrangThai("Chưa có slot");
	            dto.setTenBenhNhan("---");
	            dto.setSoDienThoai("---");
	            result.add(dto);
	        } else {
	            for (SlotThoiGian slot : existingSlots) {
	                LichBacSiDTO dto = new LichBacSiDTO();
	                dto.setNgayThangNam(date);
	                dto.setCaKham(ca);
	                dto.setThoiGianBatDau(slot.getThoiGianBatDau().toString());
	                dto.setThoiGianKetThuc(slot.getThoiGianKetThuc().toString());

	                // Kiểm tra trạng thái
	                String trangThai = slot.getTrangThai();
	                if ("checked-in".equals(trangThai)) {
	                    dto.setTrangThai("Đang khám");
	                } else if ("pending".equals(trangThai)) {
	                    dto.setTrangThai("Đang chờ");
	                } else if ("completed".equals(trangThai)) {
	                    dto.setTrangThai("Đã khám xong");
	                } else if ("cancelled".equals(trangThai)) {
	                    dto.setTrangThai("Đã hủy");
	                } else if (trangThai != null && !trangThai.isBlank()) {
	                    dto.setTrangThai("Đã đặt");
	                } else {
	                    dto.setTrangThai("Chưa đặt");
	                }


	                // Lấy thông tin bệnh nhân nếu slot đã được đặt
	                if ((List.of("Đã đặt", "Đã khám xong", "Đang khám","Đang chờ","Đã hủy").contains(dto.getTrangThai())) && slot.getBenhNhan() != null) {
 	                    BenhNhan benhNhan = slot.getBenhNhan();
	                    dto.setTenBenhNhan(benhNhan.getTen() != null ? benhNhan.getTen() : "Không xác định");
	                    dto.setSoDienThoai(benhNhan.getDienThoai() != null ? benhNhan.getDienThoai() : "Không xác định");
	                } else {
	                    dto.setTenBenhNhan("---");
	                    dto.setSoDienThoai("---");
	                }

	                // Lưu slotId để sử dụng khi check-in
	                dto.setSlotId(slot.getSlotId());

	                result.add(dto);
	            }
	        }
	    }

	    // Sắp xếp theo thứ tự ca, trạng thái (ưu tiên "Đã đến khám"), và thời gian
	    result.sort(Comparator
	        .comparingInt((LichBacSiDTO dto) -> getCaPriority(dto.getCaKham()))
	        .thenComparing((LichBacSiDTO dto) -> "Đã đến khám".equals(dto.getTrangThai()) ? 0 : 1) // Ưu tiên "Đã đến khám"
	        .thenComparing(dto -> {
	            try {
	                return LocalTime.parse(dto.getThoiGianBatDau());
	            } catch (Exception e) {
	                return LocalTime.MIN;
	            }
	        }));

	    return result;
	}

	private int getCaPriority(String ca) {
	    return switch (ca.toLowerCase()) {
	        case "sáng" -> 1;
	        case "chiều" -> 2;
	        case "ngoài giờ" -> 3;
	        default -> 4;
	    };
	}
	@PostMapping("/nhanvien/xemlichbacsi/checkin")
	@ResponseBody
	public ResponseEntity<Map<String, String>> checkInPatient(@RequestParam("slotId") String slotId) {
	    Map<String, String> response = new HashMap<>();
	    try {
	        // Tìm slot theo slotId
	        SlotThoiGian slot = slotThoiGianService.findById(slotId);
	        if (slot == null) {
	            response.put("status", "error");
	            response.put("message", "Không tìm thấy slot!");
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	        }

	        // Kiểm tra trạng thái hiện tại
	        if (!"Đã đặt".equals(slot.getTrangThai()) && !"pending".equals(slot.getTrangThai())) {
	            response.put("status", "error");
	            response.put("message", "Slot không ở trạng thái có thể check-in!");
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        // Cập nhật trạng thái
	        slot.setTrangThai("checked-in");
	        slotThoiGianService.save(slot);

	        response.put("status", "success");
	        response.put("message", "Check-in thành công!");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", "Có lỗi xảy ra: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	
	
	@GetMapping("/nhanvien/xemlichsukhambenh")
    public String lichSuKhamNhanVien(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String tenBenhNhan,
            @RequestParam(required = false) String tenBacSi,
            @RequestParam(required = false) String dienThoai, // Thêm tham số SĐT
            Model model,
            RedirectAttributes redirectAttributes) {

        Pageable pageable = PageRequest.of(page, size);
        Page<LichSuKhamNhanVienDTO> lichSuKhams;

        // Áp dụng bộ lọc nếu có
        if ((date != null && !date.isEmpty()) || (tenBenhNhan != null && !tenBenhNhan.isEmpty()) || 
            (tenBacSi != null && !tenBacSi.isEmpty()) || (dienThoai != null && !dienThoai.isEmpty())) {
            lichSuKhams = hoSoBenhService.getLichSuKhamForNhanVienWithFilters(date, tenBenhNhan, tenBacSi, dienThoai, pageable);
        } else {
            lichSuKhams = hoSoBenhService.getLichSuKhamForNhanVien(pageable);
        }

        // Kiểm tra nếu page vượt quá totalPages, chuyển hướng về trang hợp lệ
        if (page >= lichSuKhams.getTotalPages() && lichSuKhams.getTotalPages() > 0) {
            redirectAttributes.addAttribute("page", lichSuKhams.getTotalPages() - 1);
            redirectAttributes.addAttribute("size", size);
            redirectAttributes.addAttribute("date", date);
            redirectAttributes.addAttribute("tenBenhNhan", tenBenhNhan);
            redirectAttributes.addAttribute("tenBacSi", tenBacSi);
            redirectAttributes.addAttribute("dienThoai", dienThoai); // Thêm tham số SĐT
            return "redirect:/nhanvien/xemlichsukhambenh";
        }

        // Nếu page vượt quá và totalPages = 0 (không có dữ liệu), quay về trang 0
        if (page > 0 && lichSuKhams.getTotalPages() == 0) {
            redirectAttributes.addAttribute("page", 0);
            redirectAttributes.addAttribute("size", size);
            redirectAttributes.addAttribute("date", date);
            redirectAttributes.addAttribute("tenBenhNhan", tenBenhNhan);
            redirectAttributes.addAttribute("tenBacSi", tenBacSi);
            redirectAttributes.addAttribute("dienThoai", dienThoai); // Thêm tham số SĐT
            return "redirect:/nhanvien/xemlichsukhambenh";	
        }

        model.addAttribute("lichSuKhams", lichSuKhams.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", lichSuKhams.getTotalPages());
        model.addAttribute("date", date);
        model.addAttribute("tenBenhNhan", tenBenhNhan);
        model.addAttribute("tenBacSi", tenBacSi);
        model.addAttribute("dienThoai", dienThoai); // Thêm vào model

        return "nhanvien/xemlichsukhambenh/xemlichsukhambenh";
    }
	
}
