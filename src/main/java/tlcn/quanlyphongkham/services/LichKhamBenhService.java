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
import tlcn.quanlyphongkham.repositories.LichKhamBenhRepository;

@Service
public class LichKhamBenhService {
    private final LichKhamBenhRepository lichKhamBenhRepository;

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
}
