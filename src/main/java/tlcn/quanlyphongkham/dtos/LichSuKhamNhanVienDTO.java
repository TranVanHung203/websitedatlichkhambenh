package tlcn.quanlyphongkham.dtos;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class LichSuKhamNhanVienDTO {
	private String tenBacSi;
    private String tenBenhNhan;
    private String dienThoai; 
    private String ngayKham;
    private String chanDoan;
    private String trieuChung;
    private String thuoc;
    private String lieu;
    private String tanSuat;
    private String soLuong;
    private BigDecimal tongTienThuoc;
    
    public String getFormattedTongTienThuoc() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTienThuoc) + " VNĐ";
    }
}
