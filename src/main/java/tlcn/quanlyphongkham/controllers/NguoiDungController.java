package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import tlcn.quanlyphongkham.dtos.BenhNhanDTO;
import tlcn.quanlyphongkham.dtos.TaiKhoanProfileDTO;
import tlcn.quanlyphongkham.dtos.UserProfileDTO;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.services.BenhNhanService;
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

	String nguoiDungId = "9965ce1d-70a0-4494-a0af-11849e0907f2";

	@GetMapping("/user/editprofile")
	public String editProfile(Model model) {
		UserProfileDTO userProfile = userProfileService.getUserProfileByNguoiDungId(nguoiDungId);
		model.addAttribute("nguoiDung", userProfile.getNguoiDung());
		model.addAttribute("benhNhan", userProfile.getBenhNhan());
		return "benhnhan/editprofile/editprofile"; // Trả về view editprofile
	}

	@PostMapping("/user/updateprofile")
	public String updateProfile(
	    @ModelAttribute("nguoiDung") TaiKhoanProfileDTO tk,
	    @ModelAttribute("benhNhan") BenhNhanDTO benhNhanDTO,
	    Model model) {

	   
	

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

}
