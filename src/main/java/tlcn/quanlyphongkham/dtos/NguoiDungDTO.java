package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungDTO {
    private String nguoiDungId;
    private String tenDangNhap;
    private String email;
    private String vaiTro;
    private String trangthai;  // Thêm trạng thái vào DTO
}
