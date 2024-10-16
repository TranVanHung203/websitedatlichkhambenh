package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LichHen")
public class LichHen implements Serializable {
    @Id
    @Column(name = "lich_hen_id", length = 36)
    private String lichHenId;

    @ManyToOne
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan;

    @ManyToOne
    @JoinColumn(name = "bac_si_id")
    private BacSi bacSi;

    @Column(name = "ngay_hen", nullable = false)
    private LocalDateTime ngayHen;

    @Column(name = "trang_thai", length = 20, columnDefinition = "NVARCHAR(20) DEFAULT 'Đang chờ'")
    private String trangThai;
}