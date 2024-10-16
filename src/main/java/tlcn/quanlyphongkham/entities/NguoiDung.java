package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NguoiDung")
public class NguoiDung implements Serializable {
    @Id
    @Column(name = "nguoi_dung_id", length = 36)
    private String nguoiDungId;

    @Column(name = "ten_dang_nhap", nullable = false, length = 50)
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "vai_tro", nullable = false, length = 20)
    private String vaiTro;

    @Column(name = "thoi_gian_tao")
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;
}
