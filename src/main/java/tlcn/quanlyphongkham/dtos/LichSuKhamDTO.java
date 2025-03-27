package tlcn.quanlyphongkham.dtos;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LichSuKhamDTO {
    private String tenBacSi;
    private String ngayKham;
    private String chanDoan;
    private String thuoc;
    private String lieu;
    private String tanSuat;
    private String trieuChung;
    private BigDecimal tongTienThuoc;  // Thêm trường tổng tiền thuốc
    private String soLuong; 
    public String getFormattedTongTienThuoc() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTienThuoc) + " VNĐ";
    }
}