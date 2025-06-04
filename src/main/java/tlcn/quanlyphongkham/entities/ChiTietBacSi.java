package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

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

    @OneToOne
    @JoinColumn(name = "bac_si_id", nullable = false)
    @JsonIgnore
    private BacSi bacSi;

    @Column(name = "bang_cap", length = 255, columnDefinition = "nvarchar(500)")
    private String bangCap;

    @Column(name = "hoi_nghi_nuoc_ngoai", length = 255, columnDefinition = "nvarchar(500)")
    private String hoiNghiNuocNgoai;

    @Column(name = "chung_chi", length = 255, columnDefinition = "nvarchar(500)")
    private String chungChi;

    @Column(name = "dao_tao_chuyen_nganh", length = 255, columnDefinition = "nvarchar(500)")
    private String daoTaoChuyenNganh;

    @Column(name = "linh_vuc_chuyen_sau", length = 255, columnDefinition = "nvarchar(500)")
    private String linhVucChuyenSau;

    @Column(name = "gioi_thieu", columnDefinition = "nvarchar(500)")
    private String gioiThieu;

    @Override
    public int hashCode() {
        return Objects.hash(chiTietBacSiId); // Only use chiTietBacSiId to avoid recursion
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietBacSi that = (ChiTietBacSi) o;
        return Objects.equals(chiTietBacSiId, that.chiTietBacSiId);
    }
}