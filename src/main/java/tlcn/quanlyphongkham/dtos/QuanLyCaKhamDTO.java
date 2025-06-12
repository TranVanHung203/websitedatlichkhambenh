package tlcn.quanlyphongkham.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class QuanLyCaKhamDTO {
    private String bacSiId; // Chỉ lưu ID của bác sĩ
    private String ca;
    private LocalDate ngayThangNam;
}
