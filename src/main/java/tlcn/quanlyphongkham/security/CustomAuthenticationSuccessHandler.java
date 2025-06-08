package tlcn.quanlyphongkham.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // Get the user's role from the authentication object
        String role = authentication.getAuthorities().stream()
                .filter(authority -> authority instanceof SimpleGrantedAuthority)
                .map(authority -> ((SimpleGrantedAuthority) authority).getAuthority())
                .findFirst()
                .orElse("");

        // Redirect based on role
        if (role.equals("ROLE_Admin")) {
            response.sendRedirect("/admin/qltk");  // Redirect to admin dashboard
        } else if (role.equals("ROLE_BenhNhan")) {
            response.sendRedirect("/home");  // Redirect to user homepage
        } else if (role.equals("ROLE_BacSi")) {
            response.sendRedirect("/bacsi/home");
        } else if (role.equals("ROLE_NhanVien")) {
            response.sendRedirect("/nhanvien/home");// Redirect to doctor dashboard
        } else {
            response.sendRedirect("/login");  // Default redirect for other roles
        }
    }
}
