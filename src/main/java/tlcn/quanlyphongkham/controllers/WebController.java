package tlcn.quanlyphongkham.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;


import tlcn.quanlyphongkham.model.NguoiDung;

@Controller
public class WebController {

	@GetMapping("/login")
	public String login() {
	    return "web/dangnhap/dangnhap"; // Use the full path to the template
	}
	
	@GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Tạo một đối tượng user rỗng
        NguoiDung user = new NguoiDung();
        model.addAttribute("user", user);
        return "web/dangky/dangky"; // Đường dẫn đến template đăng ký
    }
	
	@GetMapping("/register/sucess")
    public String showSucessRegistrationForm() {
		 return "web/dangky/dangkythanhcong"; // Đường dẫn đến template đăng ký
	}
	
	@GetMapping("/forgetpass")
    public String showForgetPasswordForm() {
    
        return "web/quenmatkhau/quenmatkhau"; // Đường dẫn đến template đăng ký
    }
	@GetMapping("/forgetpass/doimatkhauthanhcong")
    public String showSucessForgetPasswordForm() {
    
        return "web/quenmatkhau/doimatkhauthanhcong"; // Đường dẫn đến template đăng ký
    }
	@GetMapping("/forgetpass/resetpassword")
    public String showResetPasswordForm() {
    
        return "web/quenmatkhau/resetpassword"; // Đường dẫn đến template đăng ký
    }
	
}
