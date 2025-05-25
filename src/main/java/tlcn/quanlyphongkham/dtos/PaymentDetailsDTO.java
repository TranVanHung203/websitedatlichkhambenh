package tlcn.quanlyphongkham.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaymentDetailsDTO {
    private String hoSoId;
    private String tenBenhNhan;
    private String tenBacSi;
    private BigDecimal tongTien;
    private String chanDoan;
    private String trieuChung;
    private List<String> donThuocIds = new ArrayList<>();
    private List<Thuoc> thuocList = new ArrayList<>();
    private List<String> xetNghiemIds = new ArrayList<>();
    private List<XetNghiem> xetNghiemList = new ArrayList<>();
    private Boolean daThanhToan; // Thêm cột này

    @Data
    public static class Thuoc {
        private String donThuocId;
        private String tenThuoc;
        private Integer soLuong;
        private BigDecimal gia;
        private String lieu;
        private String tanSuat;
    }

    @Data
    public static class XetNghiem {
        private String xetNghiemId;
        private String tenXetNghiem;
        private Integer gia;
    }
}