package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tlcn.quanlyphongkham.dtos.BenhNhanDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamDTO;
import tlcn.quanlyphongkham.dtos.TaiKhoanProfileDTO;
import tlcn.quanlyphongkham.dtos.UserProfileDTO;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.NguoiDungService;
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
	private HoSoBenhService hoSoBenhService;

	String nguoiDungId = "4ca0ba0c-cbe4-4ce9-8a0a-04081769a37f";

	@GetMapping("/user/editprofile")
	public String editProfile(@RequestParam(defaultValue = "0") int page, 
	                           @RequestParam(defaultValue = "2") int size, 
	                           @RequestParam(defaultValue = "medical-history") String currentTab,
	                           Model model) {
	    UserProfileDTO userProfile = userProfileService.getUserProfileByNguoiDungId(nguoiDungId);
	    model.addAttribute("nguoiDung", userProfile.getNguoiDung());
	    model.addAttribute("benhNhan", userProfile.getBenhNhan());

	    // Tìm bệnh nhân theo nguoiDungId thay vì gán cứng benhNhanId
	    BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);

	    if (benhNhan == null) {
	        model.addAttribute("lichSuKhams", Collections.emptyList());
	        return "benhnhan/editprofile/editprofile";
	    }

	    // Tạo đối tượng Pageable để phân trang
	    Pageable pageable = PageRequest.of(page, size);

	    // Lấy lịch sử khám của bệnh nhân với phân trang
	    Page<LichSuKhamDTO> lichSuKhams = hoSoBenhService.getLichSuKhamByBenhNhanId(benhNhan.getBenhNhanId(), pageable);
	    model.addAttribute("lichSuKhams", lichSuKhams);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", lichSuKhams.getTotalPages());
	    model.addAttribute("currentTab", currentTab);  // Truyền tham số currentTab vào Model

	    return "benhnhan/editprofile/editprofile";
	}




	@PostMapping("/user/updateprofile")
	public String updateProfile(@ModelAttribute("nguoiDung") TaiKhoanProfileDTO tk,
			@ModelAttribute("benhNhan") BenhNhanDTO benhNhanDTO, Model model) {

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
