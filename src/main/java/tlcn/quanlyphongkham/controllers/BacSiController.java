package tlcn.quanlyphongkham.controllers;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.transaction.Transactional;
import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.BenhNhanOfTaoDonThuocDTO;
import tlcn.quanlyphongkham.dtos.ChiTietBacSiDTO;
import tlcn.quanlyphongkham.dtos.EditProfileBSDTO;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.dtos.LichHenKhamDTO;
import tlcn.quanlyphongkham.dtos.MedicalHistoryDTO;
import tlcn.quanlyphongkham.dtos.SlotDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.DonThuoc;
import tlcn.quanlyphongkham.entities.DonThuocThuoc;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.LoaiXetNghiem;
import tlcn.quanlyphongkham.entities.PhieuXetNghiem;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.entities.VitalSigns;
import tlcn.quanlyphongkham.entities.XetNghiem;
import tlcn.quanlyphongkham.security.CustomUserDetails;
import tlcn.quanlyphongkham.services.AppointmentService;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.DonThuocService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.LoaiXetNghiemService;
import tlcn.quanlyphongkham.services.PhieuXetNghiemService;
import tlcn.quanlyphongkham.services.SlotThoiGianService;
import tlcn.quanlyphongkham.services.ThuocService;
import tlcn.quanlyphongkham.services.VitalSignsService;
import tlcn.quanlyphongkham.services.XetNghiemService;

@Controller
public class BacSiController {
	@Autowired
	private ChuyenKhoaService chuyenKhoaService;
	@Autowired
	private BacSiService bacSiService;
	@Autowired
	private LichKhamBenhService lichKhamBenhService;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private HoSoBenhService hoSoBenhService;
	@Autowired
	private ThuocService thuocService;
	@Autowired
	private DonThuocService donThuocService;
	
	@Autowired
	private BenhNhanService benhNhanService;
	
	@Autowired
    private VitalSignsService vitalSignsService;
	
	@Autowired
    private SlotThoiGianService slotThoiGianService;
	

    @Autowired
    private XetNghiemService xetNghiemService;

    @Autowired
    private PhieuXetNghiemService phieuXetNghiemService;

    @Autowired
    private LoaiXetNghiemService loaiXetNghiemService;
    
    @Value("${file.upload-dir:${user.dir}/uploads/xetnghiem}")
    private String uploadDir;

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

	@GetMapping("/chuyenkhoa")
	public ResponseEntity<List<ChuyenKhoa>> getAllChuyenKhoa() {
		List<ChuyenKhoa> specialties = chuyenKhoaService.getAllChuyenKhoa();
		return ResponseEntity.ok(specialties);
	}

	@GetMapping("/bacsi")
	public ResponseEntity<List<BacSiDTO>> getBacSiByChuyenKhoa(@RequestParam("chuyenKhoaId") String chuyenKhoaId) {
		List<BacSiDTO> doctors = bacSiService.getBacSiByChuyenKhoa(chuyenKhoaId);
		return ResponseEntity.ok(doctors);
	}

	@GetMapping("/bacsi/lichkham/viewck")
	public String getLichKhamBenhTheoNgay(@RequestParam(value = "ngay", required = false) LocalDate ngay, Model model) {

		nguoiDungId = getNguoiDungId();
		BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
		String idBacSi = bacSi.getBacSiId();
		if (ngay == null) {
			ngay = LocalDate.now();
		}

		List<LichKhamBenh> lichKhamBenhList = lichKhamBenhService.getLichKhamBenhByNgayAndBacSi(idBacSi, ngay);
		String tenBacSi = bacSiService.getTenBacSi(idBacSi);

		model.addAttribute("lichKhamBenhList", lichKhamBenhList);

		model.addAttribute("ngay", ngay);
		model.addAttribute("tenBacSi", tenBacSi);

		return "bacsi/xemcakhambs/xemcakhambs";
	}

	@PostMapping("/bacsi/lichkham/add")
	public ResponseEntity<Map<String, String>> addLichKham(@RequestParam("ca") String ca,
			@RequestParam("ngay") LocalDate ngay) {
		try {
			nguoiDungId = getNguoiDungId();
			BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
			String idBacSi = bacSi.getBacSiId(); // ID của bác sĩ

			// Kiểm tra xem lịch khám đã tồn tại chưa
			boolean exists = lichKhamBenhService.isLichKhamExist(idBacSi, ngay, ca);

			if (exists) {
				Map<String, String> response = new HashMap<>();
				response.put("status", "error");
				response.put("message", "Lịch khám đã tồn tại cho buổi này");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}

			// Nếu chưa tồn tại, thêm lịch khám
			lichKhamBenhService.saveLichKhamBS(idBacSi, ngay, ca);

			Map<String, String> successResponse = new HashMap<>();
			successResponse.put("status", "success");
			successResponse.put("message", "Lịch khám đã được thêm thành công");
			return ResponseEntity.ok(successResponse);

		} catch (Exception e) {
			e.printStackTrace(); // In ra chi tiết lỗi để kiểm tra
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("status", "error");
			errorResponse.put("message", "Lỗi khi thêm lịch khám");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	// Phương thức xóa lịch khám
	@DeleteMapping("/bacsi/lichkham/delete")
	public ResponseEntity<String> deleteLichKham(@RequestParam("id") String lichId) {
		try {
			lichKhamBenhService.deleteLichKham(lichId);
			return ResponseEntity.ok("Lịch khám đã được xóa thành công");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xóa lịch khám");
		}
	}

	@GetMapping("/bacsi/editprofile")
	public String editProfile(Model model) {
		nguoiDungId = getNguoiDungId();
		BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
		String bacSiId = bacSi.getBacSiId(); // Example, replace with dynamic ID

		// Fetch personal info and details
		EditProfileBSDTO personalInfo = bacSiService.getProfile(bacSiId);
		ChiTietBacSiDTO detailInfo = bacSiService.getDetail(bacSiId);

		// Fetch available specialties
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		System.out.println("Avatar URL: " + personalInfo.getAvatarurl());

		model.addAttribute("personalInfo", personalInfo);
		model.addAttribute("detailInfo", detailInfo);
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);

		return "bacsi/chinhsuathongtinbs/chinhsuathongtinbs"; // Your Thymeleaf template
	}

	
	

	@PostMapping("/updateProfile")
	public String updateProfile(@ModelAttribute EditProfileBSDTO profileDTO,
	                            @ModelAttribute ChiTietBacSiDTO detailDTO,
	                            @RequestParam("avatar") MultipartFile avatar,
	                            Model model) throws IOException {
	    String bacSiId = profileDTO.getBacSiId();

	    if (!avatar.isEmpty()) {
	        // Tạo tên file mới
	        String fileName = bacSiId + "-" + avatar.getOriginalFilename();

	        // Đường dẫn lưu file: thư mục /uploads nằm cùng cấp với file .jar
	        String uploadDir = new File("uploads").getAbsolutePath();

	        // Tạo thư mục nếu chưa tồn tại
	        File directory = new File(uploadDir);
	        if (!directory.exists()) {
	            directory.mkdirs();
	        }

	        // Lưu file vào thư mục uploads
	        java.nio.file.Path filePath = Paths.get(uploadDir, fileName);
	        Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        // Đặt URL để load ảnh
	        profileDTO.setAvatarurl("/uploads/" + fileName);
	    }

	    bacSiService.updateProfile(profileDTO);
	    bacSiService.updateDetail(detailDTO);

	    return "redirect:/bacsi/editprofile";
	}


	// Helper method to save the avatar image and return its URL

	@GetMapping("/bacsi/xemlichhen")
	public String getLichKham(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model) {
		if (date == null) {
			date = LocalDate.now(); // Nếu không có date, mặc định là ngày hiện tại
		}

		nguoiDungId = getNguoiDungId();
		BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
		String bacSiId = bacSi.getBacSiId();
		// Lấy danh sách lịch khám theo bác sĩ và ngày
		List<LichKhamBenh> lichKhamList = lichKhamBenhService.getLichKhamByBacSiAndDate(bacSiId, date);

		// Tạo danh sách LichKhamDTO
		List<LichHenKhamDTO> lichKhamDTOList = new ArrayList<>();
		for (LichKhamBenh lichKham : lichKhamList) {
			// Lấy danh sách slot thời gian cho từng lịch khám
			List<SlotThoiGian> slotThoiGianList = lichKhamBenhService
					.getSlotThoiGianByLichKham(lichKham.getMaLichKhamBenh());
			// Tạo LichKhamDTO và thêm vào danh sách
			lichKhamDTOList.add(new LichHenKhamDTO(lichKham.getMaLichKhamBenh(), lichKham.getNgayThangNam(),
					lichKham.getCa(), slotThoiGianList));
		}
		// Gửi dữ liệu lên giao diện
		model.addAttribute("lichKhamDTOList", lichKhamDTOList);
		model.addAttribute("date", date);
		return "bacsi/xemlichhen/xemlichhen";
	}

	@PostMapping("/bacsi/update-appointment-status")
	public ResponseEntity<Map<String, String>> updateAppointmentStatus(@RequestBody Map<String, String> request) {

		String slotId = request.get("slotId");
		String date = request.get("date");
		String time = request.get("time");
		String status = request.get("status");

		boolean success = appointmentService.updateStatus(slotId, date, time, status);

		Map<String, String> response = new HashMap<>();
		response.put("success", success ? "true" : "false");
		return ResponseEntity.ok(response);
	}

	@GetMapping("/bacsi/xemlichhen/data")
	@ResponseBody
	public List<SlotDTO> getLichHenRealTime(
	        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	    if (date == null) {
	        date = LocalDate.now(); // Mặc định ngày hiện tại nếu không có
	    }

	    String nguoiDungId = getNguoiDungId(); // Lấy từ session hoặc token
	    BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
	    String bacSiId = bacSi.getBacSiId();

	    // Lấy lịch khám của bác sĩ theo ngày
	    List<LichKhamBenh> lichKhamList = lichKhamBenhService.getLichKhamByBacSiAndDate(bacSiId, date);

	    // Chuẩn bị danh sách slot trả về
	    List<SlotDTO> result = new ArrayList<>();
	    for (LichKhamBenh lichKham : lichKhamList) {
	        List<SlotThoiGian> slotList = lichKhamBenhService.getSlotThoiGianByLichKham(lichKham.getMaLichKhamBenh());
	        for (SlotThoiGian slot : slotList) {
	            result.add(new SlotDTO(slot.getSlotId(), slot.getThoiGianBatDau(), slot.getTrangThai()));
	        }
	    }

	    return result;
	}


	
	@GetMapping("/bacsi/quanlytaodonthuoc")
	public String getAllPrescriptions(Model model, 
	        @RequestParam(value = "phone", required = false) String phone,
	        @RequestParam(value = "filterDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate filterDate,
	        @RequestParam(value = "page", defaultValue = "0") int page) {

	    if (phone == null || phone.trim().isEmpty()) {
	        // Nếu chưa nhập số điện thoại, không hiển thị thông tin
	        model.addAttribute("donThuocs", Collections.emptyList());
	        model.addAttribute("selectedBenhNhan", null);
	        return "bacsi/taodonthuoc/quanlytaodonthuoc";
	    }

	    // Tìm bệnh nhân theo số điện thoại
	    BenhNhan benhNhan = benhNhanService.findByPhone(phone.trim());
	    if (benhNhan == null) {
	        // Không tìm thấy bệnh nhân
	        model.addAttribute("donThuocs", Collections.emptyList());
	        model.addAttribute("selectedBenhNhan", null);
	        model.addAttribute("errorMessage", "Không tìm thấy bệnh nhân với số điện thoại này.");
	        return "bacsi/taodonthuoc/quanlytaodonthuoc";
	    }

	    // Thiết lập phân trang
	    Pageable pageable = PageRequest.of(page, 4);
	    
	    // Lọc theo ngày tháng năm nếu có chọn
	    Page<DonThuoc> donThuocPage;
	    if (filterDate != null) {
	        donThuocPage = donThuocService.findByBenhNhanIdAndDate(benhNhan.getBenhNhanId(), filterDate, pageable);
	    } else {
	        donThuocPage = donThuocService.findByBenhNhanId(benhNhan.getBenhNhanId(), pageable);
	    }

	    // Xử lý dữ liệu đơn thuốc trước khi hiển thị
	    for (DonThuoc donThuoc : donThuocPage.getContent()) {
	        donThuoc.calculateTongTien();
	        donThuoc.setFormattedDate(
	                donThuoc.getHoSoBenh().getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
	    }

	    // Thêm dữ liệu vào model
	    model.addAttribute("selectedBenhNhan", benhNhan);
	    model.addAttribute("donThuocs", donThuocPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", donThuocPage.getTotalPages());
	    model.addAttribute("phone", phone); // Giữ lại số điện thoại khi tìm kiếm
	    model.addAttribute("filterDate", filterDate); // Giữ lại ngày lọc khi tìm kiếm

	    return "bacsi/taodonthuoc/quanlytaodonthuoc";
	}



	@PostMapping("/bacsi/xoadonthuoc/{id}")
	public String deletePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    try {
	        // Gọi service để xóa đơn thuốc và hồ sơ bệnh
	        donThuocService.deleteDonThuocAndHoSoBenhById(id);
	        redirectAttributes.addFlashAttribute("successMessage", "Xóa đơn thuốc và hồ sơ bệnh thành công!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Xóa đơn thuốc hoặc hồ sơ bệnh thất bại!");
	    }
	    return "redirect:/bacsi/quanlytaodonthuoc";
	}

	@GetMapping("/bacsi/capnhatdonthuoc")
	public String showUpdateForm(@RequestParam("id") Long donThuocId, Model model) {
		DonThuoc donThuoc = donThuocService.findById(donThuocId);
		if (donThuoc == null) {
			model.addAttribute("errorMessage", "Không tìm thấy đơn thuốc.");
			return "redirect:/bacsi/quanlytaodonthuoc";
		}

		// Lấy thông tin bệnh nhân từ hồ sơ bệnh
		HoSoBenh hoSoBenh = donThuoc.getHoSoBenh();
		BenhNhan benhNhan = hoSoBenh != null ? hoSoBenh.getBenhNhan() : null;

		// Thêm vào model
		model.addAttribute("donThuoc", donThuoc);
		model.addAttribute("benhNhan", benhNhan); // Thêm bệnh nhân vào model
		return "bacsi/taodonthuoc/capnhatdonthuoc";
	}

	@PostMapping("/bacsi/capnhatdonthuoc")
	@ResponseBody
	public ResponseEntity<Map<String, String>> updateDonThuoc(
	        @RequestParam("donThuocId") Long donThuocId,
	        @RequestParam("hoSoId") String hoSoId,
	        @RequestParam("chanDoan") String chanDoan,
	        @RequestParam("trieuChung") String trieuChung,
	        @RequestParam("benhNhanId") String benhNhanId,
	        @RequestParam(value = "drugIds[]", required = false) List<Long> drugIds,
	        @RequestParam(value = "lieu[]", required = false) List<String> lieu,
	        @RequestParam(value = "tanSuat[]", required = false) List<String> tanSuat,
	        @RequestParam(value = "soLuong[]", required = false) List<Integer> soLuong, // Thêm số lượng
	        @RequestParam(value = "removedDrugIds", required = false) List<Long> removedDrugIds) {

	    Map<String, String> response = new HashMap<>();
	    try {
	        // Gọi service để cập nhật đơn thuốc
	        donThuocService.updateDonThuoc(donThuocId, hoSoId, chanDoan, benhNhanId, drugIds, lieu, tanSuat, soLuong, removedDrugIds,trieuChung);
	        response.put("status", "success");
	        response.put("message", "Cập nhật đơn thuốc thành công.");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", "Có lỗi xảy ra: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}


	@GetMapping("/bacsi/taodonthuoc")
	public String showCreatePrescriptionPage(Model model) {
		List<Thuoc> drugs = thuocService.getAllThuoc(); // Giả sử bạn có một ThuocService để lấy tất cả các loại thuốc
		model.addAttribute("drugs", drugs);
		return "bacsi/taodonthuoc/taodonthuoc";
	}

	@GetMapping("/bacsi/searchBenhNhanByPhone")
	public ResponseEntity<List<BenhNhanOfTaoDonThuocDTO>> searchBenhNhanByPhone(@RequestParam("phone") String phone) {
		List<BenhNhanOfTaoDonThuocDTO> patients = hoSoBenhService.getBenhNhanInfoByDienThoai(phone);
		return ResponseEntity.ok(patients);
	}

	@PostMapping("/bacsi/taodonthuoc")
	@ResponseBody
	public ResponseEntity<Map<String, String>> createPrescription(
	        @RequestParam("chanDoan") String chanDoan,
	        @RequestParam("drugIds") List<Long> drugIds,
	        @RequestParam("lieu") List<String> lieu,
	        @RequestParam("trieuChung") String trieuChung,	
	        @RequestParam("tanSuat") List<String> tanSuat,
	        @RequestParam("soLuong") List<Integer> soLuong,
	        @RequestParam("benhNhanId") String benhNhanId) {

	    nguoiDungId = getNguoiDungId();
	    BacSi bacSi1 = bacSiService.findByNguoiDungId(nguoiDungId);
	    String fixedBacSiId = bacSi1.getBacSiId();
	    BacSi bacSi = bacSiService.findById(fixedBacSiId);

	    Map<String, String> response = new HashMap<>();

	    // Kiểm tra bệnh nhân
	    BenhNhan benhNhan = hoSoBenhService.findPatientById(benhNhanId);
	    if (benhNhan == null) {
	        response.put("status", "error");
	        response.put("message", "Không tìm thấy thông tin bệnh nhân, Kiểm tra lại số điện thoại!");
	        return ResponseEntity.badRequest().body(response);
	    }

	    try {
	        // Tạo hồ sơ bệnh
	        HoSoBenh hoSoBenh = new HoSoBenh();
	        hoSoBenh.setHoSoId(UUID.randomUUID().toString());
	        hoSoBenh.setChanDoan(chanDoan);
	        hoSoBenh.setBenhNhan(benhNhan);
	        hoSoBenh.setTrieuChung(trieuChung);
	        hoSoBenh.setBacSi(bacSi);
	        hoSoBenhService.save(hoSoBenh);

	        // Tạo đơn thuốc
	        DonThuoc donThuoc = new DonThuoc();
	        donThuoc.setHoSoBenh(hoSoBenh);

	        List<DonThuocThuoc> donThuocThuocs = new ArrayList<>();
	        for (int i = 0; i < drugIds.size(); i++) {
	            Long thuocId = drugIds.get(i);
	            Thuoc thuoc = thuocService.findThuocById(thuocId);

	            // Kiểm tra số lượng thuốc trong kho
	            Integer requestedQuantity = soLuong.get(i);
	            if (thuoc.getSoLuong() < requestedQuantity) {
	                response.put("status", "error");
	                response.put("message", "Số lượng thuốc " + thuoc.getTen() + " trong kho không đủ.");
	                return ResponseEntity.badRequest().body(response);
	            }

	            // Giảm số lượng thuốc trong kho
	            thuoc.setSoLuong(thuoc.getSoLuong() - requestedQuantity);
	            thuocService.updateThuoc(thuoc);

	            // Thêm thuốc vào đơn thuốc
	            DonThuocThuoc donThuocThuoc = new DonThuocThuoc();
	            donThuocThuoc.setDonThuoc(donThuoc);
	            donThuocThuoc.setThuoc(thuoc);
	            donThuocThuoc.setLieu(lieu.get(i));
	            donThuocThuoc.setTanSuat(tanSuat.get(i));
	            donThuocThuoc.setSoLuong(requestedQuantity);

	            donThuocThuocs.add(donThuocThuoc);
	        }

	        donThuoc.setDonThuocThuocs(donThuocThuocs);
	        donThuocService.save(donThuoc);

	        response.put("status", "success");
	        response.put("message", "Đơn thuốc đã được tạo thành công!");
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", "Đã xảy ra lỗi trong quá trình tạo đơn thuốc.");
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}


	@GetMapping("/bacsi/searchpatient")
	public String searchPatientById(@RequestParam("id") String patientId, Model model) {
		BenhNhan patient = hoSoBenhService.findPatientById(patientId);
		model.addAttribute("patient", patient);
		return "bacsi/timkiembenhnhan/timkiembenhnhan";
	}

	@GetMapping("/bacsi/searchDrugs")
	@ResponseBody
	public List<Thuoc> searchDrugs(@RequestParam("name") String name) {
		// Tìm kiếm thuốc trong dịch vụ (ví dụ từ cơ sở dữ liệu)
		return donThuocService.searchDrugs(name);
	}

	@GetMapping("/bacsi/lichsukham")
	public String getLichSuKham(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "5") int size, Model model) {

		nguoiDungId = getNguoiDungId();
		BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
		String bacSiId = bacSi.getBacSiId();

		Page<HoSoBenhDTO> hoSoBenhPage = Page.empty();

		if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
			LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

			LocalDateTime startDateTime = startLocalDate.atStartOfDay();
			LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

			Pageable pageable = PageRequest.of(page, size);

			hoSoBenhPage = hoSoBenhService.getHoSoBenhWithDonThuocByDateRangeAndDoctor(startDateTime, endDateTime,
					bacSiId, pageable);
		}

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("hoSoBenhPage", hoSoBenhPage);

		return "bacsi/lichsukham/lichsukham";
	}
	/*///////////////////////////////////////////////////////////////////*/
	
	
	
	@GetMapping("bacsi/step1")
    public String step1(@RequestParam("benhNhanId") String benhNhanId, @RequestParam(value = "hoSoId", required = false) String hoSoId,@RequestParam(value = "slotId", required = false) String slotId, Model model) {
        try {
            BenhNhan benhNhan = benhNhanService.findByBenhNhanId(benhNhanId);
            if (benhNhan == null) {
                model.addAttribute("error", "Không tìm thấy bệnh nhân");
                return "error";
            }

            String nguoiDungId = getNguoiDungId();
           
            BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
            if (bacSi == null) {
                model.addAttribute("error", "Không tìm thấy bác sĩ");
                return "error";
            }

            HoSoBenh hoSoBenh;
            if (hoSoId != null && !hoSoId.isEmpty()) {
                hoSoBenh = hoSoBenhService.findById(hoSoId);
            } else {
                hoSoBenh = new HoSoBenh();
                hoSoBenh.setHoSoId(UUID.randomUUID().toString());
                hoSoBenh.setBenhNhan(benhNhan);
                hoSoBenh.setBacSi(bacSi);
                hoSoBenh.setChanDoan("Chưa chẩn đoán");
                hoSoBenh = hoSoBenhService.save(hoSoBenh);
            }

            VitalSigns vitalSigns = vitalSignsService.getVitalSignsByHoSoId(hoSoBenh.getHoSoId()).orElse(null);

            model.addAttribute("benhNhan", benhNhan);
            model.addAttribute("hoSoBenh", hoSoBenh);
            model.addAttribute("slotId", slotId);
            model.addAttribute("vitalSigns", vitalSigns);
            return "bacsi/quytrinhkham/step1";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải bước 1: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("bacsi/step1/save")
    public ResponseEntity<Map<String, Object>> saveStep1(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String hoSoId = (String) data.get("hoSoId");
            String benhNhanId = (String) data.get("benhNhanId");
            String slotId = (String) data.get("slotId");
            if (hoSoId == null || benhNhanId == null) {
                response.put("success", false);
                response.put("message", "Thiếu hoSoId hoặc benhNhanId");
                return ResponseEntity.badRequest().body(response);
            }

            vitalSignsService.saveVitalSigns(hoSoId, data);

            String redirectUrl = "/bacsi/step2?benhNhanId=" + benhNhanId + "&hoSoId=" + hoSoId + "&slotId=" + slotId ;
            response.put("success", true);
            response.put("redirectUrl", redirectUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu thông số y tế: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("bacsi/step2")
    public String step2(@RequestParam("benhNhanId") String benhNhanId, @RequestParam("hoSoId") String hoSoId,@RequestParam(value = "slotId", required = false) String slotId, Model model) {
        try {
            BenhNhan benhNhan = benhNhanService.findByBenhNhanId(benhNhanId);
            if (benhNhan == null) {
                model.addAttribute("error", "Không tìm thấy bệnh nhân");
                return "error";
            }

            HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);

            List<XetNghiem> xetNghiems = xetNghiemService.getXetNghiemByHoSoId(hoSoId);
            List<LoaiXetNghiem> loaiXetNghiems = loaiXetNghiemService.findAll();

            System.out.println("LoaiXetNghiems: " + loaiXetNghiems.size());

            model.addAttribute("benhNhan", benhNhan);
            model.addAttribute("hoSoBenh", hoSoBenh);
            model.addAttribute("slotId", slotId);
            model.addAttribute("xetNghiems", xetNghiems);
            model.addAttribute("loaiXetNghiems", loaiXetNghiems);
            return "bacsi/quytrinhkham/step2";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải bước 2: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("bacsi/step2/save")
    @ResponseBody
    public Map<String, Object> saveXetNghiem(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        try {
            String hoSoId = (String) data.get("hoSoId");
            xetNghiemService.saveXetNghiem(hoSoId, data);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("bacsi/step2/upload-result")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadKetQua(@RequestParam("xetNghiemIds") List<Long> xetNghiemIds, @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file == null || file.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn file để upload.");
                return ResponseEntity.badRequest().body(response);
            }
            if (xetNghiemIds == null || xetNghiemIds.isEmpty()) {
                response.put("success", false);
                response.put("message", "Vui lòng chọn ít nhất một xét nghiệm.");
                return ResponseEntity.badRequest().body(response);
            }
            System.out.println("Uploading file for xetNghiemIds: " + xetNghiemIds);
            String filePath = xetNghiemService.uploadKetQua(xetNghiemIds, file);
            response.put("success", true);
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lưu file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi không xác định khi upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("bacsi/step2/delete-xetnghiem/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteXetNghiem(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            xetNghiemService.deleteXetNghiem(id);
            response.put("success", true);
            response.put("message", "Xóa xét nghiệm thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa xét nghiệm: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("files/xetnghiem/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
        System.out.println("Upload directory configured: " + uploadDir);
        Path filePath = Paths.get(uploadDir).resolve(filename);
        System.out.println("Serving file: " + filePath);
        File file = filePath.toFile();
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        System.out.println("Serving file with content type: " + contentType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @PostMapping("bacsi/step2/generate-ticket")
    @ResponseBody
    public ResponseEntity<?> generatePhieuXetNghiem(@RequestBody Map<String, Object> data) {
        try {
            String hoSoId = (String) data.get("hoSoId");
            List<Long> xetNghiemIds = ((List<?>) data.get("xetNghiemIds")).stream()
                    .map(id -> {
                        if (id instanceof Number) return ((Number) id).longValue();
                        return Long.parseLong(id.toString());
                    })
                    .toList();

            System.out.println("Received hoSoId: " + hoSoId + ", xetNghiemIds: " + xetNghiemIds);

            // Validate input
            if (hoSoId == null || hoSoId.isEmpty()) {
                System.err.println("Invalid hoSoId: " + hoSoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: hoSoId is null or empty");
            }
            if (xetNghiemIds == null || xetNghiemIds.isEmpty()) {
                System.err.println("Invalid xetNghiemIds: " + xetNghiemIds);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: xetNghiemIds is null or empty");
            }

            // Fetch HoSoBenh
            HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
            if (hoSoBenh == null) {
                System.err.println("HoSoBenh not found for hoSoId: " + hoSoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: HoSoBenh not found for hoSoId: " + hoSoId);
            }
            if (hoSoBenh.getBenhNhan() == null || hoSoBenh.getBacSi() == null) {
                System.err.println("HoSoBenh is invalid (missing BenhNhan or BacSi) for hoSoId: " + hoSoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: HoSoBenh is invalid (missing BenhNhan or BacSi) for hoSoId: " + hoSoId);
            }

            // Create PhieuXetNghiem
            PhieuXetNghiem phieu = phieuXetNghiemService.createPhieuXetNghiem(hoSoId, xetNghiemIds);
            if (phieu == null || phieu.getMaPhieu() == null) {
                System.err.println("Failed to create PhieuXetNghiem for hoSoId: " + hoSoId + ", with xetNghiemIds: " + xetNghiemIds);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Failed to create PhieuXetNghiem for hoSoId: " + hoSoId);
            }

            // Fetch XetNghiem list
            List<XetNghiem> xetNghiems = xetNghiemService.getXetNghiemByHoSoId(hoSoId);
            if (xetNghiems == null || xetNghiems.isEmpty()) {
                System.err.println("No XetNghiem found for hoSoId: " + hoSoId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: No XetNghiem found for hoSoId: " + hoSoId);
            }

            // Verify and log selected xetNghiemIds
            List<XetNghiem> selectedXetNghiems = xetNghiems.stream()
                    .filter(xn -> xetNghiemIds.contains(xn.getId()))
                    .toList();
            System.out.println("Filtered selectedXetNghiems: " + selectedXetNghiems.stream().map(xn -> xn.getId()).toList());
            if (selectedXetNghiems.isEmpty()) {
                System.err.println("None of the provided xetNghiemIds match for hoSoId: " + hoSoId + ", xetNghiemIds: " + xetNghiemIds);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: None of the provided xetNghiemIds match for hoSoId: " + hoSoId);
            }

            // Generate PDF in-memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, baos);
                document.open();

                // Load Vietnamese font
                BaseFont baseFont;
                try {
                    baseFont = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    System.out.println("Loaded Arial font successfully");
                } catch (Exception e) {
                    System.err.println("Failed to load Arial font: " + e.getMessage());
                    baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                    System.out.println("Fell back to Helvetica font");
                }

                Font titleFont = new Font(baseFont, 18, Font.BOLD);
                Font subtitleFont = new Font(baseFont, 12, Font.NORMAL);
                Font headerFont = new Font(baseFont, 11, Font.BOLD);
                Font cellFont = new Font(baseFont, 10);

                // Title
                Paragraph title = new Paragraph("PHIẾU XÉT NGHIỆM", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                // Format "Ngày tạo" using DateTimeFormatter
                String formattedCreationDate = phieu.getThoiGianTao() != null
                        ? phieu.getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "Không xác định";

                // Information
                Paragraph info = new Paragraph(
                        "Mã phiếu: " + (phieu.getMaPhieu() != null ? phieu.getMaPhieu() : "N/A") + "\n" +
                        "Bệnh nhân: " + (hoSoBenh.getBenhNhan() != null && hoSoBenh.getBenhNhan().getTen() != null ? hoSoBenh.getBenhNhan().getTen() : "Không xác định") + "\n" +
                        "Bác sĩ: " + (hoSoBenh.getBacSi() != null && hoSoBenh.getBacSi().getTen() != null ? hoSoBenh.getBacSi().getTen() : "Không xác định") + "\n" +
                        "Ngày tạo: " + formattedCreationDate + "\n" +
                        "Tổng tiền: " + (hoSoBenh.getTongTien() != null ? hoSoBenh.getTongTien() : 0) + " VNĐ",
                        subtitleFont
                );
                info.setSpacingAfter(20);
                document.add(info);

                // Table
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{50, 30, 20});

                // Header
                String[] headers = {"Tên Xét Nghiệm", "Ghi Chú", "Giá (VNĐ)"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(8);
                    cell.setBackgroundColor(new BaseColor(220, 240, 255)); // Light blue background
                    cell.setBorderWidth(1.2f);
                    table.addCell(cell);
                }

                // Content
                for (XetNghiem xn : selectedXetNghiems) {
                    table.addCell(new PdfPCell(new Phrase(xn.getLoaiXetNghiem() != null && xn.getLoaiXetNghiem().getTenXetNghiem() != null ? xn.getLoaiXetNghiem().getTenXetNghiem() : "", cellFont)));
                    table.addCell(new PdfPCell(new Phrase(xn.getGhiChu() != null ? xn.getGhiChu() : "N/A", cellFont)));
                    table.addCell(new PdfPCell(new Phrase(xn.getLoaiXetNghiem() != null && xn.getLoaiXetNghiem().getGia() != null ? String.valueOf(xn.getLoaiXetNghiem().getGia()) : "0", cellFont)));
                }

                document.add(table);

                // Total
                Paragraph total = new Paragraph("Tổng tiền xét nghiệm: " + (phieu.getTongGia() != null ? phieu.getTongGia() : 0) + " VNĐ", subtitleFont);
                total.setSpacingBefore(10);
                total.setAlignment(Element.ALIGN_RIGHT);
                document.add(total);

                document.close();
            } catch (Exception e) {
                System.err.println("Error generating PDF: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating PDF: " + e.getMessage());
            }

            // Return the PDF as a ByteArrayResource
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            if (resource.getByteArray().length == 0) {
                System.err.println("Generated PDF is empty for hoSoId: " + hoSoId + ", xetNghiemIds: " + xetNghiemIds);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: Generated PDF is empty");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=phieu_xet_nghiem_" + phieu.getMaPhieu() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);

        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/bacsi/step3")
    public String step3(@RequestParam("benhNhanId") String benhNhanId, @RequestParam("hoSoId") String hoSoId,@RequestParam(value = "slotId", required = false) String slotId, Model model) {
        try {
            BenhNhan benhNhan = benhNhanService.findByBenhNhanId(benhNhanId);
            if (benhNhan == null) {
                model.addAttribute("error", "Không tìm thấy bệnh nhân");
                return "error";
            }

            HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
            if (hoSoBenh == null) {
                model.addAttribute("error", "Không tìm thấy hồ sơ bệnh");
                return "error";
            }

            List<Thuoc> thuocs = thuocService.getAllThuoc();

            model.addAttribute("benhNhan", benhNhan);
            model.addAttribute("slotId", slotId);
            model.addAttribute("hoSoBenh", hoSoBenh);
            model.addAttribute("thuocs", thuocs);
            return "bacsi/quytrinhkham/step3";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải bước 3: " + e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/bacsi/step3/save")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(@RequestBody Map<String, Object> request) {
        Map<String, String> response = new HashMap<>();

        // Lấy thông tin bác sĩ từ nguoiDungId
        String nguoiDungId = getNguoiDungId();
        BacSi bacSi = bacSiService.findByNguoiDungId(nguoiDungId);
        if (bacSi == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy thông tin bác sĩ!");
            return ResponseEntity.badRequest().body(response);
        }

        // Lấy dữ liệu từ request
        String hoSoId = (String) request.get("hoSoId");
        String benhNhanId = (String) request.get("benhNhanId");
        String slotId = (String) request.get("slotId");
        List<Map<String, Object>> drugs = (List<Map<String, Object>>) request.get("drugs");

        // Kiểm tra dữ liệu đầu vào
        if (hoSoId == null || benhNhanId == null || drugs == null || drugs.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Dữ liệu không hợp lệ!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra slotId
        if (slotId == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy slotId!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra bệnh nhân
        BenhNhan benhNhan = benhNhanService.findByBenhNhanId(benhNhanId);
        if (benhNhan == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy thông tin bệnh nhân!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra hồ sơ bệnh
        HoSoBenh hoSoBenh = hoSoBenhService.findById(hoSoId);
        if (hoSoBenh == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy hồ sơ bệnh!");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra và lấy SlotThoiGian
        SlotThoiGian slotThoiGian = slotThoiGianService.findById(slotId);
        if (slotThoiGian == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy slot thời gian với ID: " + slotId);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Gán SlotThoiGian vào HoSoBenh
            hoSoBenh.setSlotThoiGian(slotThoiGian);
            hoSoBenhService.save(hoSoBenh); // Lưu để cập nhật cột slot_id trong ho_so_benh

            // Tạo đơn thuốc
            DonThuoc donThuoc = new DonThuoc();

            List<DonThuocThuoc> donThuocThuocs = new ArrayList<>();
            for (Map<String, Object> drug : drugs) {
                Long thuocId = Long.parseLong((String) drug.get("thuocId"));
                Thuoc thuoc = thuocService.findThuocById(thuocId);

                if (thuoc == null) {
                    response.put("status", "error");
                    response.put("message", "Thuốc ID " + thuocId + " không tồn tại!");
                    return ResponseEntity.badRequest().body(response);
                }

                Integer requestedQuantity = Integer.parseInt(drug.get("soLuong").toString());
                if (thuoc.getSoLuong() < requestedQuantity) {
                    response.put("status", "error");
                    response.put("message", "Số lượng thuốc " + thuoc.getTen() + " trong kho không đủ!");
                    return ResponseEntity.badRequest().body(response);
                }

                thuoc.setSoLuong(thuoc.getSoLuong() - requestedQuantity);
                thuocService.updateThuoc(thuoc);

                DonThuocThuoc donThuocThuoc = new DonThuocThuoc();
                donThuocThuoc.setDonThuoc(donThuoc);
                donThuocThuoc.setThuoc(thuoc);
                donThuocThuoc.setLieu((String) drug.get("lieu"));
                donThuocThuoc.setTanSuat((String) drug.get("tanSuat"));
                donThuocThuoc.setSoLuong(requestedQuantity);

                donThuocThuocs.add(donThuocThuoc);
            }

            donThuoc.setDonThuocThuocs(donThuocThuocs);

            // Tính tiền thuốc
            BigDecimal tienThuoc = BigDecimal.ZERO;
            if (hoSoBenh.getDonThuocs() != null) {
                for (DonThuoc dt : hoSoBenh.getDonThuocs()) {
                    tienThuoc = tienThuoc.add(dt.calculateTongTien());
                }
            }
            tienThuoc = tienThuoc.add(donThuoc.calculateTongTien());

            // Tổng tiền = Tiền xét nghiệm (đã tính trước) + Tiền thuốc
            Integer tienXetNghiemTruocDo = hoSoBenh.getTongTien() != null ? hoSoBenh.getTongTien() : 0;
            Integer tongTien = tienXetNghiemTruocDo + tienThuoc.intValue();

            // Cập nhật tongTien và trangThai cho HoSoBenh
            hoSoBenh.setTongTien(tongTien);
            hoSoBenh.setTrangThai(true);
            hoSoBenh.addDonThuoc(donThuoc);
            donThuocService.save(donThuoc);

            // Cập nhật trạng thái slot thời gian
            slotThoiGian.setTrangThai("completed");
            slotThoiGianService.save(slotThoiGian);

            // Lấy ngày từ thoiGianTao để redirect
            LocalDate redirectDate = hoSoBenh.getThoiGianTao().toLocalDate();
            String redirectUrl = "/bacsi/xemlichhen?date=" + redirectDate.toString();

            response.put("status", "success");
            response.put("message", "Đơn thuốc đã được tạo thành công!");
            response.put("redirectUrl", redirectUrl);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Đã xảy ra lỗi trong quá trình tạo đơn thuốc: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    
    
    
    
    
    @GetMapping("bacsi/medical-history")
    public ResponseEntity<List<MedicalHistoryDTO>> getMedicalHistory(@RequestParam("benhNhanId") String benhNhanId) {
        List<HoSoBenh> medicalHistory = hoSoBenhService.findByBenhNhanId(benhNhanId);

        List<MedicalHistoryDTO> dtos = medicalHistory.stream().map(hsb -> {
            // Initialize lazy-loaded relationships
            Hibernate.initialize(hsb.getBenhNhan());
            Hibernate.initialize(hsb.getBacSi());
            Hibernate.initialize(hsb.getDonThuocs());
            Hibernate.initialize(hsb.getXetNghiems());
            Hibernate.initialize(hsb.getPhieuXetNghiems());
            Hibernate.initialize(hsb.getVitalSigns());

            MedicalHistoryDTO dto = new MedicalHistoryDTO();
            dto.setHoSoId(hsb.getHoSoId());
            
            // Map BenhNhan
            if (hsb.getBenhNhan() != null) {
                MedicalHistoryDTO.BenhNhanDTO benhNhanDTO = new MedicalHistoryDTO.BenhNhanDTO(
                    hsb.getBenhNhan().getBenhNhanId(),
                    hsb.getBenhNhan().getTen(),
                    hsb.getBenhNhan().getDienThoai()
                );
                dto.setBenhNhan(benhNhanDTO);
            }

            // Map BacSi
            if (hsb.getBacSi() != null) {
                MedicalHistoryDTO.BacSiDTO bacSiDTO = new MedicalHistoryDTO.BacSiDTO(
                    hsb.getBacSi().getBacSiId(),
                    hsb.getBacSi().getTen()
                );
                dto.setBacSi(bacSiDTO);
            }

            dto.setChanDoan(hsb.getChanDoan());
            dto.setTrieuChung(hsb.getTrieuChung());
            dto.setTongTien(hsb.getTongTien());
            dto.setDaThanhToan(hsb.getDaThanhToan());
            dto.setThoiGianTao(hsb.getThoiGianTao() != null ? hsb.getThoiGianTao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);

            // Map VitalSigns
            if (hsb.getVitalSigns() != null) {
                List<MedicalHistoryDTO.VitalSignsDTO> vitalSignsDTOs = hsb.getVitalSigns().stream().map(vs -> {
                    return new MedicalHistoryDTO.VitalSignsDTO(
                        vs.getId(),
                        vs.getTemperature(),
                        vs.getHeight(),
                        vs.getWeight(),
                        vs.getBloodPressureSys(),
                        vs.getBloodPressureDia(),
                        vs.getNotes(),
                        vs.getThoiGianTao() != null ? vs.getThoiGianTao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
                    );
                }).collect(Collectors.toList());
                dto.setVitalSigns(vitalSignsDTOs);
            }

            // Map DonThuoc
            if (hsb.getDonThuocs() != null) {
                List<MedicalHistoryDTO.DonThuocDTO> donThuocDTOs = hsb.getDonThuocs().stream().map(dt -> {
                    Hibernate.initialize(dt.getDonThuocThuocs());
                    List<MedicalHistoryDTO.DonThuocThuocDTO> donThuocThuocDTOs = dt.getDonThuocThuocs().stream().map(dtt -> {
                        MedicalHistoryDTO.ThuocDTO thuocDTO = new MedicalHistoryDTO.ThuocDTO(
                            dtt.getThuoc().getTen(),
                            dtt.getThuoc().getGia()
                        );
                        return new MedicalHistoryDTO.DonThuocThuocDTO(
                            thuocDTO,
                            dtt.getLieu(),
                            dtt.getTanSuat(),
                            dtt.getSoLuong()
                        );
                    }).collect(Collectors.toList());
                    MedicalHistoryDTO.DonThuocDTO donThuocDTO = new MedicalHistoryDTO.DonThuocDTO(
                        dt.getDonThuocId(),
                        dt.getFormattedTongTienThuoc(),
                        donThuocThuocDTOs
                    );
                    return donThuocDTO;
                }).collect(Collectors.toList());
                dto.setDonThuocs(donThuocDTOs);
            }

            // Map XetNghiem
            if (hsb.getXetNghiems() != null) {
                List<MedicalHistoryDTO.XetNghiemDTO> xetNghiemDTOs = hsb.getXetNghiems().stream().map(xn -> {
                    MedicalHistoryDTO.LoaiXetNghiemDTO loaiXetNghiemDTO = new MedicalHistoryDTO.LoaiXetNghiemDTO(
                        xn.getLoaiXetNghiem().getTenXetNghiem(),
                        xn.getLoaiXetNghiem().getGia()
                    );
                    return new MedicalHistoryDTO.XetNghiemDTO(
                        loaiXetNghiemDTO,
                        xn.getGhiChu(),
                        xn.getTrangThai(),
                        xn.getFileKetQua(),
                        xn.getThoiGianTao() != null ? xn.getThoiGianTao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null
                    );
                }).collect(Collectors.toList());
                dto.setXetNghiems(xetNghiemDTOs);
            }

            // Map PhieuXetNghiem
            if (hsb.getPhieuXetNghiems() != null) {
                List<MedicalHistoryDTO.PhieuXetNghiemDTO> phieuXetNghiemDTOs = hsb.getPhieuXetNghiems().stream().map(pxn -> {
                    return new MedicalHistoryDTO.PhieuXetNghiemDTO(
                        pxn.getMaPhieu(),
                        pxn.getTongGia(),
                        pxn.getThoiGianTao() != null ? pxn.getThoiGianTao().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null,
                        pxn.getXetNghiemIds()
                    );
                }).collect(Collectors.toList());
                dto.setPhieuXetNghiems(phieuXetNghiemDTOs);
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
