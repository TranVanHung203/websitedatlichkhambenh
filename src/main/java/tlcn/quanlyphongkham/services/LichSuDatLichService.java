package tlcn.quanlyphongkham.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.repositories.SlotThoiGianRepository;

import java.time.LocalDate;

@Service
public class LichSuDatLichService {

    @Autowired
    private SlotThoiGianRepository slotThoiGianRepository;

    // Lấy lịch sử đặt lịch của bệnh nhân với phân trang
    public Page<SlotThoiGian> getLichSuDatLichByDate(LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return slotThoiGianRepository.findByLichKhamBenhNgayThangNam(date, pageable);
    }

    public Page<SlotThoiGian> getLichSuDatLichByDateAndBenhNhanId(String benhNhanId, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Find slots by benhNhanId and date, and map time slots to the appropriate shift (morning, afternoon, outside hours)
      return slotThoiGianRepository.findByBenhNhan_BenhNhanIdAndLichKhamBenh_NgayThangNam(benhNhanId, date, pageable);
    }
}
