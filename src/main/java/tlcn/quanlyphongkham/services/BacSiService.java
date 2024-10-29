package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.ChiTietBacSi;
import tlcn.quanlyphongkham.repositories.BacSiRepository;

import java.util.List;

@Service
public class BacSiService {

    @Autowired
    private BacSiRepository bacSiRepository;

    public List<BacSi> getAllDoctors() {
        return bacSiRepository.findAll();
    }
    
 
    
    public BacSi getDoctorById(String bacSiId) {
        return bacSiRepository.findById(bacSiId).orElse(null);
    }

    public void updateDoctor(String bacSiId, BacSi updatedDoctor, ChiTietBacSi updatedChiTiet) {
        BacSi existingDoctor = bacSiRepository.findById(bacSiId).orElse(null);
        if (existingDoctor != null) {
            // Cập nhật các trường thông tin cơ bản của bác sĩ nếu có thay đổi
            if (updatedDoctor.getTen() != null) {
                existingDoctor.setTen(updatedDoctor.getTen());
            }
            if (updatedDoctor.getDiaChi() != null) {
                existingDoctor.setDiaChi(updatedDoctor.getDiaChi());
            }
            if (updatedDoctor.getDienThoai() != null) {
                existingDoctor.setDienThoai(updatedDoctor.getDienThoai());
            }
            if (updatedDoctor.getChuyenKhoa() != null) {
                existingDoctor.setChuyenKhoa(updatedDoctor.getChuyenKhoa());
            }
            if (updatedDoctor.getGioiTinh() != null) {
                existingDoctor.setGioiTinh(updatedDoctor.getGioiTinh());
            }
            if (updatedDoctor.getNgaySinh() != null) {
                existingDoctor.setNgaySinh(updatedDoctor.getNgaySinh());
            }

           
            bacSiRepository.save(existingDoctor);
        }
    }

    public void updateDoctorDetails(String bacSiId, ChiTietBacSi updatedChiTiet) {
        BacSi existingDoctor = bacSiRepository.findById(bacSiId).orElse(null);
        if (existingDoctor != null) {
            ChiTietBacSi chiTiet = existingDoctor.getChiTietBacSi();
            if (chiTiet != null) {
                chiTiet.setBangCap(updatedChiTiet.getBangCap());
                chiTiet.setHoiNghiNuocNgoai(updatedChiTiet.getHoiNghiNuocNgoai());
                chiTiet.setChungChi(updatedChiTiet.getChungChi());
                chiTiet.setDaoTaoChuyenNganh(updatedChiTiet.getDaoTaoChuyenNganh());
                chiTiet.setGioiThieu(updatedChiTiet.getGioiThieu());
                bacSiRepository.save(existingDoctor); // Lưu lại cập nhật
            }
        }
    }

}
