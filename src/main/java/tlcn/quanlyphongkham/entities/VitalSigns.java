package tlcn.quanlyphongkham.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "VitalSigns")
public class VitalSigns implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_signs_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ho_so_id", nullable = false)
    private HoSoBenh hoSoBenh;

    @Column(name = "temperature")
    private Float temperature; // Nhiệt độ (°C)

    @Column(name = "height")
    private Float height; // Chiều cao (cm)

    @Column(name = "weight")
    private Float weight; // Cân nặng (kg)

    @Column(name = "blood_pressure_sys")
    private Integer bloodPressureSys; // Huyết áp tâm thu (mmHg)

    @Column(name = "blood_pressure_dia")
    private Integer bloodPressureDia; // Huyết áp tâm trương (mmHg)

    @Column(name = "notes", columnDefinition = "nvarchar(255)")
    private String notes; // Ghi chú

    @Column(name = "thoi_gian_tao", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime thoiGianTao;

    @PrePersist
    public void prePersist() {
        if (this.thoiGianTao == null) {
            this.thoiGianTao = LocalDateTime.now().withNano(0);
        }
    }
}