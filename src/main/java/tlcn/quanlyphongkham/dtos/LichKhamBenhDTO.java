package tlcn.quanlyphongkham.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichKhamBenhDTO {
    private String maLichKhamBenh;
    private String bacSiId;         // Mã bác sĩ
    private String tenBacSi;         // Tên bác sĩ
    private String chuyenKhoaId;     // Mã chuyên khoa
    private String tenChuyenKhoa;    // Tên chuyên khoa
    private String caKham;           // Ca khám bệnh (sáng, chiều, tối,...)
}
