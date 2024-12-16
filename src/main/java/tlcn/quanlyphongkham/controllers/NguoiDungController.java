package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.BenhNhanDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamDTO;
import tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.TaiKhoanProfileDTO;
import tlcn.quanlyphongkham.dtos.UserProfileDTO;
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
import tlcn.quanlyphongkham.services.LichSuDatLichService;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.SlotThoiGianService;
import tlcn.quanlyphongkham.services.UserProfileService;

@Controller
public class NguoiDungController {

	@Autowired
	private BenhNhanService benhNhanService;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private ChuyenKhoaService chuyenKhoaService;

	@Autowired
	private BacSiService bacSiService;

	@Autowired
	private HoSoBenhService hoSoBenhService;

	@Autowired
	private LichKhamBenhService lichKhamBenhService;
	@Autowired
	private SlotThoiGianService slotThoiGianService;

	@Autowired
	private LichSuDatLichService lichSuDatLichService;

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

	@GetMapping("/user/editprofile")
	public String editProfile(Model model) {
		nguoiDungId = getNguoiDungId();
		UserProfileDTO userProfile = userProfileService.getUserProfileByNguoiDungId(nguoiDungId);
		model.addAttribute("nguoiDung", userProfile.getNguoiDung());
		model.addAttribute("benhNhan", userProfile.getBenhNhan());

		// Tìm bệnh nhân theo nguoiDungId thay vì gán cứng benhNhanId
		BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);
		return "benhnhan/editprofile/editprofile";
	}

	@GetMapping("/user/lichsukhambenh")
	public String lichSuKham(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String date, // Thêm tham số date
			Model model) {
		nguoiDungId = getNguoiDungId();
		UserProfileDTO userProfile = userProfileService.getUserProfileByNguoiDungId(nguoiDungId);
		model.addAttribute("nguoiDung", userProfile.getNguoiDung());
		model.addAttribute("benhNhan", userProfile.getBenhNhan());

		BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);

		if (benhNhan == null) {
			model.addAttribute("lichSuKhams", Collections.emptyList());
			return "benhnhan/lichsukham/lichsukham";
		}

		Pageable pageable = PageRequest.of(page, size);
		Page<LichSuKhamDTO> lichSuKhams;

		// Nếu có ngày được chọn, gọi phương thức lọc theo ngày
		if (date != null && !date.isEmpty()) {
			lichSuKhams = hoSoBenhService.getLichSuKhamByBenhNhanIdAndDate(benhNhan.getBenhNhanId(), date, pageable);
		} else {
			lichSuKhams = hoSoBenhService.getLichSuKhamByBenhNhanId(benhNhan.getBenhNhanId(), pageable);
		}

		model.addAttribute("lichSuKhams", lichSuKhams);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", lichSuKhams.getTotalPages());

		return "benhnhan/lichsukhambenh/lichsukhambenh";
	}

	@PostMapping("/user/updateprofile")
	public String updateProfile(@ModelAttribute("nguoiDung") TaiKhoanProfileDTO tk,
			@ModelAttribute("benhNhan") BenhNhanDTO benhNhanDTO, Model model) {
		nguoiDungId = getNguoiDungId();
		// Tìm người dùng hiện tại theo ID
		NguoiDung existingUser = nguoiDungService.findById(nguoiDungId);

		// Kiểm tra nếu tìm thấy người dùng, sau đó cập nhật thông tin
		if (existingUser != null) {
			// Cập nhật các trường từ DTO vào entity NguoiDung
			if (tk.getTenDangNhap() != null && !tk.getTenDangNhap().isEmpty()) {
				existingUser.setTenDangNhap(tk.getTenDangNhap());
			}
			if (tk.getEmail() != null && !tk.getEmail().isEmpty()) {
				existingUser.setEmail(tk.getEmail());
			}

			// Lưu người dùng
			nguoiDungService.saveNguoiDung(existingUser);
		}

		// Tìm bệnh nhân hiện tại theo ID
		BenhNhan existingBenhNhan = benhNhanService.findById(nguoiDungId);

		// Cập nhật thông tin bệnh nhân
		if (existingBenhNhan != null) {
			// Cập nhật các trường từ DTO vào entity BenhNhan
			if (benhNhanDTO.getTen() != null && !benhNhanDTO.getTen().isEmpty()) {
				existingBenhNhan.setTen(benhNhanDTO.getTen());
			}
			if (benhNhanDTO.getGioiTinh() != null && !benhNhanDTO.getGioiTinh().isEmpty()) {
				existingBenhNhan.setGioiTinh(benhNhanDTO.getGioiTinh());
			}
			if (benhNhanDTO.getNgaySinh() != null && !benhNhanDTO.getNgaySinh().isEmpty()) {
				existingBenhNhan.setNgaySinh(LocalDate.parse(benhNhanDTO.getNgaySinh()));
			}
			if (benhNhanDTO.getDiaChi() != null && !benhNhanDTO.getDiaChi().isEmpty()) {
				existingBenhNhan.setDiaChi(benhNhanDTO.getDiaChi());
			}
			if (benhNhanDTO.getDienThoai() != null && !benhNhanDTO.getDienThoai().isEmpty()) {
				existingBenhNhan.setDienThoai(benhNhanDTO.getDienThoai());
			}

			// Lưu bệnh nhân
			benhNhanService.save(existingBenhNhan);
		}

		// Redirect về trang hồ sơ hoặc trang khác sau khi cập nhật thành công
		return "redirect:/user/editprofile";
	}

	@GetMapping("/view/schedule")
	public String viewLichKhamBenh(@RequestParam(required = false) String chuyenKhoaId,
			@RequestParam(required = false) String ngayThangNam, Model model) {

		// Lấy danh sách chuyên khoa
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);

		// Kiểm tra nếu không có tham số nào được truyền
		if (chuyenKhoaId == null || ngayThangNam == null) {
			// Trả về view mặc định với thông tin là null
			model.addAttribute("lichKhamBenhPage", Collections.emptyList()); // Tránh null
			model.addAttribute("selectedChuyenKhoaId", ""); // Không chọn chuyên khoa nào
			model.addAttribute("selectedNgayThangNam", ""); // Không chọn ngày nào
			return "benhnhan/viewbs/viewlichkhambenh"; // Trả về view mặc định
		}

		// Xử lý ngày tháng và gọi service
		LocalDate ngay = LocalDate.parse(ngayThangNam);
		List<LichKhamBenh> lichKhamBenhPage = lichKhamBenhService.findLichKhamBenhByChuyenKhoaAndNgay(chuyenKhoaId,
				ngay);

		// Truyền dữ liệu vào model
		model.addAttribute("lichKhamBenhPage", lichKhamBenhPage);

		model.addAttribute("selectedChuyenKhoaId", chuyenKhoaId); // Lưu chuyên khoa được chọn
		model.addAttribute("selectedNgayThangNam", ngayThangNam); // Lưu ngày được chọn

		return "benhnhan/viewbs/viewlichkhambenh";
	}

	@GetMapping("/findbs")
	public String findBacSi(@RequestParam(name = "section", required = false, defaultValue = "all") String section,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "5") int size,
			@RequestParam(name = "doctorName", required = false) String doctorName, // Thêm tham số tìm kiếm theo tên
																					// bác sĩ
			Model model) {

		Pageable pageable = PageRequest.of(page, size);

		Page<BacSi> bacSiPage;

		// Tìm bác sĩ theo tên và chuyên khoa nếu có
		if (doctorName != null && !doctorName.isEmpty()) {
			bacSiPage = bacSiService.findByNameAndChuyenKhoa(doctorName, section, pageable); // Tìm kiếm theo tên và
																								// chuyên khoa
		} else if ("all".equals(section)) {
			// Nếu chọn tất cả, load toàn bộ bác sĩ
			bacSiPage = bacSiService.findAll(pageable);
		} else {
			// Nếu chọn chuyên khoa cụ thể, load bác sĩ thuộc chuyên khoa đó
			bacSiPage = bacSiService.findByChuyenKhoaTen(section, pageable); // Sửa lại ở đây
		}

		// Load danh sách chuyên khoa để hiển thị trong dropdown
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();

		// Add vào model để render ra view
		model.addAttribute("bacSiPage", bacSiPage);
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);
		model.addAttribute("currentSection", section); // Giữ giá trị đã chọn
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("doctorName", doctorName); // Thêm tham số tìm kiếm tên bác sĩ

		return "benhnhan/viewbs/findbs"; // Trả về view
	}

	@GetMapping("/chitietbacsi/{bacSiId}")
	public String viewChiTietBacSi(@PathVariable("bacSiId") String bacSiId, Model model) {
		// Lấy thông tin bác sĩ từ service
		BacSi bacSi = bacSiService.findById(bacSiId);

		if (bacSi == null) {
			// Xử lý nếu bác sĩ không tồn tại
			model.addAttribute("errorMessage", "Bác sĩ không tồn tại");
			return "error"; // Trả về trang lỗi
		}
		// Kiểm tra nếu ngaySinh là null
		if (bacSi.getNgaySinh() != null) {
			// Định dạng ngày sinh từ LocalDate
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String formattedDate = bacSi.getNgaySinh().format(formatter);
			model.addAttribute("formattedDate", formattedDate);
		} else {
			model.addAttribute("formattedDate", "Không có thông tin");
		}

		// Thêm thông tin bác sĩ vào model để hiển thị trên trang chi tiết
		model.addAttribute("bacSi", bacSi);

		return "benhnhan/viewbs/chitietbs"; // Trả về view chi tiết bác sĩ
	}

	@GetMapping("/user/dangkylichkham")
	public String registerSchedule(Model model) {
		// Fetch list of specialties and pass it to the view
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);
		return "benhnhan/dangkylichkham/buoc1"; // Return to Step 1 view
	}

	@PostMapping("/user/dangkylichkham/doctor")
	public String getDoctorsBySpecialty(@RequestParam("service") String serviceId, Model model) {

		List<BacSiDTO> bacSiList = bacSiService.getBacSiByChuyenKhoa(serviceId);
		model.addAttribute("bacSiList", bacSiList);
		model.addAttribute("selectedService", serviceId); // Add selected service to model
		return "benhnhan/dangkylichkham/buoc1"; // Return to the same view with updated doctors list
	}

	@GetMapping("/user/dangkylichkham/next")
	public String proceedToStep2(@RequestParam("doctor") String doctorId, @RequestParam("service") String serviceId,
			Model model) {

		model.addAttribute("doctorId", doctorId);

		return "/benhnhan/dangkylichkham/buoc2";
	}

	public LocalDate parseDate(String dateStr) {

		if (dateStr != null && !dateStr.isEmpty()) {

			if (dateStr.matches("\\d{4}-\\d{2}-\\d{1}$")) {
				dateStr = dateStr.substring(0, 8) + "0" + dateStr.charAt(8);
			}

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(dateStr, formatter);
		}
		return null;
	}

	@PostMapping("/user/dangkylichkham/getAvailableSlots")
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

	@GetMapping("/user/dangkylichkham/buoc3")
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
		return "/benhnhan/dangkylichkham/buoc3";
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

	@PostMapping("/user/dangkylichkham/confirm")
	public ResponseEntity<Map<String, String>> confirmBooking(@RequestBody Map<String, String> requestData) {
		String doctorId = requestData.get("doctorId");
		String selectedDate = requestData.get("selectedDate");
		String selectedTime = requestData.get("selectedTime");
		String ca = requestData.get("selectedCa");
		LocalDate selectedDates;
		nguoiDungId = getNguoiDungId();
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			selectedDates = LocalDate.parse(selectedDate, formatter);
		} catch (DateTimeParseException e) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
			selectedDates = LocalDate.parse(selectedDate, formatter);
		}

		// Tìm Lịch Khám Bệnh theo doctorId, selectedDate và ca
		MaLichKhamBenhDTO lichKhamBenh = lichKhamBenhService.findByDoctorIdAndDateAndCa(doctorId, selectedDates, ca);
		BenhNhan benhnhan = benhNhanService.findById(nguoiDungId);
		Optional<LichKhamBenh> lichkham = lichKhamBenhService.findById(lichKhamBenh.getMaLichKhamBenh());
		SlotThoiGian check = slotThoiGianService.findExist(calculateStartTime(selectedTime), benhnhan.getBenhNhanId(),
				lichKhamBenh.getMaLichKhamBenh());

		if (lichKhamBenh != null && check == null) {
			SlotThoiGian slotThoiGian = new SlotThoiGian();
			slotThoiGian.setSlotId(UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên cho Slot
			slotThoiGian.setLichKhamBenh(lichkham.get()); // Liên kết với lịch khám bệnh đã tìm được

			// Xác định giờ bắt đầu và kết thúc dựa trên `ca` và `selectedTime`
			LocalTime thoiGianBatDau = calculateStartTime(selectedTime);
			LocalTime thoiGianKetThuc = calculateEndTime(selectedTime);

			slotThoiGian.setThoiGianBatDau(thoiGianBatDau);
			slotThoiGian.setThoiGianKetThuc(thoiGianKetThuc);
			slotThoiGian.setTrangThai("pending");
			slotThoiGian.setBenhNhan(benhnhan);

			// Lưu SlotThoiGian vào cơ sở dữ liệu
			slotThoiGianService.save(slotThoiGian);

			// Return a response with a success message
			Map<String, String> response = new HashMap<>();
			response.put("status", "success");
			response.put("message", "Booking confirmed successfully");

			return ResponseEntity.ok(response);
		} else {
			// Return a response with a failure message
			Map<String, String> response = new HashMap<>();
			response.put("status", "failure");
			response.put("message", "Đã có người đặt khung giờ này vui lòng thực hiện lại");

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	@GetMapping("/user/lichsudatlich")
	public String getLichSuDatLich(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String date) {
		nguoiDungId = getNguoiDungId();
		// Find BenhNhan by nguoiDungId
		BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);
		if (benhNhan == null) {
			// Handle the case where no patient is found for the user
			model.addAttribute("message", "Bệnh nhân không tìm thấy.");
			return "benhnhan/dangkylichkham/lichsudatlichkham";
		}

		// Determine the date to filter by, default to today
		LocalDate filteredDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

		// Get paginated list of slots based on BenhNhan and date
		Page<SlotThoiGian> slotPage = lichSuDatLichService.getLichSuDatLichByDateAndBenhNhanId(benhNhan.getBenhNhanId(),
				filteredDate, page, 3);

		model.addAttribute("slots", slotPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", slotPage.getTotalPages());
		model.addAttribute("date", filteredDate); // Pass the date to keep it in the view

		return "benhnhan/dangkylichkham/lichsudatlichkham";
	}

	@PostMapping("user/cancel")
	public String cancelSlot(@RequestParam String slotId, @RequestParam(required = false) String date,
			@RequestParam(required = false, defaultValue = "0") int page) {
		// Xử lý hủy lịch
		slotThoiGianService.deleteById(slotId);

		// Sau khi hủy, quay lại với các thông tin lịch sử
		return "redirect:/user/lichsudatlich?date=" + date + "&page=" + page;
	}

	// Controller để xử lý các yêu cầu liên quan đến chuyên khoa
	@GetMapping("/departments")
	public String showDepartments(Model model) {
		// Lấy danh sách chuyên khoa từ service
		List<ChuyenKhoa> chuyenKhoas = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoas", chuyenKhoas);
		return "benhnhan/xemchuyenkhoa/danhsachchuyenkhoa"; // Trang hiển thị danh sách chuyên khoa
	}

	@GetMapping("/departments/{chuyenKhoaId}")
	public String showDepartmentDetails(@PathVariable String chuyenKhoaId, Model model) {
		// Lấy thông tin chuyên khoa từ service theo ID
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
		model.addAttribute("chuyenKhoa", chuyenKhoa);
		return "benhnhan/xemchuyenkhoa/xemchitietchuyenkhoa"; // Trang chi tiết chuyên khoa
	}

}
