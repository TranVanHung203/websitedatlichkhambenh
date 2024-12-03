package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BenhNhanOfTaoDonThuocDTO {
    private String benhnhanid;  // Đổi sang String
    private String ten;
    private LocalDate ngaysinh;
    private String gioitinh;
}

