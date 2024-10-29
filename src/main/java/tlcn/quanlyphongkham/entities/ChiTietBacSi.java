package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChiTietBacSi")
public class ChiTietBacSi implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chi_tiet_bac_si_id")
    private Long chiTietBacSiId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false)
    private BacSi bacSi;

    @Column(name = "bang_cap", length = 255,columnDefinition = "nvarchar(500)")
    private String bangCap;

    @Column(name = "hoi_nghi_nuoc_ngoai", length = 255,columnDefinition = "nvarchar(500)")
    private String hoiNghiNuocNgoai;

    @Column(name = "chung_chi", length = 255,columnDefinition = "nvarchar(500)")
    private String chungChi;

    @Column(name = "dao_tao_chuyen_nganh", length = 255,columnDefinition = "nvarchar(500)")
    private String daoTaoChuyenNganh;

    @Column(name = "linh_vuc_chuyen_sau", length = 255,columnDefinition = "nvarchar(500)")
    private String linhVucChuyenSau;

    @Column(name = "an_ban", length = 255, columnDefinition = "nvarchar(500)")
    private String anBan;

    @Column(name = "gioi_thieu", columnDefinition = "nvarchar(500)")
    private String gioiThieu;  
}
