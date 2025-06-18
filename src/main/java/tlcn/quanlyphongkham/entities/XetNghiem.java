package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "XetNghiem")
public class XetNghiem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "xet_nghiem_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ho_so_id", nullable = false)
    private HoSoBenh hoSoBenh;

    @ManyToOne
    @JoinColumn(name = "loai_xet_nghiem_id", nullable = false)
    private LoaiXetNghiem loaiXetNghiem; // Thay tenXetNghiem

    @Column(name = "ghi_chu", columnDefinition = "nvarchar(255)")
    private String ghiChu;

    @Column(name = "trang_thai", length = 20)
    private String trangThai; // "Cho", "DaUpload", "DaXem"

    @Column(name = "file_ket_qua", columnDefinition = "nvarchar(255)")
    private String fileKetQua; // Đường dẫn file

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;

    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            this.thoiGianTao = LocalDateTime.now().withNano(0);
        }
        if (this.trangThai == null) {
            this.trangThai = "Chờ kết quả";
        }
    }
}