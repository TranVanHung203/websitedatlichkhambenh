package tlcn.quanlyphongkham.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.repositories.LichKhamBenhRepository;
import tlcn.quanlyphongkham.repositories.SlotThoiGianRepository;

@Service
public class LichKhamBenhService {
    private final LichKhamBenhRepository lichKhamBenhRepository;
    @Autowired
    private SlotThoiGianRepository slotThoiGianRepository;

	@Autowired
	public LichKhamBenhService(LichKhamBenhRepository lichKhamBenhRepository) {
		this.lichKhamBenhRepository = lichKhamBenhRepository;
	}

	// Phương thức tìm tất cả lịch khám theo ngày
	public List<LichKhamBenh> findAllByDate(LocalDate date) {
		return lichKhamBenhRepository.findByNgayThangNam(date);
	}

	public LichKhamBenh addLichKhamBenh(LichKhamBenh lichKhamBenh) {
		return lichKhamBenhRepository.save(lichKhamBenh);
	}

	public boolean existsByNgayThangNamAndCaAndBacSi(LocalDate ngayThangNam, String ca, BacSi bacSi) {

		return lichKhamBenhRepository.existsByNgayThangNamAndCaAndBacSi(ngayThangNam, ca, bacSi);
	}

	@Transactional
    public boolean deleteLichKhamBenh(String maLichKhamBenh) {
        Optional<LichKhamBenh> lichKhamBenh = lichKhamBenhRepository.findById(maLichKhamBenh);
        if (lichKhamBenh.isPresent()) {
            lichKhamBenhRepository.deleteById(maLichKhamBenh);
            return true;
        } else {
            return false;
        }
    }	
	 public List<LichKhamBenh> getLichKhamBenhByNgayAndBacSi(String idBacSi, LocalDate ngay) {
		
	        return lichKhamBenhRepository.findByIdBacSiAndNgayThangNam(idBacSi, ngay);
	    }

	 public void saveLichKhamBS(String idBacSi, LocalDate ngay, String ca) {
		    String maLichId = UUID.randomUUID().toString();  // Tạo ID duy nhất
		    try {
		        lichKhamBenhRepository.addNew(maLichId, idBacSi, ngay, ca);
		    } catch (Exception e) {
		        System.out.println("Lỗi trong saveLichKhamBS: " + e.getMessage());
		        throw e;
		    }
		}


	public void deleteLichKham(String lichId) {
		lichKhamBenhRepository.deleteById(lichId);
	}
	public boolean isLichKhamExist(String idBacSi, LocalDate ngay, String ca) {
	    // Kiểm tra trong cơ sở dữ liệu nếu có lịch khám với bác sĩ, ngày và ca đã cho
	    List<LichKhamBenh> existingLichKham = lichKhamBenhRepository.findByIdBacSiAndNgayAndCa(idBacSi, ngay, ca);
	    return !existingLichKham.isEmpty(); // Nếu tìm thấy thì trả về true
	}
	
	
	
	
	  public List<LichKhamBenh> getLichKhamByBacSiAndDate(String bacSiId, LocalDate date) {
	        return lichKhamBenhRepository.findByBacSi_BacSiIdAndNgayThangNam(bacSiId, date);
	    }

	    public List<SlotThoiGian> getSlotThoiGianByLichKham(String maLichKhamBenh) {
	        return slotThoiGianRepository.findByLichKhamBenh_MaLichKhamBenh(maLichKhamBenh);
	    }

}
