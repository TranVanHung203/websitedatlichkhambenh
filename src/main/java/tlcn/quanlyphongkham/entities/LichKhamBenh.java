package tlcn.quanlyphongkham.entities;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "LichKhamBenh")
public class LichKhamBenh {

    @Id
    @Column(name = "ma_lich_id", length = 36)
    private String maLichKhamBenh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false)
    @JsonIgnore
    private BacSi bacSi;

    private LocalDate ngayThangNam;

    @Column(name = "ca", nullable = false, length = 20, columnDefinition = "nvarchar(100)")
    private String ca;

  

    @OneToMany(mappedBy = "lichKhamBenh", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SlotThoiGian> slotThoiGian;

 
}