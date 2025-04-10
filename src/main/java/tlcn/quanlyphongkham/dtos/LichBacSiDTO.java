package tlcn.quanlyphongkham.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichBacSiDTO {
	private String caKham;
    private LocalDate ngayThangNam;
    private String thoiGianBatDau;
    private String thoiGianKetThuc;
    private String trangThai; // Chỉ "Đã đặt"
    private String tenBenhNhan;
    private String soDienThoai;
    private String slotId;
}
