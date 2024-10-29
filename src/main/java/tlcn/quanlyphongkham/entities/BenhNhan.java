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
@Table(name = "BenhNhan")
public class BenhNhan implements Serializable {
    @Id
    @Column(name = "benh_nhan_id", length = 36)
    private String benhNhanId;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;
    
    @Column(name = "ten", nullable = false, columnDefinition = "nvarchar(100)")
    private String ten;


    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10, columnDefinition = "nvarchar(10)")
    private String gioiTinh;


    @Column(name = "dien_thoai", length = 15)
    private String dienThoai;

    @Column(name = "dia_chi", length = 255,columnDefinition = "nvarchar(100)")
    private String diaChi;
    
    @OneToMany(mappedBy = "benhNhan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SlotThoiGian> lichKhamBenh; // Danh sách các slot đã đặt của bệnh nhân

}
