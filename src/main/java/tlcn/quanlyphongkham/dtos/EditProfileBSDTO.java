package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileBSDTO {
    private String bacSiId;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String dienThoai;
    private String diaChi;
    
    // Thêm thông tin tài khoản
    private String email;
    private String tenDangNhap;
    private String chuyenKhoaId; 
    private String avatarurl; 
    private BigDecimal giaKham; // Thêm trường giá khám
}