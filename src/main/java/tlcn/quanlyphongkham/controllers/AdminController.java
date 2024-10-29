package tlcn.quanlyphongkham.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.ThuocService;

@Controller
public class AdminController {
    
	@Autowired
    private BacSiService bacSiService;
	
	@Autowired
    private ChuyenKhoaService chuyenKhoaService;
	
	@Autowired
	private ThuocService thuocService;
	
    @GetMapping("/admin/qltk")
    public String qltk() {
    	return "admin/quanlytaikhoan/quanlytaikhoan";
    }
    @GetMapping("/admin/qlbs")
    public String qlbs(Model model) {
    	model.addAttribute("doctors", bacSiService.getAllDoctors());
    	return "admin/quanlybacsi/quanlybacsi";
    }
    
    @GetMapping("/admin/qlck")
    public String qlck(Model model) {
    	model.addAttribute("chuyenkhoas", chuyenKhoaService.getAllChuyenKhoas());
    	return "admin/quanlychuyenkhoa/quanlychuyenkhoa";
    }
    
    @GetMapping("/admin/qlt")
    public String qlt(Model model) {
        model.addAttribute("thuocs", thuocService.getAllThuoc());  
        return "admin/quanlythuoc/quanlythuoc";  
    }
        
 // Hiển thị form cập nhật thông tin bác sĩ cùng chi tiết bác sĩ
    @GetMapping("/admin/qlbs/edit-bacsi/{bacSiId}")
    public String showUpdateForm(@PathVariable("bacSiId") String bacSiId, Model model) {
        BacSi doctor = bacSiService.getDoctorById(bacSiId);
        model.addAttribute("doctor", doctor);
        //model.addAttribute("chiTietBacSi", doctor.getChiTietBacSi());
        model.addAttribute("chuyenKhoas", chuyenKhoaService.getAllChuyenKhoas());  // Lấy danh sách chuyên khoa để hiển thị trong form
        return "bacsi/editbacsi/editbacsi";
    }
    
    // Cập nhật thông tin bác sĩ và chi tiết bác sĩ
    @PostMapping("/admin/qlbs/update/{bacSiId}")
    public String updateDoctor(@PathVariable("bacSiId") String bacSiId, 
                               BacSi updatedDoctor, 
                               ChiTietBacSi updatedChiTiet) { 
        bacSiService.updateDoctor(bacSiId, updatedDoctor, updatedChiTiet); 
        return "redirect:/admin/qlbs";  
    }
    
    @PostMapping("/admin/delete-thuoc")
    public String deleteThuoc(@RequestParam String thuocId) {
        thuocService.deleteThuoc(thuocId);
        return "redirect:/admin/qlt"; // Chuyển hướng lại trang quản lý thuốc sau khi xóa
    }
    
    @GetMapping("/admin/edit-thuoc")
    public String editThuoc(@RequestParam String thuocId, Model model) {
        Thuoc thuoc = thuocService.getThuocById(thuocId);
        model.addAttribute("thuoc", thuoc);
        return "bacsi/editthuoc/editthuoc"; // Trang cập nhật thuốc
    }

    @PostMapping("/admin/update-thuoc")
    public String updateThuoc(Thuoc thuoc) {
        thuocService.updateThuoc(thuoc);
        return "redirect:/admin/qlt"; // Chuyển hướng lại trang quản lý thuốc sau khi cập nhật
    }
    
 // Hiển thị trang thêm thuốc
    @GetMapping("/admin/add-thuoc")
    public String showAddThuocForm(Model model) {
        return "bacsi/addthuoc/addthuoc"; // Trả về trang thêm thuốc
    }
    
    @PostMapping("/admin/add-thuoc")
    public String addThuoc(@ModelAttribute Thuoc thuoc) {
        thuocService.saveThuoc(thuoc); // Gọi service để lưu thuốc
        return "redirect:/admin/qlt"; // Quay lại trang danh sách thuốc
    }
    
    
 // AdminController.java
    @GetMapping("/admin/add-chuyenkhoa")
    public String showAddChuyenKhoaForm(Model model) {
        return "bacsi/addchuyenkhoa/addchuyenkhoa"; // Trả về trang thêm chuyên khoa
    }

    @PostMapping("/admin/add-chuyenkhoa")
    public String addChuyenKhoa(@ModelAttribute ChuyenKhoa chuyenKhoa) {
        chuyenKhoaService.saveChuyenKhoa(chuyenKhoa); // Gọi service để lưu chuyên khoa
        return "redirect:/admin/qlck"; // Quay lại trang danh sách chuyên khoa
    }
    
  
    
    // Hiển thị form chỉnh sửa chuyên khoa
    @GetMapping("/admin/qlck/edit-chuyenkhoa/{chuyenKhoaId}")
    public String showEditChuyenKhoaForm(@PathVariable("chuyenKhoaId") Long  chuyenKhoaId, Model model) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
        model.addAttribute("chuyenKhoa", chuyenKhoa);
        return "bacsi/editchuyenkhoa/editchuyenkhoa"; // Đường dẫn đến trang chỉnh sửa chuyên khoa
    }

    @PostMapping("/admin/qlck/update-chuyenkhoa")
    public String updateChuyenKhoa(@RequestParam Long chuyenKhoaId, 
                                   @RequestParam String ten) {
        // Lấy chuyên khoa từ cơ sở dữ liệu dựa trên ID
        ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
        if (chuyenKhoa != null) {
            chuyenKhoa.setTen(ten);  // Cập nhật tên chuyên khoa
            chuyenKhoaService.updateChuyenKhoa(chuyenKhoa);  // Gọi service để cập nhật
        }
        return "redirect:/admin/qlck";  // Chuyển hướng về trang danh sách chuyên khoa
    }
    
    @GetMapping("/admin/qlbs/edit-chitiet/{bacSiId}")
    public String showEditDoctorDetailsForm(@PathVariable("bacSiId") String bacSiId, Model model) {
        BacSi doctor = bacSiService.getDoctorById(bacSiId);
        model.addAttribute("doctor", doctor);
        model.addAttribute("chiTietBacSi", doctor.getChiTietBacSi());
        return "bacsi/editchitietbacsi/editchitietbacsi"; // Trang mới để chỉnh sửa chi tiết bác sĩ
    }
    @PostMapping("/admin/qlbs/update-chitiet/{bacSiId}")
    public String updateDoctorDetails(@PathVariable("bacSiId") String bacSiId, ChiTietBacSi updatedChiTiet) {
        bacSiService.updateDoctorDetails(bacSiId, updatedChiTiet); // Viết một phương thức mới trong BacSiService để xử lý cập nhật
        return "redirect:/admin/qlbs";  // Quay lại danh sách bác sĩ sau khi cập nhật
    }

    
}
