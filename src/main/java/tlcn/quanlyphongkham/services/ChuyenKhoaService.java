package tlcn.quanlyphongkham.services;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.repositories.ChuyenKhoaRepository;

@Service
@RequiredArgsConstructor
public class ChuyenKhoaService {

    private final ChuyenKhoaRepository chuyenKhoaRepository;

    public List<ChuyenKhoa> getAllChuyenKhoa() {
        return chuyenKhoaRepository.findAll();
    }
}
