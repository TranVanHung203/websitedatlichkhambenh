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
@Table(name = "PhieuXetNghiem")
public class PhieuXetNghiem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "phieu_xet_nghiem_id")
    private Long id;

    @Column(name = "ma_phieu", length = 50, unique = true)
    private String maPhieu;

    @ManyToOne
    @JoinColumn(name = "ho_so_id", nullable = false)
    private HoSoBenh hoSoBenh;

    @Column(name = "tong_gia")
    private Integer tongGia; // Tổng giá xét nghiệm

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;

    @ElementCollection
    @CollectionTable(name = "PhieuXetNghiem_XetNghiem", joinColumns = @JoinColumn(name = "phieu_xet_nghiem_id"))
    @Column(name = "xet_nghiem_id")
    private List<Long> xetNghiemIds;

    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            this.thoiGianTao = LocalDateTime.now().withNano(0);
        }
        if (this.maPhieu == null) {
            this.maPhieu = "XN-" + LocalDateTime.now().toString().replaceAll("[^0-9]", "") + "-" + (int)(Math.random() * 1000);
        }
    }
}