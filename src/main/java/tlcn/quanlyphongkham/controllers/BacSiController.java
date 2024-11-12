package tlcn.quanlyphongkham.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tlcn.quanlyphongkham.dtos.HoSoBenhDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.DonThuoc;
import tlcn.quanlyphongkham.entities.DonThuocThuoc;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.Thuoc;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.DonThuocService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.ThuocService;

@Controller
public class BacSiController {
	@Autowired
	private ChuyenKhoaService chuyenKhoaService;
	@Autowired
	private BacSiService bacSiService;
	@Autowired
	private HoSoBenhService hoSoBenhService;
	@Autowired
	private ThuocService thuocService;
	@Autowired
	private DonThuocService donThuocService;
	@Autowired
	private LichKhamBenhService lichKhamBenhService;

	@GetMapping("/chuyenkhoa")
	public ResponseEntity<List<ChuyenKhoa>> getAllChuyenKhoa() {
		List<ChuyenKhoa> specialties = chuyenKhoaService.getAllChuyenKhoa();
		return ResponseEntity.ok(specialties);
	}

	@GetMapping("/bacsi/quanlytaodonthuoc")
	public String getAllPrescriptions(Model model) {
		List<DonThuoc> donThuocs = donThuocService.getAllDonThuoc();
		for (DonThuoc donThuoc : donThuocs) {
			donThuoc.calculateTongTien();
			donThuoc.setFormattedDate(
					donThuoc.getHoSoBenh().getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		}
		model.addAttribute("donThuocs", donThuocs);
		return "bacsi/taodonthuoc/quanlytaodonthuoc";
	}

	@GetMapping("/bacsi/taodonthuoc")
	public String showCreatePrescriptionPage(Model model) {
		List<Thuoc> drugs = thuocService.getAllThuoc(); // Giả sử bạn có một ThuocService để lấy tất cả các loại thuốc
		model.addAttribute("drugs", drugs);
		return "bacsi/taodonthuoc/taodonthuoc";
	}

	@GetMapping("/bacsi/searchBenhNhanByPhone")
	public ResponseEntity<List<BenhNhan>> searchBenhNhanByPhone(@RequestParam("phone") String phone) {
		List<BenhNhan> patients = hoSoBenhService.findPatientsByPhoneNumber(phone);
		return ResponseEntity.ok(patients);
	}

	@PostMapping("/bacsi/quanlytaodonthuoc")
	public String createPrescription(@RequestParam("chanDoan") String chanDoan,
			@RequestParam("drugIds") List<Long> drugIds, @RequestParam("lieu") List<String> lieu,
			@RequestParam("tanSuat") List<String> tanSuat, @RequestParam("benhNhanId") String benhNhanId, Model model) {
		String fixedBacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667";
		BacSi bacSi = bacSiService.findById(fixedBacSiId);

		BenhNhan benhNhan = hoSoBenhService.findPatientById(benhNhanId);
		if (benhNhan == null) {
			model.addAttribute("errorMessage", "Bệnh nhân không hợp lệ!");
			return "bacsi/taodonthuoc/taodonthuoc";
		}

		HoSoBenh hoSoBenh = new HoSoBenh();
		hoSoBenh.setHoSoId(UUID.randomUUID().toString());
		hoSoBenh.setChanDoan(chanDoan);
		hoSoBenh.setBenhNhan(benhNhan);
		hoSoBenh.setBacSi(bacSi);
		hoSoBenhService.save(hoSoBenh);

		DonThuoc donThuoc = new DonThuoc();
		donThuoc.setHoSoBenh(hoSoBenh);

		List<DonThuocThuoc> donThuocThuocs = new ArrayList<>();
		for (int i = 0; i < drugIds.size(); i++) {
			Long thuocId = drugIds.get(i);
			Thuoc thuoc = thuocService.findThuocById(thuocId);

			DonThuocThuoc donThuocThuoc = new DonThuocThuoc();
			donThuocThuoc.setDonThuoc(donThuoc);
			donThuocThuoc.setThuoc(thuoc);
			donThuocThuoc.setLieu(lieu.get(i));
			donThuocThuoc.setTanSuat(tanSuat.get(i));

			donThuocThuocs.add(donThuocThuoc);
		}
		donThuoc.setDonThuocThuocs(donThuocThuocs);
		donThuocService.save(donThuoc);

		model.addAttribute("successMessage", "Đơn thuốc đã được tạo thành công!");
		return "redirect:/bacsi/quanlytaodonthuoc";
	}

	@GetMapping("/bacsi/searchpatient")
	public String searchPatientById(@RequestParam("id") String patientId, Model model) {
		BenhNhan patient = hoSoBenhService.findPatientById(patientId);
		model.addAttribute("patient", patient);
		return "bacsi/timkiembenhnhan/timkiembenhnhan";
	}

	@GetMapping("/bacsi/searchDrugs")
	@ResponseBody
	public List<Thuoc> searchDrugs(@RequestParam("name") String name) {
		// Tìm kiếm thuốc trong dịch vụ (ví dụ từ cơ sở dữ liệu)
		return donThuocService.searchDrugs(name);
	}

	@GetMapping("/bacsi/lichsukham")
	public String getLichSuKham(@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, Model model) {

		String bacSiId = "84960eeb-3ac9-4712-8418-68ecf7eae667";

		List<HoSoBenhDTO> hoSoBenhList = new ArrayList<>();

		if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
			LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

			// Chuyển đổi LocalDate sang LocalDateTime
			LocalDateTime startDateTime = startLocalDate.atStartOfDay();
			LocalDateTime endDateTime = endLocalDate.atTime(23, 59, 59);

			hoSoBenhList = hoSoBenhService.getHoSoBenhWithDonThuocByDateRangeAndDoctor(startDateTime, endDateTime,
					bacSiId);
		}
		
		if (hoSoBenhList != null) {
			model.addAttribute("hoSoBenhList", hoSoBenhList);
		}
		
		return "bacsi/lichsukham/lichsukham";
	}

}
