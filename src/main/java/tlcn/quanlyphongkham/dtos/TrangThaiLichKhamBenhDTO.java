package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiLichKhamBenhDTO {
    private String maLichKhamBenh;
    private String bacSiId; // Only include bacSiId to avoid recursion
    private String ca;
    private Boolean trangThai;
}