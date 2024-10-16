package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HoSoBenh")
public class HoSoBenh implements Serializable {
    @Id
    @Column(name = "ho_so_id", length = 36)
    private String hoSoId;

    @ManyToOne
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan;

    @ManyToOne
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;

    @Column(name = "chan_doan", columnDefinition = "TEXT", nullable = false)
    private String chanDoan;

    @Column(name = "don_thuoc", columnDefinition = "TEXT")
    private String donThuoc;

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;
}