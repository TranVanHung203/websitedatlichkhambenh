package tlcn.quanlyphongkham.security;

import java.io.IOException;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

	 @Override
	    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	            AuthenticationException exception) throws IOException, ServletException {
	        String errorMessage = exception.getMessage(); // Default message

	        if (exception.getMessage().equals("Bad credentials")) {
	            errorMessage = "Sai tên tài khoản hoặc mật khẩu";
	        }

	        // Lưu thông báo lỗi vào session
	        request.getSession().setAttribute("error", errorMessage);

	        // Chuyển hướng về trang đăng nhập
	        response.sendRedirect("/ac_login");
	    }
}
