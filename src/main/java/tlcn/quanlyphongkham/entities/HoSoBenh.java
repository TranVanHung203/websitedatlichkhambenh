package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "chan_doan", columnDefinition = "nvarchar(100)", nullable = false)
    private String chanDoan;


    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;
    
    @OneToMany(mappedBy = "hoSoBenh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonThuoc> donThuocs;
    
    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            // Chỉ lấy ngày, tháng, năm, giờ, phút và giây
            this.thoiGianTao = LocalDateTime.now().withNano(0);
        }
    }

  

 
}