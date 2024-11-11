package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.ChiTietBacSiDTO;
import tlcn.quanlyphongkham.dtos.EditProfileBSDTO;
import tlcn.quanlyphongkham.dtos.LichHenKhamDTO;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.services.AppointmentService;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;

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

		String idBacSi = "f9dcf22c-ca7f-4259-b193-ff5de5f23563";
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
	        String idBacSi = "f9dcf22c-ca7f-4259-b193-ff5de5f23563"; // ID của bác sĩ

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
	        e.printStackTrace();  // In ra chi tiết lỗi để kiểm tra
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
        String bacSiId = "f9dcf22c-ca7f-4259-b193-ff5de5f23563"; // Example, replace with dynamic ID

        // Fetch personal info and details
        EditProfileBSDTO personalInfo = bacSiService.getProfile(bacSiId);
        ChiTietBacSiDTO detailInfo = bacSiService.getDetail(bacSiId);

        // Fetch available specialties
        List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
        
        model.addAttribute("personalInfo", personalInfo);
        model.addAttribute("detailInfo", detailInfo);
        model.addAttribute("chuyenKhoaList", chuyenKhoaList);

        return "bacsi/chinhsuathongtinbs/chinhsuathongtinbs"; // Your Thymeleaf template
    }

	  // Cập nhật thông tin cá nhân và chi tiết của bác sĩ
	@PostMapping("/updateProfile")
	public String updateProfile(
	    @ModelAttribute EditProfileBSDTO profileDTO, 
	    @ModelAttribute ChiTietBacSiDTO detailDTO,
	    Model model) {
	    String bacSiId = "f9dcf22c-ca7f-4259-b193-ff5de5f23563"; // Có thể lấy từ session hoặc tham số
	    
	    // Cập nhật thông tin cá nhân và chi tiết
	    String personalUpdateResult = bacSiService.updateProfile(profileDTO);
	    String detailUpdateResult = bacSiService.updateDetail(detailDTO);
	    
	
	    
	    return "redirect:/bacsi/editprofile";
	}
	@GetMapping("/bacsi/xemlichhen")
	public String getLichKham(
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	        Model model) {
	    if (date == null) {
	        date = LocalDate.now();  // Nếu không có date, mặc định là ngày hiện tại
	    }

	    String bacSiId = "f9dcf22c-ca7f-4259-b193-ff5de5f23563";
	    // Lấy danh sách lịch khám theo bác sĩ và ngày
	    List<LichKhamBenh> lichKhamList = lichKhamBenhService.getLichKhamByBacSiAndDate(bacSiId, date);

	    // Tạo danh sách LichKhamDTO
	    List<LichHenKhamDTO> lichKhamDTOList = new ArrayList<>();
	    for (LichKhamBenh lichKham : lichKhamList) {
	        // Lấy danh sách slot thời gian cho từng lịch khám
	        List<SlotThoiGian> slotThoiGianList = lichKhamBenhService.getSlotThoiGianByLichKham(lichKham.getMaLichKhamBenh());
	        // Tạo LichKhamDTO và thêm vào danh sách
	        lichKhamDTOList.add(new LichHenKhamDTO(lichKham.getMaLichKhamBenh(), lichKham.getNgayThangNam(),lichKham.getCa(), slotThoiGianList));
	    }
	    // Gửi dữ liệu lên giao diện
	    model.addAttribute("lichKhamDTOList", lichKhamDTOList);
	    model.addAttribute("date", date);
	    return "bacsi/xemlichhen/xemlichhen";
	}
	
	
	@PostMapping("/bacsi/update-appointment-status")
    public ResponseEntity<Map<String, String>> updateAppointmentStatus(
        @RequestBody Map<String, String> request) {

        String slotId = request.get("slotId");
        String date = request.get("date");
        String time = request.get("time");
        String status = request.get("status");

        boolean success = appointmentService.updateStatus(slotId, date, time, status);

        Map<String, String> response = new HashMap<>();
        response.put("success", success ? "true" : "false");
        return ResponseEntity.ok(response);
    }

}
