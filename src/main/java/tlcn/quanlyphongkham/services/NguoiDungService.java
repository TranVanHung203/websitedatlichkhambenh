package tlcn.quanlyphongkham.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tlcn.quanlyphongkham.dtos.NguoiDungDTO;
import tlcn.quanlyphongkham.dtos.ThemTaiKhoanDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.ChiTietBacSiRepository;
import tlcn.quanlyphongkham.repositories.ChuyenKhoaRepository;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

@Service
public class NguoiDungService {

	@Autowired
	private NguoiDungRepository nguoiDungRepository;
	@Autowired
	private BacSiRepository bacSiRepository;
	@Autowired
	private ChiTietBacSiRepository chiTietBacSiRepository;
	@Autowired
	private BenhNhanRepository benhNhanRepository;
	
	@Autowired
	private ChuyenKhoaRepository chuyenKhoaRepository;

	public List<NguoiDung> getAllNguoiDung() {
		return nguoiDungRepository.findAll();
	}

	public NguoiDung saveNguoiDung(NguoiDung nguoiDung) {
		return nguoiDungRepository.save(nguoiDung);
	}

	public NguoiDung findByToken(String token) {
		return nguoiDungRepository.findByToken(token);
	}

	public NguoiDung findByEmail(String email) {
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

	public void updateNguoiDung(NguoiDung nguoiDung) {
		// Lưu các thông tin đã cập nhật của người dùng
		nguoiDungRepository.save(nguoiDung);
	}

	public NguoiDung findById(String nguoiDungId) {
		NguoiDung nguoidung = nguoiDungRepository.findByNguoiDungId(nguoiDungId);
		return nguoidung;
	}

	public NguoiDung findByTenDangNhap(String tenDangNhap) {
		NguoiDung nguoidung = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
		return nguoidung;
	}

	 public Page<NguoiDungDTO> getAllNguoiDungQLTK(String search, int page) {
	        Pageable pageable = PageRequest.of(page - 1, 5); // 5 là số bản ghi mỗi trang
	        Page<NguoiDung> nguoiDungPage;

	        if (search != null && !search.isEmpty()) {
	            nguoiDungPage = nguoiDungRepository.findByEmailContainingOrTenDangNhapContaining(search, search, pageable);
	        } else {
	            nguoiDungPage = nguoiDungRepository.findAll(pageable);
	        }

	        // Chuyển đổi Page<NguoiDung> thành Page<NguoiDungDTO>
	        Page<NguoiDungDTO> nguoiDungDTOPage = nguoiDungPage.map(nguoiDung -> new NguoiDungDTO(
	                nguoiDung.getNguoiDungId(),
	                nguoiDung.getTenDangNhap(),
	                nguoiDung.getEmail(),
	                nguoiDung.getVaiTro(),
	                nguoiDung.getTrangthai()
	        ));

	        return nguoiDungDTOPage;
	    }


	
	private NguoiDungDTO convertToDTO(NguoiDung nguoiDung) {
		return new NguoiDungDTO(nguoiDung.getNguoiDungId(), nguoiDung.getTenDangNhap(), nguoiDung.getEmail(),
				nguoiDung.getVaiTro(), nguoiDung.getTrangthai() // Lấy trạng thái từ entity
		);
	}

	// Phương thức cập nhật thông tin người dùng
	public NguoiDungDTO updateNguoiDung(NguoiDungDTO nguoiDungDTO) {
		// Tìm người dùng theo ID
		Optional<NguoiDung> existingNguoiDungOpt = nguoiDungRepository.findById(nguoiDungDTO.getNguoiDungId());

		if (existingNguoiDungOpt.isPresent()) {
			NguoiDung existingNguoiDung = existingNguoiDungOpt.get();

			// Cập nhật các trường
			existingNguoiDung.setTenDangNhap(nguoiDungDTO.getTenDangNhap());
			existingNguoiDung.setEmail(nguoiDungDTO.getEmail());
		
			existingNguoiDung.setTrangthai(nguoiDungDTO.getTrangthai());
		
			// Lưu thay đổi vào cơ sở dữ liệu
			nguoiDungRepository.save(existingNguoiDung);

			// Trả về đối tượng DTO đã cập nhật
			return nguoiDungDTO;
		} else {
			throw new RuntimeException("Không tìm thấy người dùng với ID: " + nguoiDungDTO.getNguoiDungId());
		}
	}

	// Phương thức xóa người dùng theo ID
	public void deleteNguoiDung(String nguoiDungId) {
		nguoiDungRepository.deleteById(nguoiDungId);
	
	}

	// Check if email already exists for another user (excluding the current user by
	// ID)
	public boolean emailExistsForAnotherUser(String email, String nguoiDungId) {
		return nguoiDungRepository.existsByEmailAndNguoiDungIdNot(email, nguoiDungId);
	}

	// Check if username already exists for another user (excluding the current user
	// by ID)
	public boolean usernameExistsForAnotherUser(String tenDangNhap, String nguoiDungId) {
		return nguoiDungRepository.existsByTenDangNhapAndNguoiDungIdNot(tenDangNhap, nguoiDungId);
	}

	@Transactional
	public void themNguoiDung(ThemTaiKhoanDTO themTaiKhoanDTO) throws Exception {
		// Mã hóa mật khẩu
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(themTaiKhoanDTO.getMatKhau());

		// Thêm người dùng
		NguoiDung nguoiDung = new NguoiDung();
		nguoiDung.setNguoiDungId(UUID.randomUUID().toString());
		nguoiDung.setTenDangNhap(themTaiKhoanDTO.getTenDangNhap());
		nguoiDung.setMatKhau(encodedPassword); // Lưu mật khẩu đã mã hóa
		nguoiDung.setEmail(themTaiKhoanDTO.getEmail());
		nguoiDung.setVaiTro(themTaiKhoanDTO.getVaiTro());
		nguoiDung.setTrangthai("ACTIVE");

		nguoiDungRepository.save(nguoiDung);

		// Nếu là Bác sĩ, thêm vào bảng BacSi và ChiTietBacSi
		if ("BacSi".equals(themTaiKhoanDTO.getVaiTro())) {
			BacSi bacSi = new BacSi();
			bacSi.setBacSiId(UUID.randomUUID().toString());
			bacSi.setNguoiDung(nguoiDung);
			bacSi.setTen(themTaiKhoanDTO.getTen());
			bacSi.setDienThoai(themTaiKhoanDTO.getDienThoai());
			bacSi.setDiaChi(themTaiKhoanDTO.getDiaChi());
			bacSi.setNgaySinh(themTaiKhoanDTO.getNgaySinh());
			bacSi.setGioiTinh(themTaiKhoanDTO.getGioiTinh());
			Optional<ChuyenKhoa> chuyenKhoa=chuyenKhoaRepository.findById(themTaiKhoanDTO.getChuyenKhoaId());
			bacSi.setChuyenKhoa(chuyenKhoa.get());
			
			if ("Nam".equals(themTaiKhoanDTO.getGioiTinh()))
				bacSi.setUrlAvatar("/uploads/f9dcf22c-ca7f-4259-b193-ff5de5f23563-bacsinam.jpg");
			else
				bacSi.setUrlAvatar("/uploads/f9dcf22c-ca7f-4259-b193-ff5de5f23563-bacsinu.jpg");

			bacSiRepository.save(bacSi);

			// Thêm ChiTietBacSi
			ChiTietBacSi chiTietBacSi = new ChiTietBacSi();
			chiTietBacSi.setBacSi(bacSi);
			chiTietBacSiRepository.save(chiTietBacSi);
		}

		// Nếu là Bệnh nhân, thêm vào bảng BenhNhan
		else if ("BenhNhan".equals(themTaiKhoanDTO.getVaiTro())) {
			BenhNhan benhNhan = new BenhNhan();
			benhNhan.setBenhNhanId(UUID.randomUUID().toString());
			benhNhan.setNguoiDung(nguoiDung);
			benhNhan.setTen(themTaiKhoanDTO.getTen());
			benhNhan.setDienThoai(themTaiKhoanDTO.getDienThoai());
			benhNhan.setDiaChi(themTaiKhoanDTO.getDiaChi());
			benhNhan.setNgaySinh(themTaiKhoanDTO.getNgaySinh());
			benhNhan.setGioiTinh(themTaiKhoanDTO.getGioiTinh());

			benhNhanRepository.save(benhNhan);
		}
	}

	public boolean emailExists(String email) {
		return nguoiDungRepository.existsByEmail(email);
	}

	// Kiểm tra tên đăng nhập tồn tại cho người dùng mới
	public boolean usernameExists(String username) {
		return nguoiDungRepository.existsByTenDangNhap(username);
	}

	public NguoiDung findBySDT(String dienThoai) {
        BenhNhan benhNhan = benhNhanRepository.findByDienThoai(dienThoai);
        if (benhNhan != null) {
            return benhNhan.getNguoiDung();
        }
        return null;  // Or throw an exception, depending on your error handling strategy
    }

	public List<NguoiDung> findByVaiTro(String string) {
		 return nguoiDungRepository.findByVaiTro(string);
	}

}
