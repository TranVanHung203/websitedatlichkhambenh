package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.dtos.UserProfileDTO;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.NguoiDungRepository;

@Service
public class UserProfileService {
    @Autowired
    private BenhNhanRepository benhNhanRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    public UserProfileDTO getUserProfileByNguoiDungId(String nguoiDungId) {
        NguoiDung nguoiDung = nguoiDungRepository.findById(nguoiDungId).orElse(null);
        BenhNhan benhNhan = benhNhanRepository.findByNguoiDung_NguoiDungId(nguoiDungId);
        
        return new UserProfileDTO(nguoiDung, benhNhan);
    }
}
