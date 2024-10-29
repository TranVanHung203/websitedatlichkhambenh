package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Thuoc")
public class Thuoc implements Serializable {
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
}