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
    @Column(name = "chuyen_khoa_id", length = 36)
    private String chuyenKhoaId;

    @Column(name = "ten", nullable = false, length = 100,columnDefinition = "nvarchar(100)")
    private String ten;
}