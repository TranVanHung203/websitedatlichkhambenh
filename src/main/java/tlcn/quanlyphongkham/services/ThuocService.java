package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.repositories.ThuocRepository;
import tlcn.quanlyphongkham.services.ThuocService;

import java.util.List;

@Service
public class  ThuocService {

    @Autowired
    private ThuocRepository thuocRepository;

    
    public List<Thuoc> getAllThuoc() {
        return thuocRepository.findAll();  // Lấy tất cả thuốc từ database
    }
    
    public void deleteThuoc(Long thuocId) {
        thuocRepository.deleteById(thuocId);
    }
    
    public void updateThuoc(Thuoc thuoc) {
        thuocRepository.save(thuoc);
    }

    public Thuoc getThuocById(Long thuocId) {
        return thuocRepository.findById(thuocId).orElse(null);
    }
    
    public void saveThuoc(Thuoc thuoc) {
        thuocRepository.save(thuoc); // Lưu thuốc vào database
    }
    public Thuoc findThuocById(Long thuocId) {
        return thuocRepository.findById(thuocId).orElse(null);
    }
}