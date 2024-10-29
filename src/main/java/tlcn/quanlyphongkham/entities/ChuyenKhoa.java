package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ChuyenKhoa")
public class ChuyenKhoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự tăng
    @Column(name = "chuyen_khoa_id")
    private Long chuyenKhoaId;

    @Column(name = "ten", nullable = false, length = 100, columnDefinition = "nvarchar(100)")
    private String ten;
}
