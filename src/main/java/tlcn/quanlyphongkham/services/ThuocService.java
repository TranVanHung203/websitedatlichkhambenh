package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    
    public Page<Thuoc> getThuocsPaginated(int page, int size) {
        return thuocRepository.findAll(PageRequest.of(page, size));
    }
    
    public Page<Thuoc> searchThuocsPaginated(String ten, int page, int size) {
        return thuocRepository.findByTenContaining(ten, PageRequest.of(page, size));
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

	public Page<Thuoc> searchThuocsByNhaCungCapPaginated(String nhaCungCap, int page, int pageSize) {
		// TODO Auto-generated method stub
		return thuocRepository.findByNhaCungCapContaining(nhaCungCap, PageRequest.of(page, pageSize));
	}
}