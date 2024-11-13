package tlcn.quanlyphongkham.dtos;

import lombok.Data;

@Data
public class ThuocDTO {
    private Long thuocId; // ID của thuốc
    private String lieu; // Liều dùng
    private String tanSuat; // Tần suất dùng
    private String thoiGian; // Thời gian dùng
}