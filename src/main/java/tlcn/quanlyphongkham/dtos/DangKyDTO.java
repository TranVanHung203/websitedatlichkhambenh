package tlcn.quanlyphongkham.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DangKyDTO {
    // Thông tin NguoiDung
    private String tenDangNhap;
    private String matKhau;
    private String email;

    // Thông tin BenhNhan
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String dienThoai;
    private String diaChi;
}
