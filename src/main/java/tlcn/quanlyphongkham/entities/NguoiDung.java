package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "vai_tro", nullable = false, length = 20,columnDefinition = "nvarchar(100)")
    private String vaiTro;

    
    @Column(name = "active")
    private String trangthai;
    
    @Column(name = "token")
    private String token;
    
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BacSi> bacSis;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BenhNhan> benhNhans;
}
