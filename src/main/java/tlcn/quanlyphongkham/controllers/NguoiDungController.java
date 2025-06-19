
package tlcn.quanlyphongkham.controllers;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import tlcn.quanlyphongkham.dtos.BacSiDTO;
import tlcn.quanlyphongkham.dtos.BenhNhanDTO;
import tlcn.quanlyphongkham.dtos.MaLichKhamBenhDTO;
import tlcn.quanlyphongkham.dtos.MedicalHistoryDTO;
import tlcn.quanlyphongkham.dtos.TaiKhoanProfileDTO;
import tlcn.quanlyphongkham.dtos.UserProfileDTO;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.security.CustomUserDetails;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.ChuyenKhoaService;
import tlcn.quanlyphongkham.services.HoSoBenhService;
import tlcn.quanlyphongkham.services.LichKhamBenhService;
import tlcn.quanlyphongkham.services.LichSuDatLichService;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.SlotThoiGianService;
import tlcn.quanlyphongkham.services.UserProfileService;

@Controller
public class NguoiDungController {

	@Autowired
	private BenhNhanService benhNhanService;

	@Autowired
	private NguoiDungService nguoiDungService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private ChuyenKhoaService chuyenKhoaService;

	@Autowired
	private BacSiService bacSiService;

	@Autowired
	private HoSoBenhService hoSoBenhService;

	@Autowired
	private LichKhamBenhService lichKhamBenhService;
	@Autowired
	private SlotThoiGianService slotThoiGianService;

	@Autowired
	private LichSuDatLichService lichSuDatLichService;

	public String getNguoiDungId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = (CustomUserDetails) principal;
				return customUserDetails.getNguoiDungId(); // Get the NguoiDungId as String

			} else
				return null;
		} else
			return null;
	}

	String nguoiDungId;

	@GetMapping("/user/editprofile")
	public String editProfile(Model model) {
		nguoiDungId = getNguoiDungId();
		UserProfileDTO userProfile = userProfileService.getUserProfileByNguoiDungId(nguoiDungId);
		model.addAttribute("nguoiDung", userProfile.getNguoiDung());
		model.addAttribute("benhNhan", userProfile.getBenhNhan());

		// Tìm bệnh nhân theo nguoiDungId thay vì gán cứng benhNhanId
		BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);
		return "benhnhan/editprofile/editprofile";
	}


	
	
	@GetMapping("/user/lichsukhambenh")
    public String getMedicalHistory(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        try {
            String nguoiDungId = getNguoiDungId();
            BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);
            String benhNhanId = benhNhan.getBenhNhanId();

            if (benhNhanId == null || benhNhanId.trim().isEmpty()) {
                model.addAttribute("error", "Mã bệnh nhân không hợp lệ");
                return "benhnhan/lichsukhambenh/lichsukhambenh";
            }

            // Add sorting by thoi_gian_tao in descending order
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "thoi_gian_tao"));

            // Convert LocalDate to LocalDateTime for filtering
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59, 999999999) : null;

            // Fetch medical history with optional date range filtering
            Page<HoSoBenh> medicalHistoryPage = hoSoBenhService.findByBenhNhanIdAndDateRange(benhNhanId, startDateTime, endDateTime, pageable);

            // Define formatter for dd/MM/yyyy HH:mm
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Page<MedicalHistoryDTO> dtos = medicalHistoryPage.map(hsb -> {
                Hibernate.initialize(hsb.getBenhNhan());
                Hibernate.initialize(hsb.getBacSi());
                Hibernate.initialize(hsb.getDonThuocs());
                Hibernate.initialize(hsb.getXetNghiems());
                Hibernate.initialize(hsb.getPhieuXetNghiems());
                Hibernate.initialize(hsb.getVitalSigns());

                MedicalHistoryDTO dto = new MedicalHistoryDTO();
                dto.setHoSoId(hsb.getHoSoId());

                if (hsb.getBenhNhan() != null) {
                    MedicalHistoryDTO.BenhNhanDTO benhNhanDTO = new MedicalHistoryDTO.BenhNhanDTO(
                        hsb.getBenhNhan().getBenhNhanId(),
                        hsb.getBenhNhan().getTen(),
                        hsb.getBenhNhan().getDienThoai()
                    );
                    dto.setBenhNhan(benhNhanDTO);
                }

                if (hsb.getBacSi() != null) {
                    MedicalHistoryDTO.BacSiDTO bacSiDTO = new MedicalHistoryDTO.BacSiDTO(
                        hsb.getBacSi().getBacSiId(),
                        hsb.getBacSi().getTen()
                    );
                    dto.setBacSi(bacSiDTO);
                }

                dto.setChanDoan(hsb.getChanDoan());
                dto.setTrieuChung(hsb.getTrieuChung());
                dto.setTongTien(hsb.getTongTien());
                dto.setDaThanhToan(hsb.getDaThanhToan());
                // Format thoiGianTao to dd/MM/yyyy HH:mm
                dto.setThoiGianTao(hsb.getThoiGianTao() != null ? hsb.getThoiGianTao().format(formatter) : null);

                if (hsb.getVitalSigns() != null && !hsb.getVitalSigns().isEmpty()) {
                    List<MedicalHistoryDTO.VitalSignsDTO> vitalSignsDTOs = hsb.getVitalSigns().stream().map(vs -> {
                        return new MedicalHistoryDTO.VitalSignsDTO(
                            vs.getId(),
                            vs.getTemperature(),
                            vs.getHeight(),
                            vs.getWeight(),
                            vs.getBloodPressureSys(),
                            vs.getBloodPressureDia(),
                            vs.getNotes(),
                            vs.getThoiGianTao() != null ? vs.getThoiGianTao().format(formatter) : null
                        );
                    }).collect(Collectors.toList());
                    dto.setVitalSigns(vitalSignsDTOs);
                }

                if (hsb.getDonThuocs() != null && !hsb.getDonThuocs().isEmpty()) {
                    List<MedicalHistoryDTO.DonThuocDTO> donThuocDTOs = hsb.getDonThuocs().stream().map(dt -> {
                        Hibernate.initialize(dt.getDonThuocThuocs());
                        List<MedicalHistoryDTO.DonThuocThuocDTO> donThuocThuocDTOs = dt.getDonThuocThuocs() != null ?
                            dt.getDonThuocThuocs().stream().map(dtt -> {
                                MedicalHistoryDTO.ThuocDTO thuocDTO = new MedicalHistoryDTO.ThuocDTO(
                                    dtt.getThuoc().getTen(),
                                    dtt.getThuoc().getGia()
                                );
                                return new MedicalHistoryDTO.DonThuocThuocDTO(
                                    thuocDTO,
                                    dtt.getLieu(),
                                    dtt.getTanSuat(),
                                    dtt.getSoLuong()
                                );
                            }).collect(Collectors.toList()) : Collections.emptyList();
                        MedicalHistoryDTO.DonThuocDTO donThuocDTO = new MedicalHistoryDTO.DonThuocDTO(
                            dt.getDonThuocId(),
                            dt.getFormattedTongTienThuoc(),
                            donThuocThuocDTOs
                        );
                        return donThuocDTO;
                    }).collect(Collectors.toList());
                    dto.setDonThuocs(donThuocDTOs);
                }

                if (hsb.getXetNghiems() != null && !hsb.getXetNghiems().isEmpty()) {
                    List<MedicalHistoryDTO.XetNghiemDTO> xetNghiemDTOs = hsb.getXetNghiems().stream().map(xn -> {
                        MedicalHistoryDTO.LoaiXetNghiemDTO loaiXetNghiemDTO = new MedicalHistoryDTO.LoaiXetNghiemDTO(
                            xn.getLoaiXetNghiem().getTenXetNghiem(),
                            xn.getLoaiXetNghiem().getGia()
                        );
                        return new MedicalHistoryDTO.XetNghiemDTO(
                            loaiXetNghiemDTO,
                            xn.getGhiChu(),
                            xn.getTrangThai(),
                            xn.getFileKetQua(),
                            xn.getThoiGianTao() != null ? xn.getThoiGianTao().format(formatter) : null
                        );
                    }).collect(Collectors.toList());
                    dto.setXetNghiems(xetNghiemDTOs);
                }

                if (hsb.getPhieuXetNghiems() != null && !hsb.getPhieuXetNghiems().isEmpty()) {
                    List<MedicalHistoryDTO.PhieuXetNghiemDTO> phieuXetNghiemDTOs = hsb.getPhieuXetNghiems().stream().map(pxn -> {
                        return new MedicalHistoryDTO.PhieuXetNghiemDTO(
                            pxn.getMaPhieu(),
                            pxn.getTongGia(),
                            pxn.getThoiGianTao() != null ? pxn.getThoiGianTao().format(formatter) : null,
                            pxn.getXetNghiemIds()
                        );
                    }).collect(Collectors.toList());
                    dto.setPhieuXetNghiems(phieuXetNghiemDTOs);
                }

                return dto;
            });

            // Add data to model for the view
            model.addAttribute("medicalHistory", dtos.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", medicalHistoryPage.getTotalPages());
            model.addAttribute("benhNhanId", benhNhanId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);

            return "benhnhan/lichsukhambenh/lichsukhambenh";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể lấy lịch sử khám bệnh: " + e.getMessage());
            return "benhnhan/lichsukhambenh/lichsukhambenh";
        }
    }

	@GetMapping("/user/lichsukhambenh/download-pdf")
	public ResponseEntity<?> downloadMedicalHistoryPdf(
	        @RequestParam("benhNhanId") String benhNhanId,
	        @RequestParam(value = "hoSoIds", required = false) List<String> hoSoIds,
	        @RequestParam(value = "startDate", required = false) String startDateStr,
	        @RequestParam(value = "endDate", required = false) String endDateStr) {
	    try {
	        if (benhNhanId == null || benhNhanId.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Mã bệnh nhân không hợp lệ"));
	        }

	        // Parse dates, ignoring "undefined" or invalid values
	        LocalDate startDate = null;
	        LocalDate endDate = null;
	        try {
	            if (startDateStr != null && !startDateStr.equals("undefined") && !startDateStr.isEmpty()) {
	                startDate = LocalDate.parse(startDateStr);
	            }
	            if (endDateStr != null && !endDateStr.equals("undefined") && !endDateStr.isEmpty()) {
	                endDate = LocalDate.parse(endDateStr);
	            }
	        } catch (DateTimeParseException e) {
	            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Định dạng ngày không hợp lệ"));
	        }

	        // Convert LocalDate to LocalDateTime for filtering
	        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
	        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59, 999999999) : null;

	        // Fetch records based on hoSoIds or all records if hoSoIds is empty
	        List<HoSoBenh> medicalHistory;
	        if (hoSoIds != null && !hoSoIds.isEmpty()) {
	            medicalHistory = hoSoBenhService.findByIdsAndBenhNhanIdAndDateRange(hoSoIds, benhNhanId, startDateTime, endDateTime);
	        } else {
	            medicalHistory = hoSoBenhService.findByBenhNhanIdAndDateRange(benhNhanId, startDateTime, endDateTime, null).getContent();
	        }

	        if (medicalHistory.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Collections.singletonMap("error", "Không tìm thấy lịch sử khám bệnh cho bệnh nhân này"));
	        }

	        // Initialize PDF document
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);

	        // Load a font that supports Vietnamese
	        PdfFont font;
	        try {
	            // Try loading BeVietnamPro from resources
	            font = PdfFontFactory.createFont("/fonts/BeVietnamPro-Regular.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	        } catch (Exception e1) {
	            try {
	                // Fallback to Arial (system font)
	                font = PdfFontFactory.createFont("C:/Windows/Fonts/arial.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	            } catch (Exception e2) {
	                try {
	                    // Fallback to Times New Roman
	                    font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	                } catch (Exception e3) {
	                    // Final fallback to iText's built-in Helvetica
	                    font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
	                }
	            }
	        }
	        document.setFont(font);

	        // Adding title
	        document.add(new Paragraph("Lịch Sử Khám Bệnh")
	            .setFontSize(18)
	            .setBold()
	            .setTextAlignment(TextAlignment.CENTER));

	        // Formatter for dates
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	        for (HoSoBenh hsb : medicalHistory) {
	            Hibernate.initialize(hsb.getBenhNhan());
	            Hibernate.initialize(hsb.getBacSi());
	            Hibernate.initialize(hsb.getDonThuocs());
	            Hibernate.initialize(hsb.getXetNghiems());
	            Hibernate.initialize(hsb.getVitalSigns());

	            // General info
	            document.add(new Paragraph("Mã hồ sơ: " + hsb.getHoSoId()).setBold());
	            document.add(new Paragraph("Khám ngày: " + (hsb.getThoiGianTao() != null ? hsb.getThoiGianTao().format(formatter) : "Không có")));
	            document.add(new Paragraph("Bệnh nhân: " + (hsb.getBenhNhan() != null ? hsb.getBenhNhan().getTen() + " (" + hsb.getBenhNhan().getDienThoai() + ")" : "Không có")));
	            document.add(new Paragraph("Bác sĩ: " + (hsb.getBacSi() != null ? hsb.getBacSi().getTen() : "Chưa chỉ định")));
	            document.add(new Paragraph("Chẩn đoán: " + (hsb.getChanDoan() != null ? hsb.getChanDoan() : "Chưa có")));
	            document.add(new Paragraph("Triệu chứng: " + (hsb.getTrieuChung() != null ? hsb.getTrieuChung() : "Chưa có")));
	            document.add(new Paragraph("Tổng tiền: " + (hsb.getTongTien() != null ? String.format("%,d VNĐ", hsb.getTongTien().longValue()) : "Chưa tính")));
	            document.add(new Paragraph("Đã thanh toán: " + (hsb.getDaThanhToan() != null && hsb.getDaThanhToan() ? "Có" : "Chưa")));

	            // Vital Signs
	            document.add(new Paragraph("Dấu hiệu sinh tồn").setBold());
	            if (hsb.getVitalSigns() != null && !hsb.getVitalSigns().isEmpty()) {
	                Table table = new Table(6);
	                table.addCell(new Paragraph("Thời gian").setFont(font));
	                table.addCell(new Paragraph("Nhiệt độ").setFont(font));
	                table.addCell(new Paragraph("Chiều cao").setFont(font));
	                table.addCell(new Paragraph("Cân nặng").setFont(font));
	                table.addCell(new Paragraph("Huyết áp").setFont(font));
	                table.addCell(new Paragraph("Ghi chú").setFont(font));
	                for (var vs : hsb.getVitalSigns()) {
	                    table.addCell(new Paragraph(vs.getThoiGianTao() != null ? vs.getThoiGianTao().format(formatter) : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getTemperature() != null ? vs.getTemperature() + " °C" : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getHeight() != null ? vs.getHeight() + " cm" : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getWeight() != null ? vs.getWeight() + " kg" : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getBloodPressureSys() != null && vs.getBloodPressureDia() != null ? vs.getBloodPressureSys() + "/" + vs.getBloodPressureDia() + " mmHg" : "Không có").setFont(font));
	                    table.addCell(new Paragraph(vs.getNotes() != null ? vs.getNotes() : "Không có").setFont(font));
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            // Prescriptions
	            document.add(new Paragraph("Đơn thuốc").setBold());
	            if (hsb.getDonThuocs() != null && !hsb.getDonThuocs().isEmpty()) {
	                Table table = new Table(5);
	                table.addCell(new Paragraph("Đơn thuốc").setFont(font));
	                table.addCell(new Paragraph("Thuốc").setFont(font));
	                table.addCell(new Paragraph("Liều").setFont(font));
	                table.addCell(new Paragraph("Tần suất").setFont(font));
	                table.addCell(new Paragraph("Số lượng").setFont(font));
	                for (var dt : hsb.getDonThuocs()) {
	                    Hibernate.initialize(dt.getDonThuocThuocs());
	                    if (dt.getDonThuocThuocs() != null && !dt.getDonThuocThuocs().isEmpty()) {
	                        for (var dtt : dt.getDonThuocThuocs()) {
	                            table.addCell(new Paragraph("#" + dt.getDonThuocId()).setFont(font));
	                            table.addCell(new Paragraph(dtt.getThuoc() != null ? dtt.getThuoc().getTen() : "Không có").setFont(font));
	                            table.addCell(new Paragraph(dtt.getLieu() != null ? dtt.getLieu() : "Không có").setFont(font));
	                            table.addCell(new Paragraph(dtt.getTanSuat() != null ? dtt.getTanSuat() : "Không có").setFont(font));
	                            table.addCell(new Paragraph(dtt.getSoLuong() != 0 ? String.valueOf(dtt.getSoLuong()) : "Không có").setFont(font));
	                        }
	                    } else {
	                        table.addCell(new Paragraph("#" + dt.getDonThuocId()).setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                        table.addCell(new Paragraph("Không có").setFont(font));
	                    }
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            // Tests
	            document.add(new Paragraph("Xét nghiệm").setBold());
	            if (hsb.getXetNghiems() != null && !hsb.getXetNghiems().isEmpty()) {
	                Table table = new Table(4);
	                table.addCell(new Paragraph("Loại xét nghiệm").setFont(font));
	                table.addCell(new Paragraph("Giá").setFont(font));
	                table.addCell(new Paragraph("Trạng thái").setFont(font));
	                table.addCell(new Paragraph("Ghi chú").setFont(font));
	                for (var xn : hsb.getXetNghiems()) {
	                    table.addCell(new Paragraph(xn.getLoaiXetNghiem() != null ? xn.getLoaiXetNghiem().getTenXetNghiem() : "Không có").setFont(font));
	                    table.addCell(new Paragraph(xn.getLoaiXetNghiem() != null && xn.getLoaiXetNghiem().getGia() != null ? String.format("%,d VNĐ", xn.getLoaiXetNghiem().getGia().longValue()) : "Không có").setFont(font));
	                    table.addCell(new Paragraph(xn.getTrangThai() != null ? xn.getTrangThai() : "Không có").setFont(font));
	                    table.addCell(new Paragraph(xn.getGhiChu() != null ? xn.getGhiChu() : "Không có").setFont(font));
	                }
	                document.add(table);
	            } else {
	                document.add(new Paragraph("Không có dữ liệu").setFont(font));
	            }

	            document.add(new Paragraph("----------------------------------------").setTextAlignment(TextAlignment.CENTER));
	        }

	        document.close();

	        // Prepare PDF response
	        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lich-su-kham-benh.pdf");
	        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
	        headers.add(HttpHeaders.PRAGMA, "no-cache");
	        headers.add(HttpHeaders.EXPIRES, "0");

	        return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(baos.size())
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(resource);
	    } catch (Exception e) {
	        e.printStackTrace(); // Log the full stack trace for debugging
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Collections.singletonMap("error", "Không thể tạo PDF: " + e.getMessage()));
	    }
	}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap("error", "Đã xảy ra lỗi không mong muốn: " + e.getMessage()));
    }
	    
	@PostMapping("/user/updateprofile")
	public String updateProfile(@ModelAttribute("nguoiDung") TaiKhoanProfileDTO tk,
			@ModelAttribute("benhNhan") BenhNhanDTO benhNhanDTO, Model model) {
		nguoiDungId = getNguoiDungId();
		// Tìm người dùng hiện tại theo ID
		NguoiDung existingUser = nguoiDungService.findById(nguoiDungId);

		// Kiểm tra nếu tìm thấy người dùng, sau đó cập nhật thông tin
		if (existingUser != null) {
			// Cập nhật các trường từ DTO vào entity NguoiDung
			if (tk.getTenDangNhap() != null && !tk.getTenDangNhap().isEmpty()) {
				existingUser.setTenDangNhap(tk.getTenDangNhap());
			}
			if (tk.getEmail() != null && !tk.getEmail().isEmpty()) {
				existingUser.setEmail(tk.getEmail());
			}

			// Lưu người dùng
			nguoiDungService.saveNguoiDung(existingUser);
		}

		// Tìm bệnh nhân hiện tại theo ID
		BenhNhan existingBenhNhan = benhNhanService.findById(nguoiDungId);

		// Cập nhật thông tin bệnh nhân
		if (existingBenhNhan != null) {
			// Cập nhật các trường từ DTO vào entity BenhNhan
			if (benhNhanDTO.getTen() != null && !benhNhanDTO.getTen().isEmpty()) {
				existingBenhNhan.setTen(benhNhanDTO.getTen());
			}
			if (benhNhanDTO.getGioiTinh() != null && !benhNhanDTO.getGioiTinh().isEmpty()) {
				existingBenhNhan.setGioiTinh(benhNhanDTO.getGioiTinh());
			}
			if (benhNhanDTO.getNgaySinh() != null && !benhNhanDTO.getNgaySinh().isEmpty()) {
				existingBenhNhan.setNgaySinh(LocalDate.parse(benhNhanDTO.getNgaySinh()));
			}
			if (benhNhanDTO.getDiaChi() != null && !benhNhanDTO.getDiaChi().isEmpty()) {
				existingBenhNhan.setDiaChi(benhNhanDTO.getDiaChi());
			}
			if (benhNhanDTO.getDienThoai() != null && !benhNhanDTO.getDienThoai().isEmpty()) {
				existingBenhNhan.setDienThoai(benhNhanDTO.getDienThoai());
			}

			// Lưu bệnh nhân
			benhNhanService.save(existingBenhNhan);
		}

		// Redirect về trang hồ sơ hoặc trang khác sau khi cập nhật thành công
		return "redirect:/user/editprofile";
	}

	@GetMapping("/view/schedule")
	public String viewLichKhamBenh(@RequestParam(required = false) String chuyenKhoaId,
			@RequestParam(required = false) String ngayThangNam, Model model) {

		// Lấy danh sách chuyên khoa
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);

		// Kiểm tra nếu không có tham số nào được truyền
		if (chuyenKhoaId == null || ngayThangNam == null) {
			// Trả về view mặc định với thông tin là null
			model.addAttribute("lichKhamBenhPage", Collections.emptyList()); // Tránh null
			model.addAttribute("selectedChuyenKhoaId", ""); // Không chọn chuyên khoa nào
			model.addAttribute("selectedNgayThangNam", ""); // Không chọn ngày nào
			return "benhnhan/viewbs/viewlichkhambenh"; // Trả về view mặc định
		}

		// Xử lý ngày tháng và gọi service
		LocalDate ngay = LocalDate.parse(ngayThangNam);
		List<LichKhamBenh> lichKhamBenhPage = lichKhamBenhService.findLichKhamBenhByChuyenKhoaAndNgay(chuyenKhoaId,
				ngay);

		// Truyền dữ liệu vào model
		model.addAttribute("lichKhamBenhPage", lichKhamBenhPage);

		model.addAttribute("selectedChuyenKhoaId", chuyenKhoaId); // Lưu chuyên khoa được chọn
		model.addAttribute("selectedNgayThangNam", ngayThangNam); // Lưu ngày được chọn

		return "benhnhan/viewbs/viewlichkhambenh";
	}

	@GetMapping("/findbs")
	public String findBacSi(@RequestParam(name = "section", required = false, defaultValue = "all") String section,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "6") int size,
			@RequestParam(name = "doctorName", required = false) String doctorName, // Thêm tham số tìm kiếm theo tên
																					// bác sĩ
			Model model) {

		Pageable pageable = PageRequest.of(page, size);

		Page<BacSi> bacSiPage;

		// Tìm bác sĩ theo tên và chuyên khoa nếu có
		if (doctorName != null && !doctorName.isEmpty()) {
			bacSiPage = bacSiService.findByNameAndChuyenKhoa(doctorName, section, pageable); // Tìm kiếm theo tên và
																								// chuyên khoa
		} else if ("all".equals(section)) {
			// Nếu chọn tất cả, load toàn bộ bác sĩ
			bacSiPage = bacSiService.findAll(pageable);
		} else {
			// Nếu chọn chuyên khoa cụ thể, load bác sĩ thuộc chuyên khoa đó
			bacSiPage = bacSiService.findByChuyenKhoaTen(section, pageable); // Sửa lại ở đây
		}

		// Load danh sách chuyên khoa để hiển thị trong dropdown
		List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();

		// Add vào model để render ra view
		model.addAttribute("bacSiPage", bacSiPage);
		model.addAttribute("chuyenKhoaList", chuyenKhoaList);
		model.addAttribute("currentSection", section); // Giữ giá trị đã chọn
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		model.addAttribute("doctorName", doctorName); // Thêm tham số tìm kiếm tên bác sĩ

		return "benhnhan/viewbs/findbs"; // Trả về view
	}

	@GetMapping("/chitietbacsi/{bacSiId}")
    public String viewChiTietBacSi(@PathVariable("bacSiId") String bacSiId,
                                   @RequestParam(value = "ngay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngay,
                                   Model model) {
        BacSi bacSi = bacSiService.findById(bacSiId);
        if (bacSi == null) {
            model.addAttribute("errorMessage", "Bác sĩ không tồn tại");
            return "error";
        }

        // Debug giaKham
        System.out.println("Controller - BacSi ID: " + bacSiId + ", giaKham: " + bacSi.getGiaKham());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.addAttribute("formattedDate", bacSi.getNgaySinh() != null ? bacSi.getNgaySinh().format(formatter) : "Không có thông tin");

        if (ngay == null) {
            ngay = LocalDate.now();
        }
        List<LichKhamBenh> lichKhamBenhList = lichKhamBenhService.findByBacSiAndNgay(bacSi, ngay);

        model.addAttribute("bacSi", bacSi);
        model.addAttribute("lichKhamBenhList", lichKhamBenhList);
        model.addAttribute("selectedDate", ngay);

        return "benhnhan/viewbs/chitietbs";
    }

	@GetMapping("/user/dangkylichkham")
    public String registerSchedule(Model model) {
        List<ChuyenKhoa> chuyenKhoaList = chuyenKhoaService.getAllChuyenKhoa();
        model.addAttribute("chuyenKhoaList", chuyenKhoaList);
        return "benhnhan/dangkylichkham/buoc1"; 
    }

    // API lấy danh sách bác sĩ theo chuyên khoa
    @PostMapping("/user/dangkylichkham/doctor")
    @ResponseBody
    public String getDoctorsBySpecialty(@RequestParam("service") String serviceId) {
        System.out.println("Service ID nhận được: " + serviceId); // Debug

        List<BacSiDTO> bacSiList = bacSiService.getBacSiByChuyenKhoa(serviceId);

        if (bacSiList == null || bacSiList.isEmpty()) {
            System.out.println("Không tìm thấy bác sĩ cho chuyên khoa: " + serviceId);
            return "<option value=''>Không có bác sĩ</option>";
        }

        StringBuilder options = new StringBuilder();
        for (BacSiDTO bacSi : bacSiList) {
            options.append("<option value='").append(bacSi.getId()).append("'>")
                   .append(bacSi.getTen()).append("</option>");
        }

        return options.toString();
    }

	@PostMapping("/user/dangkylichkham/next")
	public String proceedToStep2(@RequestParam("service") String serviceId, 
	                             @RequestParam("doctor") String doctorId, 
	                             Model model) {
	    if (serviceId == null || serviceId.isEmpty()) {
	        throw new IllegalArgumentException("Chuyên khoa không được để trống.");
	    }
	    if (doctorId == null || doctorId.isEmpty()) {
	        throw new IllegalArgumentException("Bác sĩ không được để trống.");
	    }

	    model.addAttribute("doctorId", doctorId);
	    model.addAttribute("serviceId", serviceId);

	 

	    return "benhnhan/dangkylichkham/buoc2";
	}



	public LocalDate parseDate(String dateStr) {
	    if (dateStr != null && !dateStr.isEmpty()) {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
	        return LocalDate.parse(dateStr, formatter);
	    }
	    return null;
	}

	@PostMapping("/user/dangkylichkham/getAvailableSlots")
	@ResponseBody
	public Map<String, Object> getAvailableSlots(@RequestBody Map<String, String> requestData) {
		String doctorId = requestData.get("doctorId");
		String date = requestData.get("date");

		LocalDate selectedDate = parseDate(date);

		List<LichKhamBenh> lichKhamBenhList = lichKhamBenhService.getLichKhamBenhByNgayAndBacSi(doctorId, selectedDate);

		List<String> caList = new ArrayList<>();
		if (lichKhamBenhList != null && !lichKhamBenhList.isEmpty()) {
			for (LichKhamBenh lichKhamBenh : lichKhamBenhList) {
				caList.add(lichKhamBenh.getCa());

			}
		}

		// Danh sách kết quả trả về
		Map<String, Object> result = new HashMap<>();
		result.put("doctorId", doctorId);
		result.put("date", selectedDate);
		result.put("caList", caList);

		// Lấy danh sách các slot đã được đặt cho bác sĩ
		List<String> bookedSlots = new ArrayList<>();
		for (LichKhamBenh lichKhamBenh : lichKhamBenhList) {
			List<SlotThoiGian> slots = slotThoiGianService.findByLichKhamBenh(lichKhamBenh.getMaLichKhamBenh());

			for (SlotThoiGian slot : slots) {
				System.out.println(slot.getThoiGianBatDau().toString() + "hihi");
				bookedSlots.add(slot.getThoiGianBatDau().toString()); // Thêm giờ bắt đầu của slot vào danh sách
			}
		}

		// Trả về thông tin slot đã đặt và các ca khám
		result.put("bookedSlots", bookedSlots);
		
		return result;
	}

	@GetMapping("/user/dangkylichkham/buoc3")
	public String proceedToStep3(@RequestParam("doctorId") String doctorId,
			@RequestParam("selectedDate") String selectedDate, 
			@RequestParam("selectedTime") String selectedTime,
			@RequestParam("ca") String ca, Model model) {

		// Fetch the doctor information using doctorId
		BacSi doctor = bacSiService.findById(doctorId);

		// Add the data to the model to be rendered in the view
		model.addAttribute("doctor", doctor);
		model.addAttribute("date", selectedDate);
		model.addAttribute("time", selectedTime);
		model.addAttribute("ca", ca);

		// Return the view for Step 3
		return "/benhnhan/dangkylichkham/buoc3";
	}

	public LocalTime calculateStartTime(String selectedTime) {
		// Chuyển đổi selectedTime (dạng String) thành LocalTime
		return LocalTime.parse(selectedTime); // Giả sử selectedTime là một chuỗi như "07:30", "13:00", "17:00"
	}

	public LocalTime calculateEndTime(String selectedTime) {
		// Chuyển đổi selectedTime (dạng String) thành LocalTime
		LocalTime startTime = LocalTime.parse(selectedTime); // Giả sử selectedTime là "07:30", "13:00", "17:00"

		// Cộng thêm 30 phút vào thời gian bắt đầu
		return startTime.plusMinutes(30); // Cộng thêm 30 phút vào giờ bắt đầu
	}

	@PostMapping("/user/dangkylichkham/confirm")
	public ResponseEntity<Map<String, String>> confirmBooking(@RequestBody Map<String, String> requestData) {
	    String doctorId = requestData.get("doctorId");
	    String selectedDate = requestData.get("selectedDate");
	    String selectedTime = requestData.get("selectedTime");
	    String ca = requestData.get("selectedCa");
	    LocalDate selectedDates;
	    nguoiDungId = getNguoiDungId();
	    
	    try {
	        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
	            .appendPattern("yyyy")
	            .appendLiteral('-')
	            .appendPattern("M")
	            .appendLiteral('-')
	            .appendPattern("d")
	            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
	            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
	            .toFormatter();
	        
	        selectedDates = LocalDate.parse(selectedDate, formatter);
	    } catch (DateTimeParseException e) {
	        Map<String, String> response = new HashMap<>();
	        response.put("status", "error");
	        response.put("message", "Định dạng ngày không hợp lệ. Vui lòng nhập đúng định dạng yyyy-MM-dd.");
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }

	    // Tìm Lịch Khám Bệnh theo doctorId, selectedDate và ca
	    MaLichKhamBenhDTO lichKhamBenh = lichKhamBenhService.findByDoctorIdAndDateAndCa(doctorId, selectedDates, ca);
	    BenhNhan benhnhan = benhNhanService.findById(nguoiDungId);
	    Optional<LichKhamBenh> lichkham = lichKhamBenhService.findById(lichKhamBenh.getMaLichKhamBenh());
	    SlotThoiGian check = slotThoiGianService.findExist(calculateStartTime(selectedTime), benhnhan.getBenhNhanId(),
	            lichKhamBenh.getMaLichKhamBenh());

	    if (lichKhamBenh != null && check == null) {
	        SlotThoiGian slotThoiGian = new SlotThoiGian();
	        slotThoiGian.setSlotId(UUID.randomUUID().toString()); // Tạo ID ngẫu nhiên cho Slot
	        slotThoiGian.setLichKhamBenh(lichkham.get()); // Liên kết với lịch khám bệnh đã tìm được

	        // Xác định giờ bắt đầu và kết thúc dựa trên `ca` và `selectedTime`
	        LocalTime thoiGianBatDau = calculateStartTime(selectedTime);
	        LocalTime thoiGianKetThuc = calculateEndTime(selectedTime);

	        slotThoiGian.setThoiGianBatDau(thoiGianBatDau);
	        slotThoiGian.setThoiGianKetThuc(thoiGianKetThuc);
	        slotThoiGian.setTrangThai("pending");
	        slotThoiGian.setBenhNhan(benhnhan);

	        // Lưu SlotThoiGian vào cơ sở dữ liệu
	        slotThoiGianService.save(slotThoiGian);

	        // Return a response with a success message
	        Map<String, String> response = new HashMap<>();
	        response.put("status", "success");
	        response.put("message", "Booking confirmed successfully");

	        return ResponseEntity.ok(response);
	    } else {
	        // Return a response with a failure message
	        Map<String, String> response = new HashMap<>();
	        response.put("status", "failure");
	        response.put("message", "Đã có người đặt khung giờ này, vui lòng thực hiện lại");

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	    }
	}

	@GetMapping("/user/lichsudatlich")
	public String getLichSuDatLich(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String date) {
		nguoiDungId = getNguoiDungId();
		// Find BenhNhan by nguoiDungId
		BenhNhan benhNhan = benhNhanService.findById(nguoiDungId);
		if (benhNhan == null) {
			// Handle the case where no patient is found for the user
			model.addAttribute("message", "Bệnh nhân không tìm thấy.");
			return "benhnhan/dangkylichkham/lichsudatlichkham";
		}

		// Determine the date to filter by, default to today
		LocalDate filteredDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

		// Get paginated list of slots based on BenhNhan and date
		Page<SlotThoiGian> slotPage = lichSuDatLichService.getLichSuDatLichByDateAndBenhNhanId(benhNhan.getBenhNhanId(),
				filteredDate, page, 3);

		model.addAttribute("slots", slotPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", slotPage.getTotalPages());
		model.addAttribute("date", filteredDate); // Pass the date to keep it in the view

		return "benhnhan/dangkylichkham/lichsudatlichkham";
	}

	@PostMapping("user/cancel")
	public String cancelSlot(@RequestParam String slotId, @RequestParam(required = false) String date,
			@RequestParam(required = false, defaultValue = "0") int page) {
		// Xử lý hủy lịch
		slotThoiGianService.deleteById(slotId);

		// Sau khi hủy, quay lại với các thông tin lịch sử
		return "redirect:/user/lichsudatlich?date=" + date + "&page=" + page;
	}

	// Controller để xử lý các yêu cầu liên quan đến chuyên khoa
	@GetMapping("/departments")
	public String showDepartments(Model model) {
		// Lấy danh sách chuyên khoa từ service
		List<ChuyenKhoa> chuyenKhoas = chuyenKhoaService.getAllChuyenKhoa();
		model.addAttribute("chuyenKhoas", chuyenKhoas);
		return "benhnhan/xemchuyenkhoa/danhsachchuyenkhoa"; // Trang hiển thị danh sách chuyên khoa
	}

	@GetMapping("/departments/{chuyenKhoaId}")
	public String showDepartmentDetails(@PathVariable String chuyenKhoaId, Model model) {
		// Lấy thông tin chuyên khoa từ service theo ID
		ChuyenKhoa chuyenKhoa = chuyenKhoaService.getChuyenKhoaById(chuyenKhoaId);
		model.addAttribute("chuyenKhoa", chuyenKhoa);
		return "benhnhan/xemchuyenkhoa/xemchitietchuyenkhoa"; // Trang chi tiết chuyên khoa
	}

	@GetMapping("/test-font")
	public ResponseEntity<?> testFont() {
	    try {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf);
	        PdfFont font = PdfFontFactory.createFont("/fonts/BeVietnamPro-Black.ttf", PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
	        document.add(new Paragraph("Kiểm tra font tiếng Việt: Định dạng, Chẩn đoán, Triệu chứng").setFont(font));
	        document.close();
	        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test-font.pdf");
	        return ResponseEntity.ok()
	            .headers(headers)
	            .contentLength(baos.size())
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(resource);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Collections.singletonMap("error", "Không thể tạo PDF: " + e.getMessage()));
	    }
	}
}
