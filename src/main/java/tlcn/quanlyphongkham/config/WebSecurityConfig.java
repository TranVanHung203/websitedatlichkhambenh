package tlcn.quanlyphongkham.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
    	        .csrf(csrf -> csrf.disable())
    	        .authorizeHttpRequests((authorize) -> authorize
    	        				.requestMatchers("/404").permitAll()
    	        				.requestMatchers("/forgetpass/**").permitAll()
    	        				.requestMatchers("/register/**").permitAll()
    	        				.requestMatchers("/user/**").permitAll()
    	        				.requestMatchers("/admin/**").permitAll()
    	        				.anyRequest().permitAll()
    	        )
    	        .formLogin(
    	                form -> form
    	                        .loginPage("/login").loginProcessingUrl("/perform_login")
    	                        .usernameParameter("username").passwordParameter("password")
    	                        .defaultSuccessUrl("/home", true)
    	                        .permitAll()
    	        )
    	        .logout(
    	                logout -> logout
    	                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
    	                        .permitAll()
    	        )
    	        .exceptionHandling(
    	        		except -> except
    	        				.accessDeniedPage("/403")
    	        )
    	        .build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
    	return (web) -> web.ignoring().requestMatchers("/css/**", "/images/**", "/js/**");
    }
}
