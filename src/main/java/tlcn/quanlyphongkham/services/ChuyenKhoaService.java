package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.repositories.ChuyenKhoaRepository;

import java.util.List;

@Service
public class ChuyenKhoaService {

    @Autowired
    private ChuyenKhoaRepository chuyenKhoaRepository;

    public List<ChuyenKhoa> getAllChuyenKhoa() {
        return chuyenKhoaRepository.findAll();
    }
    public Page<ChuyenKhoa> getChuyenKhoasPaginated(int page, int size) {
        return chuyenKhoaRepository.findAll(PageRequest.of(page, size));
    }

    public Page<ChuyenKhoa> searchChuyenKhoasPaginated(String ten, int page, int size) {
        return chuyenKhoaRepository.findByTenContaining(ten, PageRequest.of(page, size));
    }
    

    public void saveChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        chuyenKhoaRepository.save(chuyenKhoa);
    }
    
    public void updateChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        chuyenKhoaRepository.save(chuyenKhoa); // JpaRepository sẽ tự động xử lý cập nhật nếu ID đã tồn tại
    }

	public ChuyenKhoa getChuyenKhoaById(String chuyenKhoaId) {
		
		return chuyenKhoaRepository.getChuyenKhoaBychuyenKhoaId(chuyenKhoaId);
	}

    public ChuyenKhoa findById(String chuyenKhoaId) {
        return chuyenKhoaRepository.findById(chuyenKhoaId).orElse(null);
    }

   
}