package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LoaiXetNghiem")
public class LoaiXetNghiem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loai_xet_nghiem_id")
    private Long id;

    @Column(name = "ten_xet_nghiem", columnDefinition = "nvarchar(100)", nullable = false)
    private String tenXetNghiem;

    @Column(name = "gia", nullable = false)
    private Integer gia; // VNĐ
}