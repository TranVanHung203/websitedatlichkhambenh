package tlcn.quanlyphongkham.security;

import java.io.IOException;

import org.springframework.security.authentication.AccountStatusException;
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
        if (exception instanceof AccountStatusException) {
            // You can redirect or show a custom error message
            response.sendRedirect("/login?error=inactive");
        } else {
            // Default behavior for other exceptions
            response.sendRedirect("/login?error=true");
        }
    }
}
