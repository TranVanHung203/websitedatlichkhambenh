package tlcn.quanlyphongkham.entities;

import lombok.Data;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "SlotThoiGian")
public class SlotThoiGian implements Serializable {

	@Id
    @Column(name = "slot_id", length = 36)
    private String slotId;
    
    @ManyToOne
    @JoinColumn(name = "maLichKhamBenh", nullable = false)
    private LichKhamBenh lichKhamBenh; // Liên kết đến lịch khám bệnh
    
    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalTime thoiGianBatDau;
    
    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalTime thoiGianKetThuc;
    
    @Column(name = "trang_thai", length = 10, nullable = false)
    private String trangThai; // "Trống" hoặc "Đã đặt"
    
    @ManyToOne
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan; // Liên kết đến bệnh nhân nếu slot đã được đặt
}
