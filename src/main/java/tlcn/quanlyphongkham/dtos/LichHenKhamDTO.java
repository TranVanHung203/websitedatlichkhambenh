package tlcn.quanlyphongkham.dtos;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tlcn.quanlyphongkham.entities.SlotThoiGian;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LichHenKhamDTO {
    private String maLichKhamBenh;
    private LocalDate ngayThangNam;
    private String ca;
    private List<SlotThoiGian> slotThoiGianList;
}
