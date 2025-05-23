package tlcn.quanlyphongkham.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "url_avatar", columnDefinition = "nvarchar(100)")
    private String urlAvatar;

    @OneToOne(mappedBy = "bacSi", cascade = CascadeType.ALL)
    @JsonIgnore // Prevent serialization recursion
    private ChiTietBacSi chiTietBacSi;

    @OneToMany(mappedBy = "bacSi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LichKhamBenh> lichKhamBenh;

    @Override
    public int hashCode() {
        return Objects.hash(bacSiId); // Only use bacSiId to avoid recursion
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BacSi bacSi = (BacSi) o;
        return Objects.equals(bacSiId, bacSi.bacSiId);
    }
}