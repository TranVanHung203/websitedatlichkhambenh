package tlcn.quanlyphongkham.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                        .requestMatchers("/register/**","/confirm","/reset-password","/forgot-password","/forgetpass").permitAll()
                        .requestMatchers("/ac_login").permitAll()
                        .requestMatchers("/admin/**").hasRole("Admin")
                        .requestMatchers("/user/**").hasRole("BenhNhan")
                        .requestMatchers("/bacsi/**").hasRole("BacSi")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.loginPage("/login")
                        .loginProcessingUrl("/ac_login")
                        .usernameParameter("tenDangNhapOrEmail")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)  // Custom failure handler
                        .permitAll())
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll())
                .exceptionHandling(except -> except.accessDeniedPage("/403"))
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