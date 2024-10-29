package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BacSi")
public class BacSi implements Serializable {
    @Id
    @Column(name = "bac_si_id", length = 36)
    private String bacSiId;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "ten", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String ten;

    @ManyToOne
    @JoinColumn(name = "chuyen_khoa_id")
    private ChuyenKhoa chuyenKhoa;

    @Column(name = "dien_thoai", length = 15)
    private String dienThoai;

    @Column(name = "dia_chi", length = 255, columnDefinition = "nvarchar(100)")
    private String diaChi;
    
    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10, columnDefinition = "nvarchar(10)")
    private String gioiTinh;
    
    @OneToOne(mappedBy = "bacSi", cascade = CascadeType.ALL)
    private ChiTietBacSi chiTietBacSi;

    @OneToMany(mappedBy = "bacSi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichKhamBenh> lichKhamBenh; // Danh sách lịch khám của bác sĩ
}
