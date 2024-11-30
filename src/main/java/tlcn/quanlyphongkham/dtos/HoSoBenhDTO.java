package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoSoBenhDTO {
	private String hoSoId;
    private String tenBenhNhan;
    private String chanDoan;
    private String thoiGianTao;
    private String tenThuoc;
    private String lieu;
    private String tanSuat;
    private BigDecimal tongTienThuoc;
}
