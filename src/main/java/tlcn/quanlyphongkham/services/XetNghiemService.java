package tlcn.quanlyphongkham.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.ChuyenKhoa;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.LoaiXetNghiem;
import tlcn.quanlyphongkham.entities.XetNghiem;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.LoaiXetNghiemRepository;
import tlcn.quanlyphongkham.repositories.XetNghiemRepository;

@Service
public class XetNghiemService {

	@Autowired
	private XetNghiemRepository xetNghiemRepository;

	@Autowired
	private HoSoBenhRepository hoSoBenhRepository;

	@Autowired
	private LoaiXetNghiemRepository loaiXetNghiemRepository;

	@Value("${file.upload-dir:${user.dir}/uploads/xetnghiem}")
	private String uploadDir;

	public void saveXetNghiem(String hoSoId, Map<String, Object> data) {
		System.out.println("Saving xetNghiem with hoSoId: " + hoSoId);
		HoSoBenh hoSoBenh = hoSoBenhRepository.findById(hoSoId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh: " + hoSoId));

		// Lưu chẩn đoán nếu có
		String chanDoan = parseString(data.get("chanDoan"));
		if (chanDoan != null) {
			if (chanDoan.length() > 255) {
				throw new IllegalArgumentException("Chẩn đoán không được vượt quá 255 ký tự");
			}
			hoSoBenh.setChanDoan(chanDoan);
			hoSoBenhRepository.save(hoSoBenh);
		}

		// Thêm xét nghiệm nếu có loaiXetNghiemId
		String loaiXetNghiemId = parseString(data.get("loaiXetNghiemId"));
		if (loaiXetNghiemId != null) {
			LoaiXetNghiem loaiXetNghiem = loaiXetNghiemRepository.findById(Long.parseLong(loaiXetNghiemId))
					.orElseThrow(() -> new RuntimeException("Không tìm thấy loại xét nghiệm: " + loaiXetNghiemId));

			XetNghiem xetNghiem = new XetNghiem();
			xetNghiem.setHoSoBenh(hoSoBenh);
			xetNghiem.setLoaiXetNghiem(loaiXetNghiem);
			xetNghiem.setGhiChu(parseString(data.get("ghiChu")));
			xetNghiemRepository.save(xetNghiem);

			// Cập nhật tổng tiền
			int tongTien = hoSoBenh.getTongTien() != null ? hoSoBenh.getTongTien() : 0;
			tongTien += loaiXetNghiem.getGia();
			hoSoBenh.setTongTien(tongTien);
			hoSoBenhRepository.save(hoSoBenh);
		}
	}

	public List<XetNghiem> getXetNghiemByHoSoId(String hoSoId) {
		List<XetNghiem> danhSachXetNghiem = xetNghiemRepository.findByHoSoBenhHoSoId(hoSoId);
		return danhSachXetNghiem != null ? danhSachXetNghiem : new ArrayList<>();
	}

	public String uploadKetQua(List<Long> xetNghiemIds, MultipartFile file) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File upload không hợp lệ hoặc rỗng");
		}
		if (xetNghiemIds == null || xetNghiemIds.isEmpty()) {
			throw new IllegalArgumentException("Danh sách xét nghiệm rỗng");
		}

		// Validate loại file
		String contentType = file.getContentType();
		boolean isValidType = contentType != null
			    && (contentType.startsWith("image/")
			        || contentType.equals("application/pdf")
			        || contentType.equals("application/msword")
			        || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
			        || contentType.equals("text/plain"));
			if (!isValidType) {
			    throw new IllegalArgumentException("Chỉ cho phép upload hình ảnh, PDF, DOC, DOCX hoặc TXT");
			}

		// Tạo tên file duy nhất
		String filename = UUID.randomUUID() + "-" + StringUtils.cleanPath(file.getOriginalFilename());
		Path uploadPath = Paths.get(uploadDir);

		// Kiểm tra và tạo thư mục
		System.out.println("Upload directory configured: " + uploadDir);
		System.out.println("Checking upload directory: " + uploadPath);
		if (!Files.exists(uploadPath)) {
			System.out.println("Creating directory: " + uploadPath);
			Files.createDirectories(uploadPath);
		}
		if (!Files.isWritable(uploadPath)) {
			throw new IOException("Thư mục không có quyền ghi: " + uploadPath);
		}

		// Lưu file
		Path filePath = uploadPath.resolve(filename);
		System.out.println("Saving file to: " + filePath);
		try {
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
			// Kiểm tra file đã được tạo
			if (!Files.exists(filePath)) {
				throw new IOException("File không được tạo tại: " + filePath);
			}
			System.out.println("File saved successfully: " + filePath);
		} catch (IOException e) {
			System.err.println("Error saving file: " + e.getMessage());
			throw new IOException("Lỗi khi lưu file: " + e.getMessage(), e);
		}

		// Cập nhật kết quả cho các xét nghiệm
		for (Long xetNghiemId : xetNghiemIds) {
			XetNghiem xetNghiem = xetNghiemRepository.findById(xetNghiemId)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy xét nghiệm: " + xetNghiemId));
			xetNghiem.setFileKetQua("/files/xetnghiem/" + filename);
			xetNghiem.setTrangThai("Đã có kết quả");
			xetNghiemRepository.save(xetNghiem);
		}

		// Trả về đường dẫn tương đối
		return "/files/xetnghiem/" + filename;
	}

	public void deleteXetNghiem(Long id) {
		XetNghiem xetNghiem = xetNghiemRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xét nghiệm: " + id));

		// Cập nhật tổng tiền trong hồ sơ bệnh
		HoSoBenh hoSoBenh = xetNghiem.getHoSoBenh();
		int tongTien = hoSoBenh.getTongTien() != null ? hoSoBenh.getTongTien() : 0;
		tongTien -= xetNghiem.getLoaiXetNghiem().getGia();
		hoSoBenh.setTongTien(Math.max(tongTien, 0));
		hoSoBenhRepository.save(hoSoBenh);

		// Xóa xét nghiệm
		xetNghiemRepository.deleteById(id);
	}

	private String parseString(Object value) {
		return value != null && !value.toString().isEmpty() ? value.toString() : null;
	}

	public List<Map<String, Object>> getPendingXetNghiem(String tenBenhNhan, String dienThoai, String trangThai,
			LocalDate thoiGianTao) {
		List<XetNghiem> xetNghiemList = xetNghiemRepository.findByFilters(
				tenBenhNhan != null && !tenBenhNhan.isEmpty() ? "%" + tenBenhNhan.toLowerCase() + "%" : null,
				dienThoai != null && !dienThoai.isEmpty() ? "%" + dienThoai + "%" : null,
				trangThai != null && !trangThai.isEmpty() ? trangThai : null, thoiGianTao);

		List<Map<String, Object>> result = new ArrayList<>();
		for (XetNghiem xetNghiem : xetNghiemList) {
			HoSoBenh hoSoBenh = xetNghiem.getHoSoBenh();
			BenhNhan benhNhan = hoSoBenh != null ? hoSoBenh.getBenhNhan() : null;
			LoaiXetNghiem loaiXetNghiem = xetNghiem.getLoaiXetNghiem();

			if (benhNhan == null || loaiXetNghiem == null || hoSoBenh == null) {
				continue;
			}

			Map<String, Object> testInfo = new HashMap<>();
			testInfo.put("xetNghiemId", xetNghiem.getId());
			testInfo.put("tenBenhNhan", benhNhan.getTen());
			testInfo.put("dienThoai", benhNhan.getDienThoai());
			testInfo.put("tenXetNghiem", loaiXetNghiem.getTenXetNghiem());
			testInfo.put("trangThai", xetNghiem.getTrangThai());
			testInfo.put("fileKetQua", xetNghiem.getFileKetQua());
			testInfo.put("thoiGianTao",
					hoSoBenh.getThoiGianTao() != null ? hoSoBenh.getThoiGianTao().toLocalDate() : null);
			result.add(testInfo);
		}

		return result;
	}
}