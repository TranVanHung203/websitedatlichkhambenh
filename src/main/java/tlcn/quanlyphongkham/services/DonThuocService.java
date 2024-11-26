package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tlcn.quanlyphongkham.dtos.DonThuocDTO;
import tlcn.quanlyphongkham.entities.DonThuoc;
import tlcn.quanlyphongkham.entities.DonThuocThuoc;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.repositories.DonThuocRepository;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.ThuocRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DonThuocService {
    @Autowired
    private DonThuocRepository donThuocRepository;

    @Autowired
    private ThuocRepository thuocRepository;

    public void save(DonThuoc donThuoc) {
        donThuocRepository.save(donThuoc);
    }
    public List<DonThuoc> getAllDonThuoc() {
        return donThuocRepository.findAll();
    }
	public List<Thuoc> searchDrugs(String query) {
		 return thuocRepository.findByTenContaining(query);
	}
	
	
	
}
