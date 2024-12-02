package tlcn.quanlyphongkham.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException; // Import DisabledException
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

@Service
public class NguoiDungDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting login for user: " + username);
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username);
        if (nguoiDung == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        if (!nguoiDung.getTrangthai().equals("ACTIVE")) {  // Check if the user is active
            // Throw DisabledException when account is inactive or not active
            throw new DisabledException("User account is not active.");
        }
        // Return CustomUserDetails with String NguoiDungId
        return new CustomUserDetails(
                nguoiDung.getTenDangNhap(),
                nguoiDung.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro())),
                nguoiDung.getNguoiDungId()  // Assuming NguoiDungId is a String
        );
    }
}
