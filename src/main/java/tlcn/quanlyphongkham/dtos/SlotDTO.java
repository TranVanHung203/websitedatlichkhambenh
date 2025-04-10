package tlcn.quanlyphongkham.dtos;



import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {
    private String slotId;
    private LocalTime thoiGianBatDau;
    private String trangThai;
}

