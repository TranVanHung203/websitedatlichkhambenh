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
    private String tenXetNghiem; // Tên xét nghiệm
    private String giaXetNghiem; // Giá xét nghiệm
    private String fileKetQua; // File kết quả xét nghiệm
    private BigDecimal tongTienXetNghiem; // Tổng tiền xét nghiệm
    
    public String getFormattedTongTienThuoc() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTienThuoc) + " VNĐ";
    }

    public String getFormattedTongTienXetNghiem() {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTienXetNghiem) + " VNĐ";
    }

    public String getFormattedTongTienChung() {
        BigDecimal tongTienChung = tongTienThuoc.add(tongTienXetNghiem);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(tongTienChung) + " VNĐ";
    }
}
