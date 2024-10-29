package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.EntityNotFoundException;
import tlcn.quanlyphongkham.dtos.LichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.NguoiDungDTO;
import tlcn.quanlyphongkham.dtos.ThemTaiKhoanDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.NguoiDungService;

@Controller
public class AdminController {
	@Autowired
	private LichKhamBenhService lichKhamBenhService;
	@Autowired
	private NguoiDungService nguoiDungService;
	@Autowired
	private BacSiService bacSiService;

	@GetMapping("/admin/qltk")
	public String getAllNguoiDung(Model model) {
		List<NguoiDungDTO> nguoiDungList = nguoiDungService.getAllNguoiDungQLTK();
		model.addAttribute("nguoiDungList", nguoiDungList); // Thêm dữ liệu vào model
		return "admin/quanlytaikhoan/quanlytaikhoan"; // Tên của trang giao diện Thymeleaf
	}

	@GetMapping("/admin/qltk/add")
	public String openFormAddUser(Model model) {

		return "admin/quanlytaikhoan/themtaikhoan"; // Tên của trang giao diện Thymeleaf
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

		try {
			nguoiDungService.themNguoiDung(themTaiKhoanDTO);
			response.put("message", "Thêm người dùng thành công.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("error", "Lỗi khi thêm người dùng: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/admin/qlck")
	public String openFormQuanLyCaKham(Model model) {

		return "admin/quanlycakham/quanlycakham"; // Tên của trang giao diện Thymeleaf

	}

	@GetMapping("/admin/qlck/{date}")
	@ResponseBody
	public List<LichKhamBenhDTO> getLichKhamByDate(@PathVariable("date") String date) {
		LocalDate localDate = LocalDate.parse(date);
		List<LichKhamBenh> lichKhamBenhs = lichKhamBenhService.findAllByDate(localDate);

		return lichKhamBenhs.stream()
				.map(lichKham -> new LichKhamBenhDTO(lichKham.getMaLichKhamBenh(), lichKham.getBacSi().getBacSiId(), // Mã
																														// bác
																														// sĩ
						lichKham.getBacSi().getTen(), lichKham.getBacSi().getChuyenKhoa().getChuyenKhoaId(), // Mã
																												// chuyên
																												// khoa
						lichKham.getBacSi().getChuyenKhoa().getTen(), lichKham.getCa() // Ca khám bệnh
				)).collect(Collectors.toList());
	}

	

	

	@PostMapping("/admin/qlck/addLichKham")
	public ResponseEntity<Map<String, Object>> addLichKhamBenh(@RequestBody LichKhamBenh lichKhamBenh) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // Kiểm tra và tìm bác sĩ dựa trên ID
	        if (lichKhamBenh.getBacSi() != null && lichKhamBenh.getBacSi().getBacSiId() != null) {
	            Optional<BacSi> optionalBacSi = bacSiService.findBacSiById(lichKhamBenh.getBacSi().getBacSiId());

	            if (optionalBacSi.isPresent()) {
	                lichKhamBenh.setBacSi(optionalBacSi.get());
	            } else {
	                response.put("status", "error");
	                response.put("message", "Bác sĩ không tồn tại với ID: " + lichKhamBenh.getBacSi().getBacSiId());
	                return ResponseEntity.ok(response); // Trả về 200 OK cho lỗi này
	            }
	        } else {
	            response.put("status", "error");
	            response.put("message", "ID bác sĩ không hợp lệ");
	            return ResponseEntity.ok(response); // Trả về 200 OK cho lỗi này
	        }

	        // Kiểm tra trùng lịch khám
	        boolean exists = lichKhamBenhService.existsByNgayThangNamAndCaAndBacSi(
	                lichKhamBenh.getNgayThangNam(),
	                lichKhamBenh.getCa(),
	                lichKhamBenh.getBacSi()
	        );

	        if (exists) {
	            response.put("status", "error");
	            response.put("message", "Lịch khám cho bác sĩ đã tồn tại vào ngày " + lichKhamBenh.getNgayThangNam() + " và ca " + lichKhamBenh.getCa());
	            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // Trả về 409 Conflict
	        }

	        // Tạo UUID cho LichKhamBenh
	        lichKhamBenh.setMaLichKhamBenh(UUID.randomUUID().toString());

	        // Lưu LichKhamBenh
	        lichKhamBenhService.addLichKhamBenh(lichKhamBenh);

	        // Tạo phản hồi thành công mà không bao gồm thông tin nhạy cảm
	        response.put("status", "success");
	        response.put("message", "Lịch khám đã được thêm thành công");
	        response.put("details", "Bạn có thể kiểm tra lịch khám của bác sĩ tại trang quản lý lịch khám.");
	        return ResponseEntity.ok(response); // Trả về 200 OK cho thành công

	    } catch (Exception e) {
	        response.put("status", "error");
	        response.put("message", e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Trả về 500 Internal Server Error cho lỗi
	    }
	}






}
