package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tlcn.quanlyphongkham.dtos.NguoiDungDTO;
import tlcn.quanlyphongkham.dtos.ThemTaiKhoanDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.ChiTietBacSiRepository;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

	public List<NguoiDungDTO> getAllNguoiDungQLTK() {
		List<NguoiDung> nguoiDungList = nguoiDungRepository.findAll();
		return nguoiDungList.stream().filter(nguoiDung -> !nguoiDung.getTrangthai().equals("DISABLE")) // Kiểm tra trạng
																										// thái
				.map(this::convertToDTO).collect(Collectors.toList());
	}

	// Chuyển đổi Entity sang DTO
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
			existingNguoiDung.setVaiTro(nguoiDungDTO.getVaiTro());
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
		Optional<NguoiDung> existingNguoiDungOpt = nguoiDungRepository.findById(nguoiDungId);

		if (existingNguoiDungOpt.isPresent()) {
			NguoiDung existingNguoiDung = existingNguoiDungOpt.get();

			existingNguoiDung.setTrangthai("DISABLE");
			existingNguoiDung.setEmail(null);
			existingNguoiDung.setTenDangNhap(null);
			// Lưu thay đổi vào cơ sở dữ liệu
			nguoiDungRepository.save(existingNguoiDung);

		} else {
			throw new RuntimeException("Không tìm thấy người dùng với ID: ");
		}
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

}
