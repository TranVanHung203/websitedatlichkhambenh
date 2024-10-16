package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DonThuoc")
public class DonThuoc implements Serializable {
    @Id
    @Column(name = "don_thuoc_id", length = 36)
    private String donThuocId;

    @ManyToOne
    @JoinColumn(name = "ho_so_id")
    private HoSoBenh hoSoBenh;

    @ManyToOne
    @JoinColumn(name = "thuoc_id")
    private Thuoc thuoc;

    @Column(name = "liều", nullable = false, length = 100,columnDefinition = "nvarchar(100)")
    private String lieu;

    @Column(name = "tần_suất", nullable = false, length = 50,columnDefinition = "nvarchar(100)")
    private String tanSuat;

    @Column(name = "thời_gian", length = 50)
    private String thoiGian;
}