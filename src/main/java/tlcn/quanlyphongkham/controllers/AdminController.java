package tlcn.quanlyphongkham.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import tlcn.quanlyphongkham.dtos.LichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.NguoiDungDTO;
import tlcn.quanlyphongkham.dtos.QuanLyCaKhamDTO;
import tlcn.quanlyphongkham.dtos.ThemTaiKhoanDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.LoaiXetNghiem;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.LoaiXetNghiemService;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.StatisticsService;
import tlcn.quanlyphongkham.services.ThuocService;

@Controller
public class AdminController {
	@Autowired
	private LichKhamBenhService lichKhamBenhService;
	@Autowired
	private NguoiDungService nguoiDungService;
	@Autowired
	private BacSiService bacSiService;
	@Autowired
	private ChuyenKhoaService chuyenKhoaService;

	@Autowired
	private StatisticsService statisticsService;

	@Autowired
	private ThuocService thuocService;

	@Autowired
	private LoaiXetNghiemService loaiXetNghiemService;
	
	@GetMapping("/admin/home")
	public String home(Model model) {
		return "admin/home/home"; // Your Thymeleaf template
	}

	@GetMapping("/admin/qltk")
	public String getAllNguoiDung(Model model, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "") String search) {

		int pageSize = 5; // Số bản ghi mỗi trang (đã định nghĩa trong service)
		int visiblePages = 4; // Số trang hiển thị trên thanh phân trang (giống qlbs)

		// Lấy danh sách người dùng với phân trang và tìm kiếm
		Page<NguoiDungDTO> nguoiDungPage = nguoiDungService.getAllNguoiDungQLTK(search, page);

		int totalPages = nguoiDungPage.getTotalPages();

		// Tính toán startPage và endPage để cố định số trang hiển thị
		int startPage = Math.max(0, page - 1 - visiblePages / 2); // page bắt đầu từ 1 nên trừ 1
		int endPage = Math.min(startPage + visiblePages - 1, totalPages - 1);

		// Điều chỉnh nếu ở cuối danh sách trang
		if (endPage - startPage < visiblePages - 1) {
			startPage = Math.max(0, endPage - visiblePages + 1);
		}

		// Thêm dữ liệu vào model
		model.addAttribute("nguoiDungPage", nguoiDungPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage + 1); // +1 vì frontend hiển thị từ 1
		model.addAttribute("endPage", endPage + 1); // +1 vì frontend hiển thị từ 1
		model.addAttribute("search", search);

		return "admin/quanlytaikhoan/quanlytaikhoan"; // Tên của trang giao diện Thymeleaf
	}

	@GetMapping("/admin/qltk/add")
	public String openFormAddUser(Model model) {
		// Danh sách chuyên khoa để truyền vào combobox
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);
		return "admin/quanlytaikhoan/themtaikhoan";
	}

	@PutMapping("/admin/qltk/update")
	public ResponseEntity<?> updateNguoiDung(@RequestBody NguoiDungDTO nguoiDungDTO) {
		// Check if email or username already exists for another user
		boolean emailExists = nguoiDungService.emailExistsForAnotherUser(nguoiDungDTO.getEmail(),
				nguoiDungDTO.getNguoiDungId());
		boolean usernameExists = nguoiDungService.usernameExistsForAnotherUser(nguoiDungDTO.getTenDangNhap(),
				nguoiDungDTO.getNguoiDungId());

		if (emailExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email đã tồn tại. Vui lòng chọn email khác.");
		}

		if (usernameExists) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
		}

		// Perform the update
		NguoiDungDTO updatedNguoiDung = nguoiDungService.updateNguoiDung(nguoiDungDTO);
		return ResponseEntity.ok(updatedNguoiDung);
	}

	// API để xóa người dùng
	@DeleteMapping("/admin/qltk/delete/{id}")
	public ResponseEntity<Void> deleteNguoiDung(@PathVariable("id") String nguoiDungId) {
		nguoiDungService.deleteNguoiDung(nguoiDungId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/admin/qltk/add")
	public ResponseEntity<Map<String, String>> themNguoiDung(@RequestBody ThemTaiKhoanDTO themTaiKhoanDTO) {
		Map<String, String> response = new HashMap<>();

		// Kiểm tra nếu email hoặc tên đăng nhập đã tồn tại
		boolean emailExists = nguoiDungService.emailExists(themTaiKhoanDTO.getEmail());
		boolean usernameExists = nguoiDungService.usernameExists(themTaiKhoanDTO.getTenDangNhap());

		if (emailExists) {
			response.put("error", "Email đã tồn tại. Vui lòng chọn email khác.");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

		if (usernameExists) {
			response.put("error", "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
		}

		// Kiểm tra nếu vai trò là Bác sĩ thì chuyên khoa ID không được để trống
		if ("BacSi".equals(themTaiKhoanDTO.getVaiTro()) && themTaiKhoanDTO.getChuyenKhoaId() == null) {
			response.put("error", "Vui lòng chọn chuyên khoa cho bác sĩ.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		// Thêm người dùng mới
		try {
			nguoiDungService.themNguoiDung(themTaiKhoanDTO);
			response.put("message", "Thêm người dùng thành công.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("error", "Lỗi khi thêm người dùng: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/admin/qlckb")
	public String openFormQuanLyCaKham(Model model) {

		return "admin/quanlycakham/quanlycakham"; // Tên của trang giao diện Thymeleaf

	}

	@GetMapping("/admin/qlckb/{date}")
	@ResponseBody
	public List<LichKhamBenhDTO> getLichKhamByDate(@PathVariable("date") String date) {
		LocalDate localDate = LocalDate.parse(date);
		List<LichKhamBenh> lichKhamBenhs = lichKhamBenhService.findAllByDate(localDate);

		return lichKhamBenhs.stream()
				.map(lichKham -> new LichKhamBenhDTO(lichKham.getMaLichKhamBenh(), lichKham.getBacSi().getBacSiId(),

						lichKham.getBacSi().getTen(), lichKham.getBacSi().getChuyenKhoa().getChuyenKhoaId(), // Mã
																												// chuyên
																												// khoa
						lichKham.getBacSi().getChuyenKhoa().getTen(), lichKham.getCa() // Ca khám bệnh
				)).collect(Collectors.toList());
	}

	@PostMapping("/admin/qlckb/addLichKham")
	public ResponseEntity<Map<String, Object>> addLichKhamBenh(@RequestBody QuanLyCaKhamDTO quanLyCaKhamDTO) {
		Map<String, Object> response = new HashMap<>();

		try {
			// Kiểm tra và tìm bác sĩ dựa trên ID từ DTO
			if (quanLyCaKhamDTO.getBacSiId() != null && !quanLyCaKhamDTO.getBacSiId().isEmpty()) {
				Optional<BacSi> optionalBacSi = bacSiService.findBacSiById(quanLyCaKhamDTO.getBacSiId());

				if (optionalBacSi.isPresent()) {
					BacSi bacSi = optionalBacSi.get();

					// Kiểm tra trùng lịch khám
					boolean exists = lichKhamBenhService.existsByNgayThangNamAndCaAndBacSi(
							quanLyCaKhamDTO.getNgayThangNam(), quanLyCaKhamDTO.getCa(), bacSi);

					if (exists) {
						response.put("status", "error");
						response.put("message", "Lịch khám cho bác sĩ đã tồn tại vào ngày "
								+ quanLyCaKhamDTO.getNgayThangNam() + " và ca " + quanLyCaKhamDTO.getCa());
						return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // Trả về 409 Conflict
					}

					// Tạo LichKhamBenh từ DTO
					LichKhamBenh lichKhamBenh = new LichKhamBenh();
					lichKhamBenh.setBacSi(bacSi);
					lichKhamBenh.setCa(quanLyCaKhamDTO.getCa());
					lichKhamBenh.setNgayThangNam(quanLyCaKhamDTO.getNgayThangNam());
					lichKhamBenh.setMaLichKhamBenh(UUID.randomUUID().toString());

					// Lưu LichKhamBenh
					lichKhamBenhService.addLichKhamBenh(lichKhamBenh);

					// Tạo phản hồi thành công
					response.put("status", "success");
					response.put("message", "Lịch khám đã được thêm thành công");
					response.put("details", "Bạn có thể kiểm tra lịch khám của bác sĩ tại trang quản lý lịch khám.");
					return ResponseEntity.ok(response); // Trả về 200 OK cho thành công
				} else {
					response.put("status", "error");
					response.put("message", "Bác sĩ không tồn tại với ID: " + quanLyCaKhamDTO.getBacSiId());
					return ResponseEntity.ok(response); // Trả về 200 OK cho lỗi này
				}
			} else {
				response.put("status", "error");
				response.put("message", "ID bác sĩ không hợp lệ");
				return ResponseEntity.ok(response); // Trả về 200 OK cho lỗi này
			}
		} catch (Exception e) {
			response.put("status", "error");
			response.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Trả về 500 Internal Server
																							// Error
		}
	}

	@DeleteMapping("/admin/qlckb/deleteLichKham/{maLichKhamBenh}")
	public ResponseEntity<Map<String, Object>> deleteLichKhamBenh(@PathVariable String maLichKhamBenh) {
		// Chuyển đổi UUID thành long.
		// Ở đây tôi sẽ lấy giá trị long từ phần đầu của UUID.

		Map<String, Object> response = new HashMap<>();
		if (lichKhamBenhService.deleteLichKhamBenh(maLichKhamBenh)) {
			response.put("success", true);
			response.put("message", "Lịch khám đã được xóa thành công.");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "Không tìm thấy lịch khám để xóa.");
			return ResponseEntity.status(404).body(response);
		}
	}

	@GetMapping("/admin/qlbs")
	public String qlbs(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String search,
			Model model) {
		int pageSize = 10; // Số bác sĩ trên mỗi trang
		int visiblePages = 4; // Số trang hiển thị trên thanh phân trang

		Page<BacSi> doctorPage;
		if (search != null && !search.isEmpty()) {
			doctorPage = bacSiService.searchByPhone(search, PageRequest.of(page, pageSize));
		} else {
			doctorPage = bacSiService.getDoctorsPaginated(page, pageSize);
		}

		int totalPages = doctorPage.getTotalPages();

		// Tính toán startPage và endPage để cố định số trang hiển thị
		int startPage = Math.max(0, page - visiblePages / 2);
		int endPage = Math.min(startPage + visiblePages - 1, totalPages - 1);

		// Điều chỉnh nếu ở cuối danh sách trang
		if (endPage - startPage < visiblePages - 1) {
			startPage = Math.max(0, endPage - visiblePages + 1);
		}

		model.addAttribute("doctors", doctorPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("search", search);
		model.addAttribute("noResults", doctorPage.getTotalElements() == 0);

		return "admin/quanlybacsi/quanlybacsi";
	}

	@GetMapping("/admin/qlck")
	public String qlck(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String ten,
			Model model) {
		int pageSize = 4; // Số chuyên khoa mỗi trang
		int visiblePages = 4; // Số trang hiển thị trên thanh phân trang

		Page<ChuyenKhoa> chuyenKhoaPage;

		if (ten.isEmpty()) {
			chuyenKhoaPage = chuyenKhoaService.getChuyenKhoasPaginated(page, pageSize);
		} else {
			chuyenKhoaPage = chuyenKhoaService.searchChuyenKhoasPaginated(ten, page, pageSize);
		}

		int totalPages = chuyenKhoaPage.getTotalPages();

		// Tính toán startPage và endPage để cố định số trang hiển thị
		int startPage = Math.max(0, page - visiblePages / 2);
		int endPage = Math.min(startPage + visiblePages - 1, totalPages - 1);

		// Điều chỉnh nếu ở cuối danh sách trang
		if (endPage - startPage < visiblePages - 1) {
			startPage = Math.max(0, endPage - visiblePages + 1);
		}

		model.addAttribute("chuyenkhoas", chuyenKhoaPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("searchQuery", ten);
		model.addAttribute("noResults", chuyenKhoaPage.getTotalElements() == 0);

		return "admin/quanlychuyenkhoa/quanlychuyenkhoa";
	}

	@GetMapping("/admin/qlt")
	public String qlt(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) String searchType,
			@RequestParam(defaultValue = "") String query, Model model) {
		int pageSize = 8; // Số thuốc mỗi trang
		int visiblePages = 4; // Số trang hiển thị trên thanh phân trang (giống qlbs)

		Page<Thuoc> thuocPage;

		if (query.isEmpty()) {
			thuocPage = thuocService.getThuocsPaginated(page, pageSize);
		} else {
			if ("nhaCungCap".equals(searchType)) {
				thuocPage = thuocService.searchThuocsByNhaCungCapPaginated(query, page, pageSize);
			} else {
				thuocPage = thuocService.searchThuocsPaginated(query, page, pageSize);
			}
		}

		int totalPages = thuocPage.getTotalPages();

		// Tính toán startPage và endPage để cố định số trang hiển thị
		int startPage = Math.max(0, page - visiblePages / 2);
		int endPage = Math.min(startPage + visiblePages - 1, totalPages - 1);

		// Điều chỉnh nếu ở cuối danh sách trang
		if (endPage - startPage < visiblePages - 1) {
			startPage = Math.max(0, endPage - visiblePages + 1);
		}

		model.addAttribute("thuocs", thuocPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("searchQuery", "ten".equals(searchType) ? query : "");
		model.addAttribute("searchNhaCungCap", "nhaCungCap".equals(searchType) ? query : "");
		model.addAttribute("noResults", thuocPage.isEmpty());

		return "admin/quanlythuoc/quanlythuoc";
	}

	@GetMapping("/admin/qlbs/edit-bacsi/{bacSiId}")
	public String showUpdateForm(@PathVariable("bacSiId") String bacSiId, Model model) {
		BacSi doctor = bacSiService.getDoctorById(bacSiId);
		// Chuyển đổi ngày sinh nếu cần
		String formattedDate = doctor.getNgaySinh().toString();

		model.addAttribute("doctor", doctor);
		model.addAttribute("chuyenKhoas", chuyenKhoaService.getAllChuyenKhoa());
		model.addAttribute("formattedDate", formattedDate);
		return "admin/quanlybacsi/editbacsi";
	}

	@PostMapping("/admin/qlbs/update/{bacSiId}")
	public String updateDoctor(@PathVariable("bacSiId") String bacSiId, @ModelAttribute BacSi updatedDoctor,
			@ModelAttribute ChiTietBacSi updatedChiTiet,
			@RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
			@RequestParam(value = "redirect", required = false) String redirectPage) {
		try {
			// Kiểm tra và lưu file ảnh nếu có
			if (avatarFile != null && !avatarFile.isEmpty()) {
				String uploadDir = "uploads/";
				Path uploadPath = Paths.get(uploadDir);
				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}
				String fileName = UUID.randomUUID().toString() + "_" + avatarFile.getOriginalFilename();
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				updatedDoctor.setUrlAvatar("/uploads/" + fileName);
			}

			// Cập nhật thông tin bác sĩ và chi tiết bác sĩ
			bacSiService.updateDoctor(bacSiId, updatedDoctor, updatedChiTiet);
		} catch (IOException e) {
			e.printStackTrace();
			return "error";
		}

		// Kiểm tra nếu cần chuyển hướng đến trang edit chi tiết
		if ("edit-details".equals(redirectPage)) {
			return "redirect:/admin/qlbs/edit-chitiet/" + bacSiId;
		}

		return "redirect:/admin/qlbs";
	}

	@PostMapping("/admin/delete-thuoc")
	public String deleteThuoc(@RequestParam Long thuocId) {
		thuocService.deleteThuoc(thuocId);
		return "redirect:/admin/qlt"; // Chuyển hướng lại trang quản lý thuốc sau khi xóa
	}

	@GetMapping("/admin/edit-thuoc")
	public String editThuoc(@RequestParam Long thuocId, Model model) {
		Thuoc thuoc = thuocService.getThuocById(thuocId);
		model.addAttribute("thuoc", thuoc);
		return "admin/quanlythuoc/editthuoc"; // Trang cập nhật thuốc
	}

	@PostMapping("/admin/update-thuoc")
	public String updateThuoc(Thuoc thuoc) {
		thuocService.updateThuoc(thuoc);
		return "redirect:/admin/qlt"; // Chuyển hướng lại trang quản lý thuốc sau khi cập nhật
	}

	// Hiển thị trang thêm thuốc
	@GetMapping("/admin/add-thuoc")
	public String showAddThuocForm(Model model) {
		return "admin/quanlythuoc/addthuoc"; // Trả về trang thêm thuốc
	}

	@PostMapping("/admin/add-thuoc")
	public String addThuoc(@ModelAttribute Thuoc thuoc) {
		thuocService.saveThuoc(thuoc); // Gọi service để lưu thuốc
		return "redirect:/admin/qlt"; // Quay lại trang danh sách thuốc
	}

	// AdminController.java
	@GetMapping("/admin/add-chuyenkhoa")
	public String showAddChuyenKhoaForm(Model model) {
		return "admin/quanlychuyenkhoa/addchuyenkhoa"; // Trả về trang thêm chuyên khoa
	}

	@PostMapping("/admin/add-chuyenkhoa")
	public String addChuyenKhoa(@ModelAttribute ChuyenKhoa chuyenKhoa) {
		if (chuyenKhoa.getChuyenKhoaId() == null) {
			chuyenKhoa.setChuyenKhoaId(UUID.randomUUID().toString()); // Tạo UUID mới cho id nếu nó null
		}
		chuyenKhoaService.saveChuyenKhoa(chuyenKhoa); // Gọi service để lưu chuyên khoa
		return "redirect:/admin/qlck"; // Quay lại trang danh sách chuyên khoa
	}

	// Hiển thị form chỉnh sửa chuyên khoa
	@GetMapping("/admin/qlck/edit-chuyenkhoa/{chuyenKhoaId}")
	public String showEditChuyenKhoaForm(@PathVariable("chuyenKhoaId") String chuyenKhoaId, Model model) {
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
		model.addAttribute("chuyenKhoa", chuyenKhoa);
		return "admin/quanlychuyenkhoa/editchuyenkhoa"; // Đường dẫn đến trang chỉnh sửa chuyên khoa
	}

	@PostMapping("/admin/qlck/update-chuyenkhoa")
	public String updateChuyenKhoa(@RequestParam String chuyenKhoaId, @RequestParam String ten,
			@RequestParam String moTa) {
		// Lấy chuyên khoa từ cơ sở dữ liệu dựa trên ID
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
		if (chuyenKhoa != null) {
			chuyenKhoa.setTen(ten); // Cập nhật tên chuyên khoa
			chuyenKhoa.setMoTa(moTa);
			chuyenKhoaService.updateChuyenKhoa(chuyenKhoa); // Gọi service để cập nhật
		}
		return "redirect:/admin/qlck"; // Chuyển hướng về trang danh sách chuyên khoa
	}

	@GetMapping("/admin/qlbs/edit-chitiet/{bacSiId}")
	public String showEditDoctorDetailsForm(@PathVariable("bacSiId") String bacSiId, Model model) {
		BacSi doctor = bacSiService.getDoctorById(bacSiId);
		model.addAttribute("doctor", doctor);
		model.addAttribute("chiTietBacSi", doctor.getChiTietBacSi());
		return "admin/quanlybacsi/editchitietbacsi"; // Trang mới để chỉnh sửa chi tiết bác sĩ
	}

	@PostMapping("/admin/qlbs/update-chitiet/{bacSiId}")
	public String updateDoctorDetails(@PathVariable("bacSiId") String bacSiId, ChiTietBacSi updatedChiTiet) {
		bacSiService.updateDoctorDetails(bacSiId, updatedChiTiet); // Viết một phương thức mới trong BacSiService để xử
																	// lý cập nhật
		return "redirect:/admin/qlbs"; // Quay lại danh sách bác sĩ sau khi cập nhật
	}

	// Hiển thị danh sách loại xét nghiệm với phân trang và tìm kiếm
	@GetMapping("/admin/qlxn")
	public String getAllLoaiXetNghiem(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "") String search, Model model) {
		int pageSize = 8; // Số loại xét nghiệm mỗi trang
		int visiblePages = 4; // Số trang hiển thị trên thanh phân trang

		Page<LoaiXetNghiem> loaiXetNghiemPage;

		if (search.isEmpty()) {
			loaiXetNghiemPage = loaiXetNghiemService.getLoaiXetNghiemsPaginated(page, pageSize);
		} else {
			loaiXetNghiemPage = loaiXetNghiemService.searchLoaiXetNghiemsPaginated(search, page, pageSize);
		}

		int totalPages = loaiXetNghiemPage.getTotalPages();

		// Tính toán startPage và endPage để cố định số trang hiển thị
		int startPage = Math.max(0, page - visiblePages / 2);
		int endPage = Math.min(startPage + visiblePages - 1, totalPages - 1);

		// Điều chỉnh nếu ở cuối danh sách trang
		if (endPage - startPage < visiblePages - 1) {
			startPage = Math.max(0, endPage - visiblePages + 1);
		}

		model.addAttribute("loaiXetNghiems", loaiXetNghiemPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("searchQuery", search);
		model.addAttribute("noResults", loaiXetNghiemPage.isEmpty());

		return "admin/quanlyloaixetnghiem/quanlyloaixetnghiem";
	}

	// Hiển thị form thêm loại xét nghiệm
	@GetMapping("/admin/add-loaixetnghiem")
	public String showAddLoaiXetNghiemForm(Model model) {
		model.addAttribute("loaiXetNghiem", new LoaiXetNghiem());
		return "admin/quanlyloaixetnghiem/addloaixetnghiem";
	}

	// Thêm loại xét nghiệm mới
	@PostMapping("/admin/add-loaixetnghiem")
	public String addLoaiXetNghiem(@ModelAttribute LoaiXetNghiem loaiXetNghiem) {
		loaiXetNghiemService.saveLoaiXetNghiem(loaiXetNghiem);
		return "redirect:/admin/qlxn";
	}

	// Hiển thị form chỉnh sửa loại xét nghiệm
	@GetMapping("/admin/edit-loaixetnghiem")
	public String showEditLoaiXetNghiemForm(@RequestParam Long id, Model model) {
		LoaiXetNghiem loaiXetNghiem = loaiXetNghiemService.getLoaiXetNghiemById(id);
		model.addAttribute("loaiXetNghiem", loaiXetNghiem);
		return "admin/quanlyloaixetnghiem/editloaixetnghiem";
	}

	// Cập nhật loại xét nghiệm
	@PostMapping("/admin/update-loaixetnghiem")
	public String updateLoaiXetNghiem(@ModelAttribute LoaiXetNghiem loaiXetNghiem) {
		loaiXetNghiemService.updateLoaiXetNghiem(loaiXetNghiem);
		return "redirect:/admin/qlxn";
	}

	@PostMapping("/admin/delete-loaixetnghiem")
	public String deleteLoaiXetNghiem(@RequestParam Long id, RedirectAttributes redirectAttributes) {
		if (loaiXetNghiemService.deleteLoaiXetNghiem(id)) {
			redirectAttributes.addFlashAttribute("message", "Loại xét nghiệm đã được xóa thành công.");
			return "redirect:/admin/qlxn";
		} else {
			redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại xét nghiệm để xóa.");
			return "redirect:/admin/qlxn";
		}
	}

	@DeleteMapping("/admin/qlck/delete/{chuyenKhoaId}")
	@ResponseBody
	public String deleteChuyenKhoa(@PathVariable String chuyenKhoaId) {
		try {

			chuyenKhoaService.delete(chuyenKhoaId);
			return "success";
		} catch (DataIntegrityViolationException e) {
			// Bắt lỗi vi phạm khóa ngoại
			return "error: Không xóa được vì có dữ liệu liên quan";
		} catch (Exception e) {
			// Bắt các lỗi khác
			return "error: " + e.getMessage();
		}
	}

	// ----------
	@GetMapping("/admin/statistics")
	public String getStatisticsPage(Model model) {
		return "admin/statistics/statistics";
	}

	@GetMapping("/admin/statistics/appointments")
	public ResponseEntity<Map<String, Map<String, Long>>> getAppointmentStatistics(@RequestParam String period,
			@RequestParam(required = false) String date, @RequestParam(required = false) String month,
			@RequestParam(required = false) String year) {
		Map<String, Map<String, Long>> stats = statisticsService.getAppointmentStatistics(period, date, month, year);
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/admin/statistics/revenue")
	public ResponseEntity<Map<String, Long>> getRevenueStatistics(@RequestParam String period,
			@RequestParam(required = false) String date, @RequestParam(required = false) String month,
			@RequestParam(required = false) String year) {
		Map<String, Long> stats = statisticsService.getRevenueStatistics(period, date, month, year);
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/admin/statistics/test-usage")
	public ResponseEntity<Map<String, Long>> getTestUsageStatistics(@RequestParam String period,
			@RequestParam(required = false) String date, @RequestParam(required = false) String month,
			@RequestParam(required = false) String year, @RequestParam String sort) {
		Map<String, Long> stats = statisticsService.getTestUsageStatistics(period, date, month, year, sort);
		return ResponseEntity.ok(stats);
	}
	   @GetMapping("/admin/statistics/revisit-rate")
	    public ResponseEntity<?> getRevisitRateStatistics(
	            @RequestParam String period,
	            @RequestParam(required = false) String date,
	            @RequestParam(required = false) String month,
	            @RequestParam(required = false) String year) {
	        try {
	            Map<String, Map<String, Number>> stats = statisticsService.getRevisitRateStatistics(period, date, month, year);
	            return ResponseEntity.ok(stats);
	        } catch (Exception e) {
	            Map<String, String> error = new HashMap<>();
	            error.put("error", "Failed to fetch revisit rate statistics: " + e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	        }
	    }
}
