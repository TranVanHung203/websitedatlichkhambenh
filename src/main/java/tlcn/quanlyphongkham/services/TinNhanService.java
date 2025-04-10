package tlcn.quanlyphongkham.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.dtos.TinNhanDTO;
import tlcn.quanlyphongkham.entities.TinNhan;
import tlcn.quanlyphongkham.repositories.TinNhanRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TinNhanService {

    private final TinNhanRepository tinNhanRepository;

    public TinNhan saveTinNhan(TinNhanDTO dto) {
        TinNhan tn = new TinNhan();
        tn.setNoiDung(dto.getNoiDung());
        tn.setTuBenhNhan(dto.isTuBenhNhan());
        tn.setThoiGian(LocalDateTime.now());
        tn.setBenhNhanId(dto.getBenhNhanId());
        tn.setBacSiId(dto.getBacSiId());
        return tinNhanRepository.save(tn);
    }

    public List<TinNhan> getLichSu(String benhNhanId, String bacSiId) {
        return tinNhanRepository.findByBenhNhanIdAndBacSiIdOrderByThoiGianAsc(benhNhanId, bacSiId);
    }
}
