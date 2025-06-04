package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.PhieuXetNghiem;
import tlcn.quanlyphongkham.entities.XetNghiem;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.PhieuXetNghiemRepository;

import java.util.List;

@Service
public class PhieuXetNghiemService {

    @Autowired
    private PhieuXetNghiemRepository phieuXetNghiemRepository;

    @Autowired
    private XetNghiemService xetNghiemService;

    @Autowired
    private HoSoBenhRepository hoSoBenhRepository;

    public PhieuXetNghiem createPhieuXetNghiem(String hoSoId, List<Long> xetNghiemIds) {
        HoSoBenh hoSoBenh = hoSoBenhRepository.findById(hoSoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh: " + hoSoId));

        PhieuXetNghiem phieu = new PhieuXetNghiem();
        phieu.setHoSoBenh(hoSoBenh);
        phieu.setXetNghiemIds(xetNghiemIds);

        // Tính tổng giá
        int tongGia = 0;
        List<XetNghiem> xetNghiems = xetNghiemService.getXetNghiemByHoSoId(hoSoId);
        for (XetNghiem xn : xetNghiems) {
            if (xetNghiemIds.contains(xn.getId())) {
                tongGia += xn.getLoaiXetNghiem().getGia();
            }
        }
        phieu.setTongGia(tongGia);

        // Cập nhật lại tổng tiền trong HoSoBenh
        hoSoBenh.setTongTien(tongGia);
        hoSoBenhRepository.save(hoSoBenh);

        return phieuXetNghiemRepository.save(phieu);
    }
}