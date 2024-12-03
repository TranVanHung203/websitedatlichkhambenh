package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;

@Service
public class BenhNhanService {

    @Autowired
    private BenhNhanRepository benhNhanRepository;

    public BenhNhan save(BenhNhan benhNhan) {
        return benhNhanRepository.save(benhNhan);
    }

	public BenhNhan findById(String nguoiDungId) {
		BenhNhan benhNhan = benhNhanRepository.findByNguoiDung_NguoiDungId(nguoiDungId);
		return benhNhan;
	}

	public BenhNhan findByPhone(String patientPhone) {
        return benhNhanRepository.findByDienThoai(patientPhone);
    }
}
