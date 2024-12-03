package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "DonThuocThuoc")
public class DonThuocThuoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_thuoc_id")
    private DonThuoc donThuoc;

    @ManyToOne
    @JoinColumn(name = "thuoc_id")
    private Thuoc thuoc;

    // Thêm trường lieu và tanSuat
    @Column(name = "lieu")
    private String lieu;

    @Column(name = "tan_suat")
    private String tanSuat;
}
