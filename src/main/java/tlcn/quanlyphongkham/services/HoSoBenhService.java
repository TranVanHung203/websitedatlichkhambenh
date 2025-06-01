package tlcn.quanlyphongkham.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.dtos.BenhNhanOfTaoDonThuocDTO;
import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamDTO;
import tlcn.quanlyphongkham.dtos.LichSuKhamNhanVienDTO;
import tlcn.quanlyphongkham.dtos.PaymentDetailsDTO;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.DonThuocRepository;
import tlcn.quanlyphongkham.repositories.DonThuocThuocRepository;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.ThuocRepository;

@Service
public class HoSoBenhService {
	@Autowired
	private HoSoBenhRepository hoSoBenhRepository;
	@Autowired
	private DonThuocRepository donThuocRepository;
	@Autowired
	private ThuocRepository thuocRepository;
	@Autowired
	private DonThuocThuocRepository donThuocThuocRepository;

	@Autowired
	private BenhNhanRepository benhNhanRepository;

	public BenhNhan findPatientById(String patientId) {
		return benhNhanRepository.findById(patientId).orElse(null);
	}

//    public HoSoBenh findHoSoById(String hoSoId) {
//        return hoSoBenhRepository.findById(hoSoId).orElse(null);
//    }
	public HoSoBenh save(HoSoBenh hoSoBenh) {
		return hoSoBenhRepository.save(hoSoBenh);
	}

	// Tìm hồ sơ bệnh theo ID
	public HoSoBenh findById(String hoSoId) {
		return hoSoBenhRepository.findById(hoSoId).orElse(null);
	}

	public List<BenhNhanOfTaoDonThuocDTO> getBenhNhanInfoByDienThoai(String dienThoai) {
		List<Object[]> results = benhNhanRepository.findBenhNhanInfoByDienThoai(dienThoai);
		return results.stream().map(obj -> new BenhNhanOfTaoDonThuocDTO((String) obj[0], // benhnhanid
				(String) obj[1], // ten
				((java.sql.Date) obj[2]).toLocalDate(), // Chuyển đổi sang LocalDate
				(String) obj[3] // gioitinh
		)).collect(Collectors.toList());
	}

	public Page<HoSoBenhDTO> getHoSoBenhWithDonThuocByDateRangeAndDoctor(LocalDateTime startDateTime,
			LocalDateTime endDateTime, String bacSiId, Pageable pageable) {

		// Lấy dữ liệu thô từ repository
		Page<Object[]> rawResults = hoSoBenhRepository.findHoSoBenhWithGroupedThuoc(startDateTime, endDateTime, bacSiId,
				pageable);

		// Chuyển đổi Object[] sang HoSoBenhDTO
		return rawResults.map(obj -> {
			String hoSoId = (String) obj[0];
			String tenBenhNhan = (String) obj[1];
			String chanDoan = (String) obj[2];
			String thoiGianTao = (String) obj[3];
			String tenThuoc = (String) obj[4];
			String lieu = (String) obj[5];
			String tanSuat = (String) obj[6];
			BigDecimal tongTienThuoc = (BigDecimal) obj[7];

			// Thêm thông tin số lượng vào DTO (Chuyển sang kiểu int)
			String soLuong = (String) obj[8]; // Thay đổi từ int sang String

			return new HoSoBenhDTO(hoSoId, tenBenhNhan, chanDoan, thoiGianTao, tenThuoc, lieu, tanSuat, tongTienThuoc,
					soLuong);
		});
	}

	public Page<LichSuKhamDTO> getLichSuKhamByBenhNhanId(String benhNhanId, Pageable pageable) {
		Page<Object[]> rawData = hoSoBenhRepository.findLichSuKhamByBenhNhanIdRaw(benhNhanId, pageable);

		return rawData.map(obj -> new LichSuKhamDTO((String) obj[0], // tenBacSi
				(String) obj[1], // ngayKham
				(String) obj[2], // chanDoan
				(String) obj[3], // thuoc
				(String) obj[4], // lieu
				(String) obj[5], // tanSuat
				(String) obj[6], (BigDecimal) obj[7], // tongTienThuoc
				(String) obj[8] // soLuong
		));
	}

	public Page<LichSuKhamDTO> getLichSuKhamByBenhNhanIdAndDate(String benhNhanId, String date, Pageable pageable) {
		Page<Object[]> rawData = hoSoBenhRepository.findLichSuKhamByBenhNhanIdAndDateRaw(benhNhanId, date, pageable);

		return rawData.map(obj -> new LichSuKhamDTO((String) obj[0], // tenBacSi
				(String) obj[1], // ngayKham
				(String) obj[2], // chanDoan
				(String) obj[3], // thuoc
				(String) obj[4], // lieu
				(String) obj[5], // tanSuat
				(String) obj[6], (BigDecimal) obj[7], // tongTienThuoc
				(String) obj[8] // soLuong
		));
	}

	public List<HoSoBenh> findByBenhNhanId(String benhNhanId) {
		// TODO Auto-generated method stub
		List<HoSoBenh> medicalHistory = hoSoBenhRepository.findByBenhNhanId(benhNhanId);
		return medicalHistory;
	}

	public PaymentDetailsDTO getPaymentDetailsBySlotId(String slotId) {
		List<Object[]> rawResults = hoSoBenhRepository.findPaymentDetailsBySlotId(slotId);
		if (rawResults.isEmpty()) {
			return null;
		}

		Object[] obj = rawResults.get(0);
		PaymentDetailsDTO dto = new PaymentDetailsDTO();
		dto.setHoSoId((String) obj[0]);
		dto.setTenBenhNhan((String) obj[1]);
		dto.setTenBacSi((String) obj[2]);
		dto.setTongTien(obj[3] != null ? new BigDecimal(obj[3].toString()) : BigDecimal.ZERO);
		dto.setChanDoan((String) obj[4]);
		dto.setTrieuChung((String) obj[5]);
		dto.setDaThanhToan((Boolean) obj[6]); // Thêm cột này

		// Xử lý donThuocIds
		String donThuocIdsStr = (String) obj[7];
		if (donThuocIdsStr != null && !donThuocIdsStr.isEmpty()) {
			dto.setDonThuocIds(Arrays.asList(donThuocIdsStr.split(",")));
		}

		// Xử lý thuocList
		String thuocListStr = (String) obj[8];
		if (thuocListStr != null && !thuocListStr.isEmpty()) {
			Arrays.stream(thuocListStr.split("\\|")).forEach(thuocStr -> {
				String[] parts = thuocStr.split(":");
				if (parts.length == 6) {
					PaymentDetailsDTO.Thuoc thuoc = new PaymentDetailsDTO.Thuoc();
					thuoc.setDonThuocId(parts[0]);
					thuoc.setTenThuoc(parts[1]);
					thuoc.setSoLuong(Integer.parseInt(parts[2]));
					thuoc.setGia(new BigDecimal(parts[3]));
					thuoc.setLieu(parts[4]);
					thuoc.setTanSuat(parts[5]);
					dto.getThuocList().add(thuoc);
				}
			});
		}

		// Xử lý xetNghiemIds
		String xetNghiemIdsStr = (String) obj[9];
		if (xetNghiemIdsStr != null && !xetNghiemIdsStr.isEmpty()) {
			dto.setXetNghiemIds(Arrays.asList(xetNghiemIdsStr.split(",")));
		}

		// Xử lý xetNghiemList
		String xetNghiemListStr = (String) obj[10];
		if (xetNghiemListStr != null && !xetNghiemListStr.isEmpty()) {
			Arrays.stream(xetNghiemListStr.split("\\|")).forEach(xetNghiemStr -> {
				String[] parts = xetNghiemStr.split(":");
				if (parts.length == 3) {
					PaymentDetailsDTO.XetNghiem xetNghiem = new PaymentDetailsDTO.XetNghiem();
					xetNghiem.setXetNghiemId(parts[0]);
					xetNghiem.setTenXetNghiem(parts[1]);
					xetNghiem.setGia(Integer.parseInt(parts[2]));
					dto.getXetNghiemList().add(xetNghiem);
				}
			});
		}

		return dto;
	}
	public Page<HoSoBenh> findMedicalHistoryWithFilters(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String patientName,
            String doctorName,
            String phoneNumber,
            Pageable pageable) {
        return hoSoBenhRepository.findMedicalHistoryWithFilters(
                startDate, endDate, patientName, doctorName, phoneNumber, pageable);
    }
	
}
