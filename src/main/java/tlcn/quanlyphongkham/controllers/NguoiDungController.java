package tlcn.quanlyphongkham.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import tlcn.quanlyphongkham.dtos.DangKyDTO;
import tlcn.quanlyphongkham.entities.NguoiDung;

@Controller
public class NguoiDungController {
    
    @GetMapping("/user/editprofile")
    public String editprofile() {
        return "benhnhan/editprofile/editprofile";
    }
}
