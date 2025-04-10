package tlcn.quanlyphongkham.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TinNhanDTO {
    private String noiDung;
    private boolean tuBenhNhan;
    private String benhNhanId;
    private String bacSiId;
    private LocalDateTime thoiGian;
}