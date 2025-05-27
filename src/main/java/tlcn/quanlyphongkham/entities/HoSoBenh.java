package tlcn.quanlyphongkham.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HoSoBenh")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HoSoBenh implements Serializable {
    @Id
    @Column(name = "ho_so_id", length = 36)
    private String hoSoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benh_nhan_id")
    @JsonIgnoreProperties({"hoSoBenhs"})
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id")
    @JsonIgnoreProperties({"hoSoBenhs"})
    private BacSi bacSi;

    @Column(name = "chan_doan", columnDefinition = "nvarchar(100)", nullable = false)
    private String chanDoan;
    
    @Column(name = "trieu_chung", columnDefinition = "nvarchar(100)")
    private String trieuChung;

    @Column(name = "tong_tien")
    private Integer tongTien;

    @Column(name = "da_thanh_toan")
    private Boolean daThanhToan;

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @OneToMany(mappedBy = "hoSoBenh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hoSoBenh"})
    private List<DonThuoc> donThuocs;

    @OneToMany(mappedBy = "hoSoBenh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hoSoBenh"})
    private List<XetNghiem> xetNghiems;

    @OneToMany(mappedBy = "hoSoBenh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hoSoBenh"})
    private List<PhieuXetNghiem> phieuXetNghiems;

    @OneToMany(mappedBy = "hoSoBenh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hoSoBenh"})
    private List<VitalSigns> vitalSigns;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = true, unique = true) // Thêm cột slot_id, có thể null
    private SlotThoiGian slotThoiGian; // Quan hệ One-to-One với SlotThoiGian
    
    
    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            this.thoiGianTao = LocalDateTime.now().withNano(0);
        }
        if (this.tongTien == null) {
            this.tongTien = 0;
        }
        if (this.daThanhToan == null) {
            this.daThanhToan = false;
        }
        if (this.trangThai == null) {
            this.trangThai = false;
        }
    }

    public void addDonThuoc(DonThuoc donThuoc) {
        if (donThuocs == null) {
            donThuocs = new ArrayList<>();
        }
        donThuocs.add(donThuoc);
        donThuoc.setHoSoBenh(this);
    }
}