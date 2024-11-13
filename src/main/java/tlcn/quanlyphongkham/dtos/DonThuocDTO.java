package tlcn.quanlyphongkham.dtos;

import lombok.Data;
import java.util.List;

@Data
public class DonThuocDTO {
    private String hoSoId; // ID của hồ sơ bệnh (bệnh nhân và bác sĩ đã được chọn)
    private String chanDoan; // Chuẩn đoán
    private List<ThuocDTO> thuocList; // Danh sách thuốc
}