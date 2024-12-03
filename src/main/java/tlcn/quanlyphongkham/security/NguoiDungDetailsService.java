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

        // Lấy thông tin người dùng từ database
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username);

        // Kiểm tra nếu không tìm thấy người dùng
        if (nguoiDung == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // Kiểm tra trạng thái của tài khoản người dùng (trạng thái "ACTIVE")
        if (!nguoiDung.getTrangthai().equals("ACTIVE")) {
            // Nếu tài khoản không kích hoạt, ném DisabledException
            throw new DisabledException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt!");
        }

        // Trả về CustomUserDetails với các thông tin chi tiết của người dùng
        return new CustomUserDetails(
                nguoiDung.getTenDangNhap(),                // Tên đăng nhập
                nguoiDung.getMatKhau(),                    // Mật khẩu
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + nguoiDung.getVaiTro())),  // Quyền của người dùng
                nguoiDung.getNguoiDungId()  // ID người dùng (nếu cần)
        );
    }
}
