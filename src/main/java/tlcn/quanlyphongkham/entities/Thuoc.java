package tlcn.quanlyphongkham.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"donThuocThuocs"})
@Table(name = "Thuoc")
public class Thuoc implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "thuoc_id", length = 36)
    private Long thuocId;

    @Column(name = "ten", nullable = false, length = 100)
    private String ten;

    @Column(name = "mo_ta", columnDefinition = "nvarchar(100)")
    private String moTa;

    @Column(name = "gia", nullable = false)
    private BigDecimal gia;

    @OneToMany(mappedBy = "thuoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DonThuocThuoc> donThuocThuocs;
}
