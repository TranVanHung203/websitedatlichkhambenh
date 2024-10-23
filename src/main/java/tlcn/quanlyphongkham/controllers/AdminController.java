package tlcn.quanlyphongkham.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import tlcn.quanlyphongkham.entities.NguoiDung;

@Controller
public class AdminController {
    
    @GetMapping("/admin/qltk")
    public String qltk() {
    	return "admin/quanlytaikhoan/quanlytaikhoan";
    }
}
