package tlcn.quanlyphongkham.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tlcn.quanlyphongkham.security.CustomAuthenticationFailureHandler;
import tlcn.quanlyphongkham.security.CustomAuthenticationSuccessHandler;
import tlcn.quanlyphongkham.security.NguoiDungDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private NguoiDungDetailsService nguoiDungDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(nguoiDungDetailsService); // Ensure NguoiDungDetailsService is used
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/404").permitAll()
                        .requestMatchers("/forgetpass/**").permitAll()
                        .requestMatchers("/register/**", "/confirm", "/reset-password", "/forgot-password", "/forgetpass").permitAll()
                        .requestMatchers("/ac_login").permitAll()
                        .requestMatchers("/admin/**", "/bacsi").hasRole("Admin")
                        .requestMatchers("/user/**").hasRole("BenhNhan")
                        .requestMatchers("/bacsi/**", "/bacsi").hasRole("BacSi")
                        .requestMatchers("/nhanvien/**").hasRole("NhanVien")
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/chat/msgbn").hasRole("BenhNhan") 
                        .requestMatchers("/chat/msgbs").hasRole("BacSi") 
                        .requestMatchers("/home", "/view/schedule", "/findbs", "/departments/**", "/chitietbacsi/**", "/user/dangkylichkham","/chatbot/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/ac_login")
                        .usernameParameter("tenDangNhapOrEmail")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)  // Custom failure handler
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")  // Redirect to login after logout
                        .permitAll())
                .exceptionHandling(except -> except
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))  // Default handling for 403 errors
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.NOT_FOUND), new AntPathRequestMatcher("/404"))
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                )
                // Add a custom filter to handle redirection when accessing /login after login
                .addFilterBefore((request, response, chain) -> {
                    // Cast to HttpServletRequest and HttpServletResponse to access specific methods
                    HttpServletRequest httpRequest = (HttpServletRequest) request;
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    
                    if (httpRequest.getRequestURI().equals("/login")) {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated()) {
                            // If the user is already authenticated, redirect based on role
                            if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_Admin"))) {
                                httpResponse.sendRedirect("/admin/qltk");  // Redirect to Admin dashboard
                            } else if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_BacSi"))) {
                                httpResponse.sendRedirect("/bacsi/editprofile");  // Redirect to BacSi dashboard
                            } else if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_BenhNhan"))) {
                                httpResponse.sendRedirect("/home");  // Redirect to BenhNhan dashboard
                            } else if (authentication.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_NhanVien"))) {
                                httpResponse.sendRedirect("/nhanvien/dangkylichkham");  // Redirect to BenhNhan dashboard
                            } else {
                                httpResponse.sendRedirect("/login");  // Default redirect if role is not matched
                            }
                        }
                    }
                    chain.doFilter(request, response);  // Continue the request processing
                }, UsernamePasswordAuthenticationFilter.class)  // Apply this before the login filter
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Use BCryptPasswordEncoder for password encoding
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/images/**", "/js/**");
    }
}
