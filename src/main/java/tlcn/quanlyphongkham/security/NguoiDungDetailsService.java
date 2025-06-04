package tlcn.quanlyphongkham.security;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

@Service
public class NguoiDungDetailsService implements UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("Attempting login for: " + usernameOrEmail);

        // Tìm người dùng bằng tên đăng nhập hoặc email
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(usernameOrEmail);
        if (nguoiDung == null) {
            nguoiDung = nguoiDungRepository.findByEmail(usernameOrEmail);
        }

        // Kiểm tra nếu không tìm thấy người dùng
        if (nguoiDung == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        // Kiểm tra trạng thái của tài khoản người dùng (trạng thái "ACTIVE")
        if (!nguoiDung.getTrangthai().equals("ACTIVE")) {
            throw new DisabledException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt!");
        }

        // Trả về CustomUserDetails với các thông tin chi tiết của người dùng
        return new CustomUserDetails(
                nguoiDung.getTenDangNhap(),                // Tên đăng nhập
                nguoiDung.getMatKhau(),                    // Mật khẩu
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro())),  // Quyền của người dùng
                nguoiDung.getNguoiDungId()                 // ID người dùng
        );
    }
}