package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DonThuoc")
public class DonThuoc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "don_thuoc_id")
	private Long donThuocId;

	@ManyToOne
	@JoinColumn(name = "ho_so_id")
	private HoSoBenh hoSoBenh;

	@OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<DonThuocThuoc> donThuocThuocs;

	@Transient private String formattedDate;
	public BigDecimal calculateTongTien() {
		BigDecimal total = BigDecimal.ZERO;
		for (DonThuocThuoc dtt : donThuocThuocs) {
			BigDecimal lieu = new BigDecimal(dtt.getLieu());
			BigDecimal gia = dtt.getThuoc().getGia();
			total = total.add(lieu.multiply(gia));
		}
		return total;
	}

}
