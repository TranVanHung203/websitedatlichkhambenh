package tlcn.quanlyphongkham.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ThemTaiKhoanDTO {
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String vaiTro;

    // Thông tin thêm cho Bác sĩ và Bệnh nhân
    private String ten;
    private String dienThoai;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;

    // Thông tin thêm cho Bác sĩ
    private String chuyenKhoaId;
}