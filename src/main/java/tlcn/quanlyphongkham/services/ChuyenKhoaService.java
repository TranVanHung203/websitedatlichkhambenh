package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
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
    
    public void saveChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        chuyenKhoaRepository.save(chuyenKhoa);
    }
    
    public void updateChuyenKhoa(ChuyenKhoa chuyenKhoa) {
        chuyenKhoaRepository.save(chuyenKhoa); // JpaRepository sẽ tự động xử lý cập nhật nếu ID đã tồn tại
    }

	public ChuyenKhoa getChuyenKhoaById(String chuyenKhoaId) {
		
		return chuyenKhoaRepository.getChuyenKhoaBychuyenKhoaId(chuyenKhoaId);
	}

	

   
}