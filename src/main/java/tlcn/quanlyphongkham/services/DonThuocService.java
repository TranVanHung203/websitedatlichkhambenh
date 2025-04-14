package tlcn.quanlyphongkham.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.DonThuoc;
import tlcn.quanlyphongkham.entities.DonThuocThuoc;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.DonThuocRepository;
import tlcn.quanlyphongkham.repositories.DonThuocThuocRepository;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.ThuocRepository;

@Service
@RequiredArgsConstructor

public class DonThuocService {
	@Autowired
	private DonThuocRepository donThuocRepository;

	@Autowired
	private HoSoBenhRepository hoSoBenhRepository;

	@Autowired
	private BenhNhanRepository benhNhanRepository;

	@Autowired
	private DonThuocThuocRepository donThuocThuocRepository;

	@Autowired
	private ThuocRepository thuocRepository;

	public void save(DonThuoc donThuoc) {
		donThuocRepository.save(donThuoc);
	}

	public List<DonThuoc> getAllDonThuoc() {
		return donThuocRepository.findAll();
	}
//	public Page<DonThuoc> getAllDonThuoc(Pageable pageable) {
//	    return donThuocRepository.findAll(pageable); // No sorting here
//	}
//	
//	public Page<DonThuoc> searchByBenhNhanName(String name, Pageable pageable) {
//	    return donThuocRepository.searchByNameBenhNhan(name, pageable);
//	}
	  // Phương thức để lấy đơn thuốc theo bác sĩ và tên bệnh nhân
    public Page<DonThuoc> findByBacSiIdAndBenhNhanNameContaining(String bacSiId, String name, Pageable pageable) {
        return donThuocRepository.findByBacSiIdAndBenhNhanNameContaining(bacSiId, name, pageable);
    }

    // Phương thức để lấy tất cả đơn thuốc của bác sĩ
    public Page<DonThuoc> getAllDonThuocByBacSiId(String bacSiId, Pageable pageable) {
        return donThuocRepository.findAllByBacSiId(bacSiId, pageable);
    }

	public List<Thuoc> searchDrugs(String query) {
		return thuocRepository.findByTenContaining(query);
	}

	public void deleteDonThuocAndHoSoBenhById(Long donThuocId) {
		DonThuoc donThuoc = donThuocRepository.findById(donThuocId)
				.orElseThrow(() -> new IllegalArgumentException("Đơn thuốc không tồn tại với ID: " + donThuocId));

		// Lấy ID của HoSoBenh liên quan
		String hoSoBenhId = donThuoc.getHoSoBenh().getHoSoId();

		// Xóa đơn thuốc
		donThuocRepository.deleteById(donThuocId);

		// Kiểm tra và xóa HoSoBenh (nếu không có đơn thuốc khác liên kết)
		if (!donThuocRepository.existsByHoSoBenh_HoSoId(hoSoBenhId)) {
			hoSoBenhRepository.deleteById(hoSoBenhId);
		}
	}

	public DonThuoc findById(Long donThuocId) {
		return donThuocRepository.findById(donThuocId).orElse(null);
	}

	@Transactional
	public void updateDonThuoc(Long donThuocId, String hoSoId, String chanDoan, String benhNhanId, List<Long> drugIds,
	                           List<String> lieu, List<String> tanSuat, List<Integer> soLuong, List<Long> removedDrugIds,String trieuChung) {
	    // 1. Lấy đơn thuốc từ cơ sở dữ liệu
	    DonThuoc donThuoc = donThuocRepository.findById(donThuocId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc với ID: " + donThuocId));

	    // 2. Xử lý hồ sơ bệnh
	    HoSoBenh hoSoBenh = null;
	    if (hoSoId != null) {
	        hoSoBenh = hoSoBenhRepository.findById(hoSoId)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh với ID: " + hoSoId));
	        donThuoc.setHoSoBenh(hoSoBenh);
	    }

	    // 3. Cập nhật thông tin bệnh nhân
	    if (benhNhanId != null && hoSoBenh != null) {
	        BenhNhan newBenhNhan = benhNhanRepository.findById(benhNhanId)
	                .orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với ID: " + benhNhanId));
	        if (!newBenhNhan.getBenhNhanId().equals(hoSoBenh.getBenhNhan().getBenhNhanId())) {
	            hoSoBenh.setBenhNhan(newBenhNhan);
	        }
	    }

	    // 4. Cập nhật chẩn đoán
	    if (chanDoan != null && !chanDoan.isEmpty() && hoSoBenh != null) {
	        hoSoBenh.setChanDoan(chanDoan);
	    }

	    if (trieuChung != null && !trieuChung.isEmpty() && hoSoBenh != null) {
	        hoSoBenh.setTrieuChung(trieuChung);
	    }
	    // 5. Xử lý các thuốc bị loại bỏ
	    if (removedDrugIds != null && !removedDrugIds.isEmpty()) {
	        List<DonThuocThuoc> drugsToRemove = donThuoc.getDonThuocThuocs().stream()
	                .filter(dtt -> removedDrugIds.contains(dtt.getThuoc().getThuocId())).collect(Collectors.toList());

	        for (DonThuocThuoc dtt : drugsToRemove) {
	            Thuoc thuoc = dtt.getThuoc();
	            thuoc.setSoLuong(thuoc.getSoLuong() + dtt.getSoLuong()); // Cộng lại số lượng khi xóa thuốc
	            thuocRepository.save(thuoc); // Lưu lại số lượng trong kho
	        }

	        // Xóa thuốc khỏi cơ sở dữ liệu và danh sách
	        donThuocThuocRepository.deleteAll(drugsToRemove);
	        donThuoc.getDonThuocThuocs().removeAll(drugsToRemove);
	    }

	    // 6. Xử lý thêm hoặc cập nhật thuốc mới
	    if (drugIds != null && lieu != null && tanSuat != null && soLuong != null) {
	        if (drugIds.size() != lieu.size() || drugIds.size() != tanSuat.size() || drugIds.size() != soLuong.size()) {
	            throw new RuntimeException("Danh sách thuốc, liều, tần suất và số lượng phải có cùng kích thước!");
	        }

	        Map<Long, DonThuocThuoc> existingDrugMap = donThuoc.getDonThuocThuocs().stream()
	                .collect(Collectors.toMap(dtt -> dtt.getThuoc().getThuocId(), dtt -> dtt));

	        for (int i = 0; i < drugIds.size(); i++) {
	            Long drugId = drugIds.get(i);
	            String lieuThuoc = lieu.get(i);
	            String tanSuatThuoc = tanSuat.get(i);
	            Integer quantity = soLuong.get(i);  // Lấy số lượng thuốc

	            Thuoc thuoc = thuocRepository.findById(drugId)
	                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + drugId));

	            if (thuoc.getSoLuong() < quantity) {
	                throw new RuntimeException("Thuốc " + thuoc.getTen() + " không đủ số lượng trong kho.");
	            }

	            DonThuocThuoc existingDrug = existingDrugMap.get(drugId);
	            if (existingDrug != null) {
	                // Cập nhật thuốc đã có
	                int oldQuantity = existingDrug.getSoLuong();
	                existingDrug.setLieu(lieuThuoc);
	                existingDrug.setTanSuat(tanSuatThuoc);
	                existingDrug.setSoLuong(quantity);

	                // Điều chỉnh số lượng trong kho
	                thuoc.setSoLuong(thuoc.getSoLuong() + oldQuantity - quantity);
	            } else {
	                // Thêm thuốc mới
	                DonThuocThuoc newDrug = new DonThuocThuoc();
	                newDrug.setThuoc(thuoc);
	                newDrug.setLieu(lieuThuoc);
	                newDrug.setTanSuat(tanSuatThuoc);
	                newDrug.setDonThuoc(donThuoc);
	                newDrug.setSoLuong(quantity);
	                donThuoc.getDonThuocThuocs().add(newDrug);

	                // Trừ số lượng trong kho
	                thuoc.setSoLuong(thuoc.getSoLuong() - quantity);
	            }
	            thuocRepository.save(thuoc); // Lưu lại thay đổi số lượng trong kho
	        }
	    }

	    // 7. Lưu hồ sơ bệnh nếu có thay đổi
	    if (hoSoBenh != null) {
	        hoSoBenhRepository.save(hoSoBenh);
	    }

	    // 8. Lưu đơn thuốc
	    donThuocRepository.save(donThuoc);
	}

	public Page<DonThuoc> findByBenhNhanId(String benhNhanId, Pageable pageable) {
		// TODO Auto-generated method stub
		return donThuocRepository.findByBenhNhanId(benhNhanId, pageable);
	}

	public Page<DonThuoc> findByBenhNhanIdAndDate(String benhNhanId, LocalDate filterDate, Pageable pageable) {
		// TODO Auto-generated method stub
		return donThuocRepository.findByBenhNhanIdAndDate(benhNhanId, filterDate, pageable);
	}


	

}
