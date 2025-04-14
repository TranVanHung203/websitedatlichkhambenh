package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TinNhan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String noiDung;

    @Column(nullable = false)
    private LocalDateTime thoiGian;

    @Column(nullable = false)
    private boolean tuBenhNhan; // true = bệnh nhân gửi, false = bác sĩ gửi

    @Column(nullable = false)
    private String benhNhanId;

    @Column(nullable = false)
    private String bacSiId;
}