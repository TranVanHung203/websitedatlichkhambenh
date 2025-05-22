package tlcn.quanlyphongkham.entities;

import java.io.Serializable;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "SlotThoiGian")
public class SlotThoiGian implements Serializable {

    @Id
    @Column(name = "slot_id", length = 36)
    private String slotId;

    @ManyToOne
    @JoinColumn(name = "maLichKhamBenh", nullable = false)
    @JsonIgnore
    private LichKhamBenh lichKhamBenh; // Liên kết đến lịch khám bệnh

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc", nullable = false)
    private LocalTime thoiGianKetThuc;

    @Column(name = "trang_thai", length = 20, nullable = false)
    private String trangThai; // "Trống" hoặc "Đã đặt"

    @ManyToOne
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhan benhNhan; // Liên kết đến bệnh nhân nếu slot đã được đặt

   
}