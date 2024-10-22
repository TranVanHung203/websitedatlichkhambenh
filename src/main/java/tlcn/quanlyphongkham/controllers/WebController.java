package tlcn.quanlyphongkham.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import tlcn.quanlyphongkham.dtos.DangKyDTO;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.EmailService;
import tlcn.quanlyphongkham.services.NguoiDungService;

@Controller
public class WebController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private BenhNhanService benhNhanService;
    
    @Autowired
    private EmailService emailService;
    


    @PostMapping("/login")
    public String login(@RequestParam("tenDangNhapOrEmail") String tenDangNhapOrEmail, 
                        @RequestParam("password") String password, 
                        Model model, HttpSession session) {
        
        // Kiểm tra thông tin đăng nhập
        NguoiDung user = nguoiDungService.validateLogin(tenDangNhapOrEmail, password);
        
        if (user != null) {
            // Nếu thông tin đăng nhập đúng, lưu người dùng vào session
            session.setAttribute("loggedUser", user);
            return "redirect:/home"; // Chuyển hướng đến trang chủ sau khi đăng nhập thành công
        } else {
            // Nếu sai tên đăng nhập hoặc mật khẩu, thông báo lỗi
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return "web/dangnhap/dangnhap"; // Trả về trang đăng nhập cùng thông báo lỗi
        }
    }
    
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        // Lấy thông tin người dùng từ session
        NguoiDung loggedUser = (NguoiDung) session.getAttribute("loggedUser");
        
        if (loggedUser != null) {
            // Nếu đã đăng nhập, hiển thị tên người dùng trên trang chủ
            model.addAttribute("tenNguoiDung", loggedUser.getTenDangNhap());
            return "web/home/home"; // Đường dẫn đến trang chủ
        } else {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/login"; 
        }
    }
    
    
    
    
    

    @GetMapping("/register")
    public String showDangKyForm(Model model) {
        model.addAttribute("dangKyDTO", new DangKyDTO());
        return "web/dangky/dangky";
    }

    @PostMapping("/register")
    public String dangKy(@ModelAttribute DangKyDTO dangKyDTO, Model model) {
        // Tạo mới đối tượng NguoiDung
        NguoiDung nguoiDung = new NguoiDung();
        
        if (nguoiDungService.findByEmail(dangKyDTO.getEmail()) != null) {
            model.addAttribute("error", "Email đã tồn tại. Vui lòng nhập email khác.");
            return "web/dangky/dangky"; // Trả về trang đăng ký với thông báo lỗi
        }
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(dangKyDTO.getMatKhau());
        
        nguoiDung.setNguoiDungId(java.util.UUID.randomUUID().toString());
        nguoiDung.setTenDangNhap(dangKyDTO.getTenDangNhap());
        nguoiDung.setMatKhau(encodedPassword);
        nguoiDung.setEmail(dangKyDTO.getEmail());
        nguoiDung.setVaiTro("USER"); // Gán vai trò mặc định là USER

        nguoiDungService.saveNguoiDung(nguoiDung);

        // Tạo mới đối tượng BenhNhan
        BenhNhan benhNhan = new BenhNhan();
        benhNhan.setBenhNhanId(java.util.UUID.randomUUID().toString());
        benhNhan.setNguoiDung(nguoiDung);
        benhNhan.setTen(dangKyDTO.getTen());
        benhNhan.setNgaySinh(dangKyDTO.getNgaySinh());
        benhNhan.setGioiTinh(dangKyDTO.getGioiTinh());
        benhNhan.setDienThoai(dangKyDTO.getDienThoai());
        benhNhan.setDiaChi(dangKyDTO.getDiaChi());

        benhNhanService.save(benhNhan);

        String token = java.util.UUID.randomUUID().toString();
        nguoiDung.setToken(token);
        nguoiDungService.saveNguoiDung(nguoiDung);

        // Gửi email xác nhận
        String emailBody = "Cảm ơn bạn đã đăng ký tài khoản. Vui lòng nhấn vào đường dẫn sau để xác nhận tài khoản của bạn: "
                           + "http://localhost:8181/confirm?token=" + token;
        emailService.sendEmail(nguoiDung.getEmail(), "Xác nhận đăng ký tài khoản", emailBody);
        
        return "redirect:/login";
    }
    
    @GetMapping("/confirm")
    public String confirmAccount(@RequestParam("token") String token, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findByToken(token); // Tìm theo token

        if (nguoiDung != null) {
            // Kích hoạt tài khoản
            nguoiDung.setTrangthai("ACTIVE"); // Cập nhật trạng thái tài khoản
            nguoiDungService.saveNguoiDung(nguoiDung);
            return "redirect:/register/success"; // Chuyển hướng đến trang thành công
        } else {
            return "redirect:/error"; // Chuyển hướng đến trang lỗi
        }
    }


    @GetMapping("/login")
    public String showDangNhapForm() {
		 return "web/dangnhap/dangnhap"; // Đường dẫn đến template đăng ký
	}
    @GetMapping("/register/success")
    public String showSucessRegistrationForm() {
		 return "web/dangky/dangkythanhcong"; // Đường dẫn đến template đăng ký
	}
}
