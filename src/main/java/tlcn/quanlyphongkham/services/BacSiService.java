package tlcn.quanlyphongkham.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.ChuyenKhoaRepository;

@Service
@RequiredArgsConstructor
public class BacSiService {

    private final BacSiRepository bacSiRepository; // Thay đổi này
    private final ChuyenKhoaRepository chuyenKhoaRepository; // Thay đổi này

    public List<BacSiDTO> getBacSiByChuyenKhoa(String chuyenKhoaId) {
        ChuyenKhoa chuyenKhoa = chuyenKhoaRepository.findById(chuyenKhoaId)
            .orElseThrow(() -> new EntityNotFoundException("Chuyên khoa không tồn tại"));
        return bacSiRepository.findByChuyenKhoa(chuyenKhoa).stream()
                .map(bacSi -> new BacSiDTO(bacSi.getBacSiId(), bacSi.getTen()))
                .collect(Collectors.toList());
    }

	public Optional<BacSi> findBacSiById(String bacSiId) {
		
		return bacSiRepository.findById(bacSiId);
	}

	
}
