package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SucKhoeDuLieu")
public class SucKhoeDuLieu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nguoi_dung_id", nullable = false, length = 36)
    private String nguoiDungId;

    @Column(name = "chi_so", nullable = false, length = 50) // Tăng length lên 50
    private String chiSo; // huyetAp, duongHuyet, canNang

    @Column(name = "gia_tri_tam_thu")
    private Double tamThu; // Huyết áp tâm thu (mmHg)

    @Column(name = "gia_tri_tam_truong")
    private Double tamTruong; // Huyết áp tâm trương (mmHg)

    @Column(name = "gia_tri_duong_huyet")
    private Double duongHuyet; // Đường huyết (mg/dL)

    @Column(name = "thoi_diem_duong_huyet", length = 10)
    private String thoiDiemDuongHuyet; // truocAn, sauAn

    @Column(name = "gia_tri_can_nang")
    private Double canNang; // Cân nặng (kg)

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @Column(name = "nguon", length = 10)
    private String nguon; // tay, tuDong
}