package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoSoBenhDTO {
    private String hoSoId;
    private String tenBenhNhan;
    private String chanDoan;
    private LocalDateTime thoiGianTao;
    private String tenThuoc;
}
