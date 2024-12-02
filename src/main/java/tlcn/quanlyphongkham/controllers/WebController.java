package tlcn.quanlyphongkham.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
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

    
    
   

    
	@GetMapping("/ac_login")
	public String showLoginForm(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session
                    .getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
        
        model.addAttribute("typenotify", "error");
        model.addAttribute("mess", errorMessage);
        
		return "web/dangnhap/dangnhap";
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
        
        if (nguoiDungService.findByTenDangNhap(dangKyDTO.getTenDangNhap()) != null) {
            model.addAttribute("error1", "Tên đăng nhập đã tồn tại. Vui lòng nhập tên đăng nhập khác.");
            return "web/dangky/dangky"; // Trả về trang đăng ký với thông báo lỗi
        }
        
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(dangKyDTO.getMatKhau());
        
        nguoiDung.setNguoiDungId(java.util.UUID.randomUUID().toString());
        nguoiDung.setTenDangNhap(dangKyDTO.getTenDangNhap());
        nguoiDung.setMatKhau(encodedPassword);
        nguoiDung.setEmail(dangKyDTO.getEmail());
        nguoiDung.setVaiTro("BenhNhan"); 
        nguoiDung.setTrangthai("DISABLE");// Gán vai trò mặc định là USER

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
        
        return "/web/dangky/yeucauxacthuc";
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
    @GetMapping("/register/ycxn")
    public String showNotiXNForm() {
		 return "web/dangky/yeucauxacthuc"; // Đường dẫn đến template đăng ký
	}
    @GetMapping("/forgetpass")
    public String showForgetPassForm() {
		 return "web/quenmatkhau/quenmatkhau"; // Đường dẫn đến template đăng ký
	}
    
    
    
    
    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        NguoiDung nguoiDung = (NguoiDung) nguoiDungService.findByEmail(email);
        
        if (nguoiDung != null) {
            // Tạo token để gửi qua email
            String token = java.util.UUID.randomUUID().toString();
            nguoiDung.setToken(token);
            nguoiDungService.saveNguoiDung(nguoiDung);

            // Gửi email xác nhận với đường dẫn đặt lại mật khẩu
            String emailBody = "Để đặt lại mật khẩu của bạn, vui lòng nhấn vào đường dẫn sau: "
                             + "http://localhost:8181/reset-password?token=" + token;
            emailService.sendEmail(nguoiDung.getEmail(), "Đặt lại mật khẩu", emailBody);
            model.addAttribute("message", "Vui lòng kiểm tra email của bạn để đặt lại mật khẩu.");
        } else {
            model.addAttribute("error", "Email không tồn tại.");
        }

        return "web/quenmatkhau/thongbaoquenmatkhau"; // Trả về trang quên mật khẩu với thông báo
    }
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findByToken(token);
        
        if (nguoiDung != null) {
            model.addAttribute("token", token);
            return "web/quenmatkhau/resetpassword"; // Trang đặt lại mật khẩu
        } else {
            return "error"; // Trang lỗi nếu token không hợp lệ
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam("token") String token, 
            @RequestParam("matKhau") String matKhau, 
            @RequestParam("confirmMatKhau") String confirmMatKhau, // Thêm tham số xác nhận mật khẩu
            Model model) {
        
        // Kiểm tra xem mật khẩu và xác nhận mật khẩu có giống nhau không
        if (!matKhau.equals(confirmMatKhau)) {
            model.addAttribute("token", token); // Thêm token vào model
            model.addAttribute("error", "Mật khẩu và xác nhận mật khẩu không khớp.");
            return "web/quenmatkhau/resetpassword"; // Trả về trang đặt lại mật khẩu với thông báo lỗi
        }

        NguoiDung nguoiDung = nguoiDungService.findByToken(token);
        
        if (nguoiDung != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(matKhau);
            nguoiDung.setMatKhau(encodedPassword);
            nguoiDung.setToken(null);
            nguoiDungService.saveNguoiDung(nguoiDung);
            model.addAttribute("message", "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập.");
            return "web/quenmatkhau/doimatkhauthanhcong"; // Quay về trang đăng nhập
        } else {
            return "error"; // Trang lỗi nếu token không hợp lệ
        }
    }
   


}
