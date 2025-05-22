package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
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
}