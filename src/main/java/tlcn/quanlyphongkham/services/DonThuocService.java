package tlcn.quanlyphongkham.services;

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
	public Page<DonThuoc> getAllDonThuoc(Pageable pageable) {
	    return donThuocRepository.findAll(pageable); // No sorting here
	}
	
	public Page<DonThuoc> searchByBenhNhanName(String name, Pageable pageable) {
	    return donThuocRepository.searchByNameBenhNhan(name, pageable);
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
			List<String> lieu, List<String> tanSuat, List<Long> removedDrugIds) {
		// 1. Lấy đơn thuốc từ cơ sở dữ liệu
		DonThuoc donThuoc = donThuocRepository.findById(donThuocId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc với ID: " + donThuocId));

		// 2. Xử lý hồ sơ bệnh (Không thay đổi)
		HoSoBenh hoSoBenh = null;
		if (hoSoId != null) {
			hoSoBenh = hoSoBenhRepository.findById(hoSoId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh với ID: " + hoSoId));
			donThuoc.setHoSoBenh(hoSoBenh);
		}

		// 3. Cập nhật thông tin bệnh nhân (Không thay đổi)
		if (benhNhanId != null && hoSoBenh != null) {
			BenhNhan newBenhNhan = benhNhanRepository.findById(benhNhanId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy bệnh nhân với ID: " + benhNhanId));
			if (!newBenhNhan.getBenhNhanId().equals(hoSoBenh.getBenhNhan().getBenhNhanId())) {
				hoSoBenh.setBenhNhan(newBenhNhan);
			}
		}

		// 4. Cập nhật chẩn đoán (Không thay đổi)
		if (chanDoan != null && !chanDoan.isEmpty() && hoSoBenh != null) {
			hoSoBenh.setChanDoan(chanDoan);
		}
		System.out.println(removedDrugIds.size() + "hahahaha");
		// 5. Xóa các thuốc bị loại bỏ
		if (removedDrugIds != null && !removedDrugIds.isEmpty()) {
			List<DonThuocThuoc> drugsToRemove = donThuoc.getDonThuocThuocs().stream()
					.filter(dtt -> removedDrugIds.contains(dtt.getThuoc().getThuocId())).collect(Collectors.toList());

			// Xóa thuốc khỏi cơ sở dữ liệu
			donThuocThuocRepository.deleteAll(drugsToRemove);

			// Loại bỏ thuốc khỏi danh sách của DonThuoc
			donThuoc.getDonThuocThuocs().removeAll(drugsToRemove);
		}

		// 6. Cập nhật hoặc thêm thuốc mới
		if (drugIds != null && lieu != null && tanSuat != null) {
			if (drugIds.size() != lieu.size() || drugIds.size() != tanSuat.size()) {
				throw new RuntimeException("Danh sách thuốc, liều và tần suất phải có cùng kích thước!");
			}

			Map<Long, DonThuocThuoc> existingDrugMap = donThuoc.getDonThuocThuocs().stream()
					.collect(Collectors.toMap(dtt -> dtt.getThuoc().getThuocId(), dtt -> dtt));

			for (int i = 0; i < drugIds.size(); i++) {
				Long drugId = drugIds.get(i);
				String lieuThuoc = lieu.get(i);
				String tanSuatThuoc = tanSuat.get(i);

				Thuoc thuoc = thuocRepository.findById(drugId)
						.orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc với ID: " + drugId));

				DonThuocThuoc existingDrug = existingDrugMap.get(drugId);
				if (existingDrug != null) {
					// Cập nhật thuốc đã có
					existingDrug.setLieu(lieuThuoc);
					existingDrug.setTanSuat(tanSuatThuoc);
				} else {
					// Thêm thuốc mới
					DonThuocThuoc newDrug = new DonThuocThuoc();
					newDrug.setThuoc(thuoc);
					newDrug.setLieu(lieuThuoc);
					newDrug.setTanSuat(tanSuatThuoc);
					newDrug.setDonThuoc(donThuoc);
					donThuoc.getDonThuocThuocs().add(newDrug);
				}
			}
		}

		// 7. Lưu hồ sơ bệnh nếu có thay đổi (Không thay đổi)
		if (hoSoBenh != null) {
			hoSoBenhRepository.save(hoSoBenh);
		}

		// 8. Lưu đơn thuốc
		donThuocRepository.save(donThuoc);
	}

	

}
