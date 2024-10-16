package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "QuanTriVien")
public class QuanTriVien implements Serializable {
    @Id
    @Column(name = "quan_tri_id", length = 36)
    private String quanTriId;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;
}