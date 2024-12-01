package tlcn.quanlyphongkham.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import jakarta.persistence.criteria.Path;
import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.BenhNhanOfTaoDonThuocDTO;
import tlcn.quanlyphongkham.dtos.ChiTietBacSiDTO;
import tlcn.quanlyphongkham.dtos.EditProfileBSDTO;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.dtos.LichHenKhamDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.DonThuoc;
import tlcn.quanlyphongkham.entities.DonThuocThuoc;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.services.AppointmentService;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.DonThuocService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.ThuocService;

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

		String idBacSi = "84960eeb-3ac9-4712-8418-68ecf7eae667";
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
			String idBacSi = "84960eeb-3ac9-4712-8418-68ecf7eae667"; // ID của bác sĩ

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
		String bacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667"; // Example, replace with dynamic ID

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
	
	  @Value("${upload.path}")
	    private String uploadDir;
	  

	  @PostMapping("/updateProfile")
	  public String updateProfile(@ModelAttribute EditProfileBSDTO profileDTO, 
	                              @ModelAttribute ChiTietBacSiDTO detailDTO,
	                              @RequestParam("avatar") MultipartFile avatar, 
	                              Model model) throws IOException {
	      String bacSiId = profileDTO.getBacSiId(); // Get the doctor ID

	      if (!avatar.isEmpty()) {
	          // Generate a new filename
	          String fileName = bacSiId + "-" + avatar.getOriginalFilename();
	          // Path where the file will be stored
	          String uploadDir = System.getProperty("user.dir") + "/uploads/";

	          // Create the directory if it doesn't exist
	          File directory = new File(uploadDir);
	          if (!directory.exists()) {
	              directory.mkdirs();
	          }

	          // Save the file to the server
	          java.nio.file.Path filePath = Paths.get(uploadDir, fileName);
	          Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	          // Set the URL of the uploaded image
	          profileDTO.setAvatarurl("/uploads/" + fileName); // Store the relative URL
	      }

	      // Update the profile and details
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

		String bacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667";
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

	@GetMapping("/bacsi/quanlytaodonthuoc")
	public String getAllPrescriptions(Model model) {
		List<DonThuoc> donThuocs = donThuocService.getAllDonThuoc();
		for (DonThuoc donThuoc : donThuocs) {
			donThuoc.calculateTongTien();
			donThuoc.setFormattedDate(
					donThuoc.getHoSoBenh().getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}
		model.addAttribute("donThuocs", donThuocs);
		return "bacsi/taodonthuoc/quanlytaodonthuoc";
	}
	
	
	@PostMapping("/bacsi/xoadonthuoc/{id}")
	public String deletePrescription(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    try {
	        // Gọi service để xóa đơn thuốc và hồ sơ bệnh
	        donThuocService.deleteDonThuocAndHoSoBenhById(id);
	        redirectAttributes.addFlashAttribute("message", "Xóa đơn thuốc và hồ sơ bệnh thành công!");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Xóa đơn thuốc hoặc hồ sơ bệnh thất bại!");
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
	        @RequestParam("benhNhanId") String benhNhanId, // Thêm tham số này
	        @RequestParam(value = "drugIds[]", required = false) List<Long> drugIds,
	        @RequestParam(value = "lieu[]", required = false) List<String> lieu,
	        @RequestParam(value = "tanSuat[]", required = false) List<String> tanSuat,
	        @RequestParam(value = "removedDrugIds", required = false) List<Long> removedDrugIds) {

	    Map<String, String> response = new HashMap<>();
	    try {
	        // Gọi service để cập nhật đơn thuốc
	        donThuocService.updateDonThuoc(donThuocId, hoSoId, chanDoan, benhNhanId, drugIds, lieu, tanSuat, removedDrugIds);
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
	        @RequestParam("tanSuat") List<String> tanSuat,
	        @RequestParam("benhNhanId") String benhNhanId) {

	    String fixedBacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667";
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
	        hoSoBenh.setBacSi(bacSi);
	        hoSoBenhService.save(hoSoBenh);

	        // Tạo đơn thuốc
	        DonThuoc donThuoc = new DonThuoc();
	        donThuoc.setHoSoBenh(hoSoBenh);

	        List<DonThuocThuoc> donThuocThuocs = new ArrayList<>();
	        for (int i = 0; i < drugIds.size(); i++) {
	            Long thuocId = drugIds.get(i);
	            Thuoc thuoc = thuocService.findThuocById(thuocId);

	            DonThuocThuoc donThuocThuoc = new DonThuocThuoc();
	            donThuocThuoc.setDonThuoc(donThuoc);
	            donThuocThuoc.setThuoc(thuoc);
	            donThuocThuoc.setLieu(lieu.get(i));
	            donThuocThuoc.setTanSuat(tanSuat.get(i));

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
	public String getLichSuKham(
	        @RequestParam(value = "startDate", required = false) String startDate,
	        @RequestParam(value = "endDate", required = false) String endDate,
	        @RequestParam(value = "page", defaultValue = "0") int page,
	        @RequestParam(value = "size", defaultValue = "1") int size,
	        Model model) {

	    String bacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667";

	    Page<HoSoBenhDTO> hoSoBenhPage = Page.empty();

	    if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
	        LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

	        LocalDateTime startDateTime = startLocalDate.atStartOfDay();
	        LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

	        Pageable pageable = PageRequest.of(page, size);

	        hoSoBenhPage = hoSoBenhService.getHoSoBenhWithDonThuocByDateRangeAndDoctor(startDateTime, endDateTime, bacSiId, pageable);
	    }

	    model.addAttribute("startDate", startDate);
	    model.addAttribute("endDate", endDate);
	    model.addAttribute("hoSoBenhPage", hoSoBenhPage);

	    return "bacsi/lichsukham/lichsukham";
	}
	
	
	
}
