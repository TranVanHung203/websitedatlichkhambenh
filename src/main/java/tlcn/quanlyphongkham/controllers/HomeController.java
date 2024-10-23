package tlcn.quanlyphongkham.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import tlcn.quanlyphongkham.entities.NguoiDung;

@Controller
public class HomeController {
    
    @GetMapping("/home")
    public String home(Model model, HttpSession session) {
        NguoiDung loggedUser = (NguoiDung) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            model.addAttribute("tenNguoiDung", loggedUser.getTenDangNhap());
            return "benhnhan/home/home"; // Đường dẫn đến template trang chủ
        } else {
            return "redirect:/login"; // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
        }
    }
}
