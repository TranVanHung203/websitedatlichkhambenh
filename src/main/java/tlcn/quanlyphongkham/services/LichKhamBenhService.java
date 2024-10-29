package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.repositories.LichKhamBenhRepository;

import java.time.LocalDate;
import java.util.List;

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
}
