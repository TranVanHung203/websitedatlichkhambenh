package tlcn.quanlyphongkham.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.Hibernate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.LichBacSiDTO;
import tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.MedicalHistoryDTO;
import tlcn.quanlyphongkham.dtos.PaymentDetailsDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.security.CustomUserDetails;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.LoaiXetNghiemService;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.PhieuXetNghiemService;
import tlcn.quanlyphongkham.services.SlotThoiGianService;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;

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

	@Autowired
	private PhieuXetNghiemService phieuXetNghiemService;

	@Autowired
	private LoaiXetNghiemService loaiXetNghiemService;

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
	@GetMapping("/nhanvien/home")
	public String Home(Model model) {
		
		return "nhanvien/home/home"; // Return to Step 1 view
	}

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

	@GetMapping("/nhanvien/xemlichbacsi")
	public String viewDoctorSchedule(Model model) {
		try {
			List<BacSi> bacSiList = bacSiService.getAllDoctors();
			if (bacSiList == null) {
				model.addAttribute("errorMessage", "Không thể tải danh sách bác sĩ.");
				return "error";
			}

			List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
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
		if (chuyenKhoaId.equals("0")) { // Trường hợp lấy tất cả bác sĩ
			return bacSiService.getAllDoctors();
		}
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.findById(chuyenKhoaId);
		if (chuyenKhoa == null) {
			return Collections.emptyList();
		}
		return bacSiService.getDoctorsByChuyenKhoa(chuyenKhoaId);
	}

	@GetMapping("/nhanvien/xemlichbacsi/schedule")
	@ResponseBody
	public List<LichBacSiDTO> getDoctorScheduleSlots(@RequestParam("doctorId") String doctorId,
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		BacSi bacSi = bacSiService.findById(doctorId);
		if (bacSi == null || date == null) {
			return Collections.emptyList();
		}

		List<LichKhamBenh> lichList = lichKhamBenhService.getLichKhamBenhByBacSi(doctorId).stream()
				.filter(lich -> lich.getNgayThangNam().isEqual(date)).collect(Collectors.toList());

		Map<String, List<String>> timeFrames = Map.of("Sáng",
				List.of("07:30", "08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00"), "Chiều",
				List.of("13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"), "Ngoài Giờ",
				List.of("17:00", "17:30", "18:00", "18:30", "19:00", "19:30"));

		List<LichBacSiDTO> result = new ArrayList<>();

		for (LichKhamBenh lich : lichList) {
			String ca = lich.getCa();
			List<String> timeList = timeFrames.getOrDefault(ca, new ArrayList<>());
			List<SlotThoiGian> existingSlots = lich.getSlotThoiGian();
			
			if (existingSlots == null || existingSlots.isEmpty()) {
			    LichBacSiDTO dto = new LichBacSiDTO();
			    
			    dto.setNgayThangNam(date);
			    dto.setCaKham(ca);
			    dto.setThoiGianBatDau("---");
			    dto.setThoiGianKetThuc("---");
			    dto.setTrangThai("Chưa có slot");
			    dto.setTenBenhNhan("---");
			    dto.setSoDienThoai("---");
			    dto.setDaThanhToan(false); // Default to 0 since no HoSoBenh exists
			    result.add(dto);
			} else {
			    for (SlotThoiGian slot : existingSlots) {
			        LichBacSiDTO dto = new LichBacSiDTO();
			        dto.setNgayThangNam(date);
			        dto.setCaKham(ca);
			        dto.setThoiGianBatDau(slot.getThoiGianBatDau().toString());
			        dto.setThoiGianKetThuc(slot.getThoiGianKetThuc().toString());

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

			        if (List.of("Đã đặt", "Đã khám xong", "Đang khám", "Đang chờ", "Đã hủy")
			                .contains(dto.getTrangThai()) && slot.getBenhNhan() != null) {
			            BenhNhan benhNhan = slot.getBenhNhan();
			            dto.setTenBenhNhan(benhNhan.getTen() != null ? benhNhan.getTen() : "Không xác định");
			            dto.setSoDienThoai(
			                    benhNhan.getDienThoai() != null ? benhNhan.getDienThoai() : "Không xác định");
			        } else {
			            dto.setTenBenhNhan("---");
			            dto.setSoDienThoai("---");
			        }

			        dto.setSlotId(slot.getSlotId());
			        // Set daThanhToan with null check for hoSoBenh
			        dto.setDaThanhToan(slot.getHoSoBenh() != null ? slot.getHoSoBenh().getDaThanhToan() : false);
			        result.add(dto);
			    }
			}
		}

		result.sort(Comparator.comparingInt((LichBacSiDTO dto) -> getCaPriority(dto.getCaKham()))
				.thenComparing((LichBacSiDTO dto) -> "Đã đến khám".equals(dto.getTrangThai()) ? 0 : 1)
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
			SlotThoiGian slot = slotThoiGianService.findById(slotId);
			if (slot == null) {
				response.put("status", "error");
				response.put("message", "Không tìm thấy slot!");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			if (!"Đã đặt".equals(slot.getTrangThai()) && !"pending".equals(slot.getTrangThai())) {
				response.put("status", "error");
				response.put("message", "Slot không ở trạng thái có thể check-in!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

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

	@GetMapping("/nhanvien/xemlichbacsi/payment/details")
	@ResponseBody
	public ResponseEntity<PaymentDetailsDTO> getPaymentDetails(@RequestParam("slotId") String slotId) {
		try {
			PaymentDetailsDTO paymentDetails = hoSoBenhService.getPaymentDetailsBySlotId(slotId);
			if (paymentDetails == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(paymentDetails);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
//Thanh toán cho tiền mặt
	@PostMapping("/nhanvien/xemlichbacsi/payment/confirm")
	@ResponseBody
	public ResponseEntity<Map<String, String>> confirmPayment(@RequestParam("hoSoId") String hoSoId) {
		Map<String, String> response = new HashMap<>();
		try {
			HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
			if (hoSoBenh == null) {
				response.put("status", "error");
				response.put("message", "Không tìm thấy hồ sơ bệnh!");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			if (hoSoBenh.getDaThanhToan()) {
				response.put("status", "error");
				response.put("message", "Hồ sơ đã được thanh toán!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			hoSoBenh.setDaThanhToan(true);
			hoSoBenhService.save(hoSoBenh);

			// Cập nhật trạng thái SlotThoiGian
			if (hoSoBenh.getSlotThoiGian() != null) {
				hoSoBenh.getSlotThoiGian().setTrangThai("completed");
				slotThoiGianService.save(hoSoBenh.getSlotThoiGian());
			}

			response.put("status", "success");
			response.put("message", "Thanh toán thành công!");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", "Có lỗi xảy ra: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/nhanvien/xemlichbacsi/payment/credit-card")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> createMomoPayment(@RequestParam("hoSoId") String hoSoId,
			@RequestParam(value = "paymentMethod", required = false, defaultValue = "payWithATM") String paymentMethod) {
		Logger logger = LoggerFactory.getLogger(getClass());
		Map<String, Object> response = new HashMap<>();
		try {
			logger.info("Bắt đầu xử lý thanh toán Momo cho hoSoId: {}, phương thức: {}", hoSoId, paymentMethod);

			// Tìm hồ sơ bệnh
			HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
			
			if (hoSoBenh == null) {
				logger.warn("Không tìm thấy hồ sơ bệnh với hoSoId: {}", hoSoId);
				response.put("status", "error");
				response.put("message", "Không tìm thấy hồ sơ bệnh!");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			// Kiểm tra trạng thái thanh toán
			if (hoSoBenh.getDaThanhToan()) {
				logger.warn("Hồ sơ đã được thanh toán, hoSoId: {}", hoSoId);
				response.put("status", "error");
				response.put("message", "Hồ sơ đã được thanh toán!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Kiểm tra slot thời gian
			if (hoSoBenh.getSlotThoiGian() == null || hoSoBenh.getSlotThoiGian().getSlotId() == null) {
				logger.warn("Slot thời gian không tồn tại cho hoSoId: {}", hoSoId);
				response.put("status", "error");
				response.put("message", "Slot thời gian không tồn tại!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Lấy chi tiết thanh toán theo slotId
			PaymentDetailsDTO paymentDetails = hoSoBenhService
					.getPaymentDetailsBySlotId(hoSoBenh.getSlotThoiGian().getSlotId());
			if (paymentDetails == null) {
				logger.warn("Không tìm thấy chi tiết thanh toán cho slotId: {}",
						hoSoBenh.getSlotThoiGian().getSlotId());
				response.put("status", "error");
				response.put("message", "Không tìm thấy chi tiết thanh toán!");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			// Lấy tổng tiền từ PaymentDetailsDTO
			BigDecimal amount = paymentDetails.getTongTien();
			if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
				logger.warn("Tổng tiền không hợp lệ cho hoSoId: {}", hoSoId);
				response.put("status", "error");
				response.put("message", "Tổng tiền không hợp lệ!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			long amountLong = amount.longValue();
			logger.info("Tổng tiền thanh toán: {}", amountLong);

			// Thông tin Momo
			String partnerCode = "MOMOLRJZ20181206"; // Thay bằng partnerCode thực tế từ MoMo
			String accessKey = "mTCKt9W3eU1m39TW"; // Thay bằng accessKey thực tế
			String secretKey = "SetA5RDnLHvt51AULf51DyauxUo3kDU6"; // Thay bằng secretKey thực tế
			String IdBacSi=hoSoBenh.getBacSi().getBacSiId();
			String idBacSi = URLEncoder.encode(IdBacSi, "UTF-8");
			LocalDateTime thoiGianTao = hoSoBenh.getThoiGianTao();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Format as yyyy-MM-dd
			String ngay = thoiGianTao.format(formatter);
			String encodedNgay = URLEncoder.encode(ngay, StandardCharsets.UTF_8.name());
			String redirectUrl = "https://websitedatlichkhambenh-mi5p.onrender.com/nhanvien/xemlichbacsi/payment/confirm-momo?idBacSi="+idBacSi+"&ngay="+encodedNgay;
			String ipnUrl = "https://websitedatlichkhambenh-mi5p.onrender.com/nhanvien/xemlichbacsi/payment/momo-ipn";
			String requestId = UUID.randomUUID().toString();
			String orderId = "ORDER_" + hoSoId + "_" + System.currentTimeMillis();
			String orderInfo = "Thanh toán hồ sơ bệnh " + hoSoId;
			String requestType = "payWithCC".equals(paymentMethod) ? "payWithCC" : "payWithATM";
			String extraData = "";
			
			// Tạo chữ ký
			String rawSignature = "accessKey=" + accessKey + "&amount=" + amountLong + "&extraData=" + extraData
					+ "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo + "&partnerCode="
					+ partnerCode + "&redirectUrl=" + redirectUrl + "&requestId=" + requestId + "&requestType="
					+ requestType ;

			String signature = hmacSha256(rawSignature, secretKey);

			// Tạo request body cho MoMo
			JSONObject requestBody = new JSONObject();
			requestBody.put("partnerCode", partnerCode);
			requestBody.put("partnerName", "Your Partner Name");
			requestBody.put("storeId", "Your Store ID");
			requestBody.put("requestId", requestId);
			requestBody.put("amount", amountLong);
			requestBody.put("orderId", orderId);
			requestBody.put("orderInfo", orderInfo);
			requestBody.put("redirectUrl", redirectUrl);
			requestBody.put("ipnUrl", ipnUrl);
			requestBody.put("extraData", extraData);
			requestBody.put("requestType", requestType);
			requestBody.put("signature", signature);
			requestBody.put("idBacSi", idBacSi);
			requestBody.put("lang", "vi");

			// Gọi API MoMo
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://test-payment.momo.vn/v2/gateway/api/create");
			httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setEntity(new StringEntity(requestBody.toString(), StandardCharsets.UTF_8));

			CloseableHttpResponse momoResponse = client.execute(httpPost);
			int statusCode = momoResponse.getStatusLine().getStatusCode();
			String responseBody = EntityUtils.toString(momoResponse.getEntity(), StandardCharsets.UTF_8);

			logger.info("Momo API Response Status: {}, Body: {}", statusCode, responseBody);

			if (statusCode != 200) {
				logger.error("Momo API trả về mã trạng thái không phải 200: {}, response: {}", statusCode,
						responseBody);
				response.put("status", "error");
				response.put("message", "Lỗi khi gọi API MoMo: Trạng thái " + statusCode);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

			if (responseBody == null || responseBody.trim().isEmpty()) {
				logger.error("Momo API trả về response body rỗng");
				response.put("status", "error");
				response.put("message", "Phản hồi từ MoMo rỗng!");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

			JSONObject momoResult;
			try {
				momoResult = new JSONObject(responseBody);
			} catch (Exception e) {
				logger.error("Không thể phân tích phản hồi MoMo API thành JSON: {}, lỗi: {}", responseBody,
						e.getMessage());
				response.put("status", "error");
				response.put("message", "Phản hồi từ MoMo không hợp lệ: Không thể phân tích JSON!");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}

			if (momoResult.has("payUrl") && !momoResult.getString("payUrl").isEmpty()) {
				String payUrl = momoResult.getString("payUrl");
				logger.info("Tạo đơn hàng MoMo thành công, payUrl: {}", payUrl);
				response.put("status", "success");
				response.put("message", "Tạo đơn hàng MoMo thành công!");
				response.put("payUrl", payUrl);
				response.put("orderId", orderId);
				return ResponseEntity.ok(response);
			} else {
				String errorMessage = momoResult.optString("message", "Lỗi không xác định");
				logger.error("Không thể tạo đơn hàng MoMo, phản hồi: {}", responseBody);
				response.put("status", "error");
				response.put("message", "Không thể tạo đơn hàng MoMo: " + errorMessage);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
			}
		} catch (Exception e) {
			logger.error("Lỗi khi xử lý thanh toán MoMo cho hoSoId: {}, lỗi: {}", hoSoId, e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "Có lỗi xảy ra: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Hàm tạo chữ ký HMAC SHA256 (không thay đổi)
	private String hmacSha256(String data, String key) throws Exception {
		Mac sha256Hmac = Mac.getInstance("HmacSHA256");
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
		sha256Hmac.init(secretKeySpec);
		byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	@PostMapping("/nhanvien/xemlichbacsi/payment/check-status")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkPaymentStatus(@RequestParam("orderId") String orderId) {
		Logger logger = LoggerFactory.getLogger(getClass());
		Map<String, Object> response = new HashMap<>();

		try {
			logger.info("Kiểm tra trạng thái thanh toán cho orderId: {}", orderId);

			// Giả lập logic kiểm tra trạng thái (thay bằng logic thực tế với API Momo hoặc
			// DB)
			// Ví dụ: Kiểm tra trong cơ sở dữ liệu hoặc gọi API IPN của Momo
			// Đây là logic giả lập, bạn cần thay bằng cách gọi thực tế
			String paymentStatus = checkPaymentStatusInDatabase(orderId); // Hàm giả lập, cần triển khai

			if ("SUCCESS".equalsIgnoreCase(paymentStatus)) {
				response.put("status", "success");
				response.put("message", "Thanh toán thành công!");
			} else if ("FAILED".equalsIgnoreCase(paymentStatus)) {
				response.put("status", "error");
				response.put("message", "Thanh toán thất bại!");
			} else {
				response.put("status", "pending");
				response.put("message", "Thanh toán đang xử lý!");
			}

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Lỗi khi kiểm tra trạng thái thanh toán cho orderId: {}, lỗi: {}", orderId, e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "Có lỗi xảy ra khi kiểm tra trạng thái: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	// Hàm giả lập kiểm tra trạng thái (cần thay bằng logic thực tế)
	private String checkPaymentStatusInDatabase(String orderId) {
		// Thay bằng logic kiểm tra trong DB hoặc gọi API IPN của Momo
		// Ví dụ: Sử dụng JPA hoặc JDBC để truy vấn trạng thái thanh toán
		// Đây chỉ là placeholder
		return "PENDING"; // Giả lập, cần thay bằng logic thực tế
	}

	@GetMapping("nhanvien/xemlichbacsi/payment/confirm-momo")
    public RedirectView confirmMomoPayment(
            @RequestParam("partnerCode") String partnerCode,
            @RequestParam("orderId") String orderId,
            @RequestParam("requestId") String requestId,
            @RequestParam("amount") String amount,
            @RequestParam("orderInfo") String orderInfo,
            @RequestParam("orderType") String orderType,
            @RequestParam("transId") String transId,
            @RequestParam("resultCode") String resultCode,
            @RequestParam("message") String message,
            @RequestParam("idBacSi") String idBacSi,
            @RequestParam("responseTime") String responseTime,
            @RequestParam("extraData") String extraData,
            @RequestParam("signature") String signature,
            @RequestParam("ngay") String ngay) {

        // Extract hoSoId from orderId
        String hoSoId = extractHoSoIdFromOrderId(orderId);

        // Encode query parameters
        String encodedIdBacSi = URLEncoder.encode(idBacSi, StandardCharsets.UTF_8);
        String encodedNgay = URLEncoder.encode(ngay, StandardCharsets.UTF_8);

        // Base redirect URL
        String redirectUrl = "/nhanvien/xemlichbacsi?doctorId=" + encodedIdBacSi + "&date=" + encodedNgay;

        if ("0".equals(resultCode)) {
            // Update payment status
            updatePaymentStatus(hoSoId);
            return new RedirectView(redirectUrl);
        } else {
            // Append error message for failed payment
            redirectUrl += "&error=" + URLEncoder.encode("Thanh toan that bai", StandardCharsets.UTF_8);
            return new RedirectView(redirectUrl);
        }
    }

	// Hàm trích xuất hoSoId từ orderId (tùy chỉnh theo định dạng của bạn)
	private String extractHoSoIdFromOrderId(String orderId) {
		// Giả sử orderId có định dạng "ORDER_<hoSoId>_<timestamp>"
		String[] parts = orderId.split("_");
		if (parts.length >= 2) {
			return parts[1]; // Trả về hoSoId (phần thứ 2)
		}
		return null; // Xử lý lỗi nếu không trích xuất được
	}

	// Hàm cập nhật trạng thái thanh toán trong cơ sở dữ liệu
	private void updatePaymentStatus(String hoSoId) {
		// Giả sử bạn có service để quản lý HoSoBenh
		HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
		if (hoSoBenh != null) {
			hoSoBenh.setDaThanhToan(true); // Cập nhật trạng thái thành true
			hoSoBenhService.save(hoSoBenh); // Lưu thay đổi vào cơ sở dữ liệu
		}
	}

	@PostMapping("/nhanvien/xemlichbacsi/payment/momo-ipn")
	@ResponseBody
	public String handleMomoIpn(@RequestParam Map<String, String> params) {
		String resultCode = params.get("resultCode");
		String orderId = params.get("orderId");

		if ("0".equals(resultCode)) {
			String hoSoId = extractHoSoIdFromOrderId(orderId);
			updatePaymentStatus(hoSoId);
		}
		// Trả về phản hồi theo yêu cầu của MoMo (thường là "SUCCESS")
		return "SUCCESS";
	}

	@GetMapping("/nhanvien/xemlichsukhambenh")
	public String getMedicalHistoryForStaff(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "4") int size,
			@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(value = "patientName", required = false) String patientName,
			@RequestParam(value = "doctorName", required = false) String doctorName,
			@RequestParam(value = "phoneNumber", required = false) String phoneNumber, Model model) {
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "thoi_gian_tao"));
			LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
			LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59, 999999999) : null;

			patientName = patientName != null && !patientName.trim().isEmpty() ? patientName.trim() : null;
			doctorName = doctorName != null && !doctorName.trim().isEmpty() ? doctorName.trim() : null;
			phoneNumber = phoneNumber != null && !phoneNumber.trim().isEmpty() ? phoneNumber.trim() : null;

			Page<HoSoBenh> medicalHistoryPage = hoSoBenhService.findMedicalHistoryWithFilters(startDateTime,
					endDateTime, patientName, doctorName, phoneNumber, pageable);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

			Page<MedicalHistoryDTO> dtos = medicalHistoryPage.map(hsb -> {
				Hibernate.initialize(hsb.getBenhNhan());
				Hibernate.initialize(hsb.getBacSi());
				Hibernate.initialize(hsb.getDonThuocs());
				Hibernate.initialize(hsb.getXetNghiems());
				Hibernate.initialize(hsb.getPhieuXetNghiems());
				Hibernate.initialize(hsb.getVitalSigns());

				MedicalHistoryDTO dto = new MedicalHistoryDTO();
				dto.setHoSoId(hsb.getHoSoId());

				if (hsb.getBenhNhan() != null) {
					MedicalHistoryDTO.BenhNhanDTO benhNhanDTO = new MedicalHistoryDTO.BenhNhanDTO(
							hsb.getBenhNhan().getBenhNhanId(),
							hsb.getBenhNhan().getTen() != null ? hsb.getBenhNhan().getTen() : "-",
							hsb.getBenhNhan().getDienThoai() != null ? hsb.getBenhNhan().getDienThoai() : "-");
					dto.setBenhNhan(benhNhanDTO);
				} else {
					dto.setBenhNhan(new MedicalHistoryDTO.BenhNhanDTO("-", "-", "-"));
				}

				if (hsb.getBacSi() != null) {
					MedicalHistoryDTO.BacSiDTO bacSiDTO = new MedicalHistoryDTO.BacSiDTO(hsb.getBacSi().getBacSiId(),
							hsb.getBacSi().getTen() != null ? hsb.getBacSi().getTen() : "-");
					dto.setBacSi(bacSiDTO);
				} else {
					dto.setBacSi(new MedicalHistoryDTO.BacSiDTO("-", "-"));
				}

				dto.setChanDoan(hsb.getChanDoan() != null ? hsb.getChanDoan() : "-");
				dto.setTrieuChung(hsb.getTrieuChung() != null ? hsb.getTrieuChung() : "-");
				dto.setTongTien(hsb.getTongTien() != null ? hsb.getTongTien() : 0);
				dto.setDaThanhToan(hsb.getDaThanhToan() != null ? hsb.getDaThanhToan() : false);
				dto.setThoiGianTao(hsb.getThoiGianTao() != null ? hsb.getThoiGianTao().format(formatter) : "-");

				if (hsb.getVitalSigns() != null && !hsb.getVitalSigns().isEmpty()) {
					List<MedicalHistoryDTO.VitalSignsDTO> vitalSignsDTOs = hsb.getVitalSigns().stream().map(vs -> {
						return new MedicalHistoryDTO.VitalSignsDTO(vs.getId(),
								vs.getTemperature() != null ? vs.getTemperature() : 0.0f,
								vs.getHeight() != null ? vs.getHeight() : 0.0f,
								vs.getWeight() != null ? vs.getWeight() : 0.0f,
								vs.getBloodPressureSys() != null ? vs.getBloodPressureSys() : 0,
								vs.getBloodPressureDia() != null ? vs.getBloodPressureDia() : 0,
								vs.getNotes() != null ? vs.getNotes() : "-",
								vs.getThoiGianTao() != null ? vs.getThoiGianTao().format(formatter) : "-");
					}).collect(Collectors.toList());
					dto.setVitalSigns(vitalSignsDTOs);
				} else {
					dto.setVitalSigns(Collections.emptyList());
				}

				if (hsb.getDonThuocs() != null && !hsb.getDonThuocs().isEmpty()) {
					List<MedicalHistoryDTO.DonThuocDTO> donThuocDTOs = hsb.getDonThuocs().stream().map(dt -> {
						Hibernate.initialize(dt.getDonThuocThuocs());
						List<MedicalHistoryDTO.DonThuocThuocDTO> donThuocThuocDTOs = dt.getDonThuocThuocs() != null
								? dt.getDonThuocThuocs().stream().map(dtt -> {
									MedicalHistoryDTO.ThuocDTO thuocDTO = new MedicalHistoryDTO.ThuocDTO(
											dtt.getThuoc().getTen() != null ? dtt.getThuoc().getTen() : "-",
											dtt.getThuoc().getGia() != null ? dtt.getThuoc().getGia()
													: BigDecimal.ZERO);
									return new MedicalHistoryDTO.DonThuocThuocDTO(thuocDTO,
											dtt.getLieu() != null ? dtt.getLieu() : "-",
											dtt.getTanSuat() != null ? dtt.getTanSuat() : "-",
											dtt.getSoLuong() != 0 ? dtt.getSoLuong() : 0);
								}).collect(Collectors.toList())
								: Collections.emptyList();
						return new MedicalHistoryDTO.DonThuocDTO(dt.getDonThuocId(),
								dt.getFormattedTongTienThuoc() != null ? dt.getFormattedTongTienThuoc() : "-",
								donThuocThuocDTOs);
					}).collect(Collectors.toList());
					dto.setDonThuocs(donThuocDTOs);
				} else {
					dto.setDonThuocs(Collections.emptyList());
				}

				if (hsb.getXetNghiems() != null && !hsb.getXetNghiems().isEmpty()) {
					List<MedicalHistoryDTO.XetNghiemDTO> xetNghiemDTOs = hsb.getXetNghiems().stream().map(xn -> {
						MedicalHistoryDTO.LoaiXetNghiemDTO loaiXetNghiemDTO = new MedicalHistoryDTO.LoaiXetNghiemDTO(
								xn.getLoaiXetNghiem().getTenXetNghiem() != null
										? xn.getLoaiXetNghiem().getTenXetNghiem()
										: "-",
								xn.getLoaiXetNghiem().getGia() != null ? xn.getLoaiXetNghiem().getGia() : 0);
						return new MedicalHistoryDTO.XetNghiemDTO(loaiXetNghiemDTO,
								xn.getGhiChu() != null ? xn.getGhiChu() : "-",
								xn.getTrangThai() != null ? xn.getTrangThai() : "-",
								xn.getFileKetQua() != null ? xn.getFileKetQua() : "-",
								xn.getThoiGianTao() != null ? xn.getThoiGianTao().format(formatter) : "-");
					}).collect(Collectors.toList());
					dto.setXetNghiems(xetNghiemDTOs);
				} else {
					dto.setXetNghiems(Collections.emptyList());
				}

				if (hsb.getPhieuXetNghiems() != null && !hsb.getPhieuXetNghiems().isEmpty()) {
					List<MedicalHistoryDTO.PhieuXetNghiemDTO> phieuXetNghiemDTOs = hsb.getPhieuXetNghiems().stream()
							.map(pxn -> {
								return new MedicalHistoryDTO.PhieuXetNghiemDTO(
										pxn.getMaPhieu() != null ? pxn.getMaPhieu() : "-",
										pxn.getTongGia() != null ? pxn.getTongGia() : 0,
										pxn.getThoiGianTao() != null ? pxn.getThoiGianTao().format(formatter) : "-",
										pxn.getXetNghiemIds() != null ? pxn.getXetNghiemIds()
												: Collections.emptyList());
							}).collect(Collectors.toList());
					dto.setPhieuXetNghiems(phieuXetNghiemDTOs);
				} else {
					dto.setPhieuXetNghiems(Collections.emptyList());
				}

				return dto;
			});

			model.addAttribute("medicalHistory", dtos.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", medicalHistoryPage.getTotalPages());
			model.addAttribute("startDate", startDate);
			model.addAttribute("endDate", endDate);
			model.addAttribute("patientName", patientName);
			model.addAttribute("doctorName", doctorName);
			model.addAttribute("phoneNumber", phoneNumber);

			return "nhanvien/xemlichsukhambenh/xemlichsukhambenh";
		} catch (Exception e) {
			model.addAttribute("error", "Không thể lấy lịch sử khám bệnh: " + e.getMessage());
			return "nhanvien/xemlichsukhambenh/xemlichsukhambenh";
		}
	}

	@GetMapping("/nhanvien/xemlichsukhambenh/download-pdf")
	public ResponseEntity<?> downloadMedicalHistoryPdf(
	        @RequestParam(value = "hoSoIds", required = false) String hoSoIds) {
	    try {
	        if (hoSoIds == null || hoSoIds.trim().isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Collections.singletonMap("error", "Vui lòng chọn ít nhất một bản ghi"));
	        }
	        // Parse hoSoIds as strings
	        List<String> selectedIds = Arrays.stream(hoSoIds.split(","))
	                .map(String::trim)
	                .filter(id -> !id.isEmpty())
	                .collect(Collectors.toList());

	        if (selectedIds.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body(Collections.singletonMap("error", "Danh sách ID trống sau khi xử lý"));
	        }


	        // Fetch selected medical history records
	        List<HoSoBenh> medicalHistory = hoSoBenhService.findMedicalHistoryByIds(selectedIds);

	        if (medicalHistory.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body(Collections.singletonMap("error", "Không tìm thấy bản ghi nào với ID đã chọn"));
	        }

	        // Initialize PDF document
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);

	        // Load a font that supports Vietnamese
	        PdfFont font;
	        try {
	            font = PdfFontFactory.createFont("/fonts/BeVietnamPro-Regular.ttf", PdfEncodings.IDENTITY_H,
	                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	        } catch (Exception e1) {
	            try {
	                font = PdfFontFactory.createFont("C:/Windows/Fonts/arial.ttf", PdfEncodings.IDENTITY_H,
	                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	            } catch (Exception e2) {
	                try {
	                    font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", PdfEncodings.IDENTITY_H,
	                            PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	                } catch (Exception e3) {
	                    font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	                }
	            }
	        }
	        document.setFont(font);

	        // Adding title
	        document.add(new Paragraph("Lịch Sử Khám Bệnh").setFontSize(18).setBold()
	                .setTextAlignment(TextAlignment.CENTER));

	        // Formatter for dates
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	        for (HoSoBenh hsb : medicalHistory) {
	            Hibernate.initialize(hsb.getBenhNhan());
	            Hibernate.initialize(hsb.getBacSi());
	            Hibernate.initialize(hsb.getDonThuocs());
	            Hibernate.initialize(hsb.getXetNghiems());
	            Hibernate.initialize(hsb.getVitalSigns());

	            // General info
	            document.add(new Paragraph("Khám ngày: "
	                    + (hsb.getThoiGianTao() != null ? hsb.getThoiGianTao().format(formatter) : "Không có")));
	            document.add(new Paragraph("Bệnh nhân: "
	                    + (hsb.getBenhNhan() != null
	                            ? hsb.getBenhNhan().getTen() + " ("
	                                    + (hsb.getBenhNhan().getDienThoai() != null ? hsb.getBenhNhan().getDienThoai()
	                                            : "Không có")
	                                    + ")"
	                            : "Không có")));
	            document.add(new Paragraph(
	                    "Bác sĩ: " + (hsb.getBacSi() != null ? hsb.getBacSi().getTen() : "Chưa chỉ định")));
	            document.add(
	                    new Paragraph("Chẩn đoán: " + (hsb.getChanDoan() != null ? hsb.getChanDoan() : "Chưa có")));
	            document.add(new Paragraph(
	                    "Triệu chứng: " + (hsb.getTrieuChung() != null ? hsb.getTrieuChung() : "Chưa có")));
	            document.add(new Paragraph("Tổng tiền: "
	                    + (hsb.getTongTien() != null ? String.format("%,d VNĐ", hsb.getTongTien().longValue())
	                            : "Chưa tính")));
	            document.add(new Paragraph(
	                    "Đã thanh toán: " + (hsb.getDaThanhToan() != null && hsb.getDaThanhToan() ? "Có" : "Chưa")));

	            // Vital Signs
	            document.add(new Paragraph("Dấu hiệu sinh tồn").setBold());
	            if (hsb.getVitalSigns() != null && !hsb.getVitalSigns().isEmpty()) {
	                Table table = new Table(6);
	                table.addCell(new Paragraph("Thời gian").setFont(font));
	                table.addCell(new Paragraph("Nhiệt độ").setFont(font));
	                table.addCell(new Paragraph("Chiều cao").setFont(font));
	                table.addCell(new Paragraph("Cân nặng").setFont(font));
	                table.addCell(new Paragraph("Huyết áp").setFont(font));
	                table.addCell(new Paragraph("Ghi chú").setFont(font));
	                for (var vs : hsb.getVitalSigns()) {
	                    table.addCell(new Paragraph(
	                            vs.getThoiGianTao() != null ? vs.getThoiGianTao().format(formatter) : "Không có")
	                            .setFont(font));
	                    table.addCell(new Paragraph(
	                            vs.getTemperature() != null && vs.getTemperature() != 0.0f ? vs.getTemperature() + " °C"
	                                    : "Không có")
	                            .setFont(font));
	                    table.addCell(new Paragraph(
	                            vs.getHeight() != null && vs.getHeight() != 0.0f ? vs.getHeight() + " cm" : "Không có")
	                            .setFont(font));
	                    table.addCell(new Paragraph(
	                            vs.getWeight() != null && vs.getWeight() != 0.0f ? vs.getWeight() + " kg" : "Không có")
	                            .setFont(font));
	                    table.addCell(new Paragraph(vs.getBloodPressureSys() != null && vs.getBloodPressureDia() != null
	                            ? vs.getBloodPressureSys() + "/" + vs.getBloodPressureDia() + " mmHg"
	                            : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getNotes() != null ? vs.getNotes() : "Không có").setFont(font));
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            // Prescriptions
	            document.add(new Paragraph("Đơn thuốc").setBold());
	            if (hsb.getDonThuocs() != null && !hsb.getDonThuocs().isEmpty()) {
	                Table table = new Table(5);
	                table.addCell(new Paragraph("Đơn thuốc").setFont(font));
	                table.addCell(new Paragraph("Thuốc").setFont(font));
	                table.addCell(new Paragraph("Liều").setFont(font));
	                table.addCell(new Paragraph("Tần suất").setFont(font));
	                table.addCell(new Paragraph("Số lượng").setFont(font));
	                for (var dt : hsb.getDonThuocs()) {
	                    Hibernate.initialize(dt.getDonThuocThuocs());
	                    if (dt.getDonThuocThuocs() != null && !dt.getDonThuocThuocs().isEmpty()) {
	                        for (var dtt : dt.getDonThuocThuocs()) {
	                            table.addCell(new Paragraph(
	                                    "#" + (dt.getDonThuocId() != null ? dt.getDonThuocId() : "Không có"))
	                                    .setFont(font));
	                            table.addCell(
	                                    new Paragraph(dtt.getThuoc() != null ? dtt.getThuoc().getTen() : "Không có")
	                                            .setFont(font));
	                            table.addCell(new Paragraph(dtt.getLieu() != null ? dtt.getLieu() : "Không có")
	                                    .setFont(font));
	                            table.addCell(new Paragraph(dtt.getTanSuat() != null ? dtt.getTanSuat() : "Không có")
	                                    .setFont(font));
	                            table.addCell(new Paragraph(
	                                    dtt.getSoLuong() != 0 ? String.valueOf(dtt.getSoLuong()) : "Không có")
	                                    .setFont(font));
	                        }
	                    } else {
	                        table.addCell(
	                                new Paragraph("#" + (dt.getDonThuocId() != null ? dt.getDonThuocId() : "Không có"))
	                                        .setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                    }
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            // Tests
	            document.add(new Paragraph("Xét nghiệm").setBold());
	            if (hsb.getXetNghiems() != null && !hsb.getXetNghiems().isEmpty()) {
	                Table table = new Table(4);
	                table.addCell(new Paragraph("Loại xét nghiệm").setFont(font));
	                table.addCell(new Paragraph("Giá").setFont(font));
	                table.addCell(new Paragraph("Trạng thái").setFont(font));
	                table.addCell(new Paragraph("Ghi chú").setFont(font));
	                for (var xn : hsb.getXetNghiems()) {
	                    table.addCell(new Paragraph(
	                            xn.getLoaiXetNghiem() != null ? xn.getLoaiXetNghiem().getTenXetNghiem() : "Không có")
	                            .setFont(font));
	                    table.addCell(
	                            new Paragraph(xn.getLoaiXetNghiem() != null && xn.getLoaiXetNghiem().getGia() != null
	                                    ? String.format("%,d VNĐ", xn.getLoaiXetNghiem().getGia().longValue())
	                                    : "Không có").setFont(font));
	                    table.addCell(new Paragraph(xn.getTrangThai() != null ? xn.getTrangThai() : "Không có")
	                            .setFont(font));
	                    table.addCell(
	                            new Paragraph(xn.getGhiChu() != null ? xn.getGhiChu() : "Không có").setFont(font));
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            document.add(new Paragraph("----------------------------------------")
	                    .setTextAlignment(TextAlignment.CENTER));
	        }

	        document.close();

	        // Prepare PDF response
	        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lich-su-kham-benh.pdf");
	        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
	        headers.add(HttpHeaders.PRAGMA, "no-cache");
	        headers.add(HttpHeaders.EXPIRES, "0");

	        return ResponseEntity.ok().headers(headers).contentLength(baos.size())
	                .contentType(MediaType.APPLICATION_PDF).body(resource);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "Không thể tạo PDF: " + e.getMessage()));
	    }
	}
}
