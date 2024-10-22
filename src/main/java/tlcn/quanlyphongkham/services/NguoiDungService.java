package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

import java.util.List;

@Service
public class NguoiDungService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public List<NguoiDung> getAllNguoiDung() {
        return nguoiDungRepository.findAll();
    }

    public NguoiDung saveNguoiDung(NguoiDung nguoiDung) {
        return nguoiDungRepository.save(nguoiDung);
    }

	public NguoiDung findByToken(String token) {
		return nguoiDungRepository.findByToken(token);
	}

	public Object findByEmail(String email) {
		return nguoiDungRepository.findByEmail(email);
	}
	
	public NguoiDung validateLogin(String tenDangNhap, String rawPassword) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
                
        if (user != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            if (encoder.matches(rawPassword, user.getMatKhau())) {
          
                return user; // Trả về người dùng nếu mật khẩu khớp
            }																																								
        }
        return null; // Trả về null nếu không tìm thấy người dùng hoặc mật khẩu sai
    }
	
	
	
	
	


	

	
}