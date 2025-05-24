package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.LoaiXetNghiem;
import tlcn.quanlyphongkham.repositories.LoaiXetNghiemRepository;

import java.util.List;

@Service
public class LoaiXetNghiemService {

    @Autowired
    private LoaiXetNghiemRepository loaiXetNghiemRepository;

    public List<LoaiXetNghiem> findAll() {
        return loaiXetNghiemRepository.findAll();
    }

    public Page<LoaiXetNghiem> getLoaiXetNghiemsPaginated(int page, int pageSize) {
        return loaiXetNghiemRepository.findAll(PageRequest.of(page, pageSize));
    }


    public Page<LoaiXetNghiem> searchLoaiXetNghiemsPaginated(String ten, int page, int pageSize) {
        return loaiXetNghiemRepository.findByTenXetNghiemContainingIgnoreCase(ten, PageRequest.of(page, pageSize));
    }


    public LoaiXetNghiem getLoaiXetNghiemById(Long id) {
        return loaiXetNghiemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại xét nghiệm với ID: " + id));
    }


    public void saveLoaiXetNghiem(LoaiXetNghiem loaiXetNghiem) {
        loaiXetNghiemRepository.save(loaiXetNghiem);
    }

    public void updateLoaiXetNghiem(LoaiXetNghiem loaiXetNghiem) {
        if (loaiXetNghiemRepository.existsById(loaiXetNghiem.getId())) {
            loaiXetNghiemRepository.save(loaiXetNghiem);
        } else {
            throw new RuntimeException("Không tìm thấy loại xét nghiệm để cập nhật");
        }
    }

    public boolean deleteLoaiXetNghiem(Long id) {
        if (loaiXetNghiemRepository.existsById(id)) {
            loaiXetNghiemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Tìm loại xét nghiệm theo ID
    public LoaiXetNghiem findById(Long id) {
        return loaiXetNghiemRepository.findById(id).orElse(null);
    }
    
}