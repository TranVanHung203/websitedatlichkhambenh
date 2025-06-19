package tlcn.quanlyphongkham.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.ChiTietBacSiDTO;
import tlcn.quanlyphongkham.dtos.EditProfileBSDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.ChiTietBacSiRepository;
import tlcn.quanlyphongkham.repositories.ChuyenKhoaRepository;

@Service
public class BacSiService {

	@Autowired
	private BacSiRepository bacSiRepository;
	@Autowired
	private ChuyenKhoaRepository chuyenKhoaRepository; // Thay đổi này
	@Autowired
	private ChiTietBacSiRepository chiTietBacSiRepository; // Thay đổi này
	
	public List<BacSi> getDoctorsByChuyenKhoa(String chuyenKhoaId) {
        return bacSiRepository.findByChuyenKhoaChuyenKhoaId(chuyenKhoaId);
    }
	public List<BacSi> getAllDoctors() {
		return bacSiRepository.findAll();
	}

	public BacSi getDoctorById(String bacSiId) {
		return bacSiRepository.findById(bacSiId).orElse(null);
	}

	public BacSi findById(String bacSiId) {
		// Sử dụng findById để tìm bác sĩ theo ID
		Optional<BacSi> bacSiOptional = bacSiRepository.findById(bacSiId);
		return bacSiOptional.orElse(null); // Trả về bác sĩ nếu tìm thấy, ngược lại trả về null
	}

	public void updateDoctor(String bacSiId, BacSi updatedDoctor, ChiTietBacSi updatedChiTiet) {
	    BacSi existingDoctor = bacSiRepository.findById(bacSiId).orElse(null);
	    
	    if (existingDoctor != null) {
	        // Cập nhật thông tin bác sĩ nếu có thay đổi
	        if (updatedDoctor.getTen() != null) {
	            existingDoctor.setTen(updatedDoctor.getTen());
	        }
	        if (updatedDoctor.getDiaChi() != null) {
	            existingDoctor.setDiaChi(updatedDoctor.getDiaChi());
	        }
	        if (updatedDoctor.getDienThoai() != null) {
	            existingDoctor.setDienThoai(updatedDoctor.getDienThoai());
	        }
	        if (updatedDoctor.getChuyenKhoa() != null) {
	            existingDoctor.setChuyenKhoa(updatedDoctor.getChuyenKhoa());
	        }
	        if (updatedDoctor.getGioiTinh() != null) {
	            existingDoctor.setGioiTinh(updatedDoctor.getGioiTinh());
	        }
	        if (updatedDoctor.getNgaySinh() != null) {
	            existingDoctor.setNgaySinh(updatedDoctor.getNgaySinh());
	        }
	        // Cập nhật giá khám nếu có giá mới
	        if (updatedDoctor.getGiaKham() != null) {
	            existingDoctor.setGiaKham(updatedDoctor.getGiaKham());
	        }

	        // Cập nhật avatar nếu có ảnh mới
	        if (updatedDoctor.getUrlAvatar() != null && !updatedDoctor.getUrlAvatar().isEmpty()) {
	            existingDoctor.setUrlAvatar(updatedDoctor.getUrlAvatar());
	        }

	        // Lưu thông tin đã cập nhật vào database
	        bacSiRepository.save(existingDoctor);
	    }
	}


	public void updateDoctorDetails(String bacSiId, ChiTietBacSi updatedChiTiet) {
		BacSi existingDoctor = bacSiRepository.findById(bacSiId).orElse(null);
		if (existingDoctor != null) {
			ChiTietBacSi chiTiet = existingDoctor.getChiTietBacSi();
			if (chiTiet != null) {
				chiTiet.setBangCap(updatedChiTiet.getBangCap());
				chiTiet.setHoiNghiNuocNgoai(updatedChiTiet.getHoiNghiNuocNgoai());
				chiTiet.setChungChi(updatedChiTiet.getChungChi());
				chiTiet.setDaoTaoChuyenNganh(updatedChiTiet.getDaoTaoChuyenNganh());
				chiTiet.setLinhVucChuyenSau(updatedChiTiet.getLinhVucChuyenSau());
				chiTiet.setGioiThieu(updatedChiTiet.getGioiThieu());
				bacSiRepository.save(existingDoctor); // Lưu lại cập nhật
			}
		}
	}

	public List<BacSiDTO> getBacSiByChuyenKhoa(String chuyenKhoaId) {
		ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(chuyenKhoaId)
				.orElseThrow(() -> new EntityNotFoundException("Chuyên khoa không tồn tại"));
		return bacSiRepository.findByChuyenKhoa(chuyenKhoa).stream()
				.map(bacSi -> new BacSiDTO(bacSi.getBacSiId(), bacSi.getTen())).collect(Collectors.toList());
	}

	public Optional<BacSi> findBacSiById(String bacSiId) {

		return bacSiRepository.findById(bacSiId);
	}

	public String getTenBacSi(String bacSiId) {
		BacSi bacSi = bacSiRepository.findById(bacSiId).orElse(null);
		return bacSi != null ? bacSi.getTen() : "Tên không xác định";
	}

	public EditProfileBSDTO getProfile(String bacSiId) {
        Optional<BacSi> bacSiOpt = bacSiRepository.findById(bacSiId);
        if (bacSiOpt.isPresent()) {
            BacSi bacSi = bacSiOpt.get();
            return new EditProfileBSDTO(
                bacSi.getBacSiId(), 
                bacSi.getTen(), 
                bacSi.getNgaySinh(), 
                bacSi.getGioiTinh(),
                bacSi.getDienThoai(), 
                bacSi.getDiaChi(), 
                bacSi.getNguoiDung().getEmail(),
                bacSi.getNguoiDung().getTenDangNhap(), 
                bacSi.getChuyenKhoa().getChuyenKhoaId(),
                bacSi.getUrlAvatar(),
                bacSi.getGiaKham() // Thêm trường giá khám
            );
        }
        return null;
    }

	// Lấy thông tin chi tiết của bác sĩ
	public ChiTietBacSiDTO getDetail(String bacSiId) {
		BacSi bacsi = bacSiRepository.findByBacSiId(bacSiId);
		Optional<ChiTietBacSi> detailOpt = chiTietBacSiRepository.findByBacSi(bacsi);
		if (detailOpt.isPresent()) {
			ChiTietBacSi detail = detailOpt.get();
			return new ChiTietBacSiDTO(detail.getBacSi().getBacSiId(), detail.getBangCap(),
					detail.getHoiNghiNuocNgoai(), detail.getChungChi(), detail.getDaoTaoChuyenNganh(),
					detail.getLinhVucChuyenSau(), detail.getGioiThieu());
		}
		return null;
	}

	// Cập nhật thông tin cá nhân
	public String updateProfile(EditProfileBSDTO profileDTO) {
        Optional<BacSi> bacSiOpt = bacSiRepository.findById(profileDTO.getBacSiId());
        if (bacSiOpt.isPresent()) {
            BacSi bacSi = bacSiOpt.get();
            bacSi.setTen(profileDTO.getTen());
            bacSi.setNgaySinh(profileDTO.getNgaySinh());
            bacSi.setGioiTinh(profileDTO.getGioiTinh());
            bacSi.setDienThoai(profileDTO.getDienThoai());
            bacSi.setDiaChi(profileDTO.getDiaChi());
            if (profileDTO.getAvatarurl() != null) {
                bacSi.setUrlAvatar(profileDTO.getAvatarurl());
            }
            if (profileDTO.getGiaKham() != null) {
                bacSi.setGiaKham(profileDTO.getGiaKham()); // Cập nhật giá khám
            }

            bacSi.getNguoiDung().setEmail(profileDTO.getEmail());
            bacSi.getNguoiDung().setTenDangNhap(profileDTO.getTenDangNhap());

            ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(profileDTO.getChuyenKhoaId())
                    .orElseThrow(() -> new RuntimeException("Specialty not found"));

            // Set the ChuyenKhoa to the BacSi entity
            bacSi.setChuyenKhoa(chuyenKhoa);
            bacSiRepository.save(bacSi);
            return "Cập nhật thông tin cá nhân thành công!";
        }
        return "Không tìm thấy bác sĩ.";
    }

	// Cập nhật thông tin chi tiết
	public String updateDetail(ChiTietBacSiDTO detailDTO) {
		BacSi bacsi = bacSiRepository.findByBacSiId(detailDTO.getBacSiId());
		Optional<ChiTietBacSi> detailOpt = chiTietBacSiRepository.findByBacSi(bacsi);
		if (detailOpt.isPresent()) {
			ChiTietBacSi detail = detailOpt.get();
			detail.setBangCap(detailDTO.getBangCap());
			detail.setHoiNghiNuocNgoai(detailDTO.getHoiNghiNuocNgoai());
			detail.setChungChi(detailDTO.getChungChi());
			detail.setDaoTaoChuyenNganh(detailDTO.getDaoTaoChuyenNganh());
			detail.setLinhVucChuyenSau(detailDTO.getLinhVucChuyenSau());
			detail.setGioiThieu(detailDTO.getGioiThieu());

			chiTietBacSiRepository.save(detail);
			return "Cập nhật thông tin chi tiết thành công!";
		}
		return "Không tìm thấy thông tin chi tiết của bác sĩ.";
	}

	public Page<BacSi> findAll(Pageable pageable) {
		return bacSiRepository.findAll(pageable);
	}

	public Page<BacSi> findByChuyenKhoaTen(String tenChuyenKhoa, Pageable pageable) {
	    return bacSiRepository.findByChuyenKhoa_Ten(tenChuyenKhoa, pageable); // Dùng phương thức tìm kiếm theo tên chuyên khoa
	}

	public Page<BacSi> findByNameAndChuyenKhoa(String doctorName, String section, Pageable pageable) {
	    if ("all".equals(section)) {
	        // Nếu không chọn chuyên khoa (hoặc chọn "all"), tìm bác sĩ theo tên
	        return bacSiRepository.findByTenContainingIgnoreCase(doctorName, pageable);
	    } else {
	        // Nếu có chuyên khoa, tìm bác sĩ theo tên và chuyên khoa
	        return bacSiRepository.findByTenContainingIgnoreCaseAndChuyenKhoa_Ten(doctorName, section, pageable);
	    }
	}

	
	 public Page<BacSi> searchByPhone(String phone, Pageable pageable) {
	        return bacSiRepository.findByDienThoaiContaining(phone, pageable);
	    }
	 public Page<BacSi> getDoctorsPaginated(int page, int size) {
	        return bacSiRepository.findAll(PageRequest.of(page, size));
	    }

	public BacSi findByNguoiDungId(String nguoiDungId) {
		// TODO Auto-generated method stub
		return bacSiRepository.findByNguoiDung_NguoiDungId(nguoiDungId);
	}

	

}