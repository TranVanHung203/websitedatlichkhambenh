package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DonThuoc")
public class DonThuoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "don_thuoc_id")
    private Long donThuocId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ho_so_id")
    private HoSoBenh hoSoBenh;

    @OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonThuocThuoc> donThuocThuocs;

    @Transient 
    private String formattedDate;

    @Transient
    private String formattedTongTienThuoc;

    // Phương thức tính tổng tiền bằng số lượng
    public BigDecimal calculateTongTien() {
        BigDecimal total = BigDecimal.ZERO;
        for (DonThuocThuoc dtt : donThuocThuocs) {
            BigDecimal gia = dtt.getThuoc().getGia();
            BigDecimal soLuong = BigDecimal.valueOf(dtt.getSoLuong());
            total = total.add(gia.multiply(soLuong));
        }
        return total;
    }

    // Phương thức định dạng tổng tiền
    public String getFormattedTongTienThuoc() {
        if (formattedTongTienThuoc == null) {
            BigDecimal tongTienThuoc = calculateTongTien();
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            formattedTongTienThuoc = formatter.format(tongTienThuoc) + " VNĐ";
        }
        return formattedTongTienThuoc;
    }
}
