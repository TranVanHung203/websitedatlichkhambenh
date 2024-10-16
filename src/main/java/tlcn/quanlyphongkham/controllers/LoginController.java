package tlcn.quanlyphongkham.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String login() {
	    return "web/dangnhap/dangnhap"; // Use the full path to the template
	}

}
