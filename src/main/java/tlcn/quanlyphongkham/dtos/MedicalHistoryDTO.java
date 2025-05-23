package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryDTO implements Serializable {
    private String hoSoId;
    private BenhNhanDTO benhNhan;
    private BacSiDTO bacSi;
    private String chanDoan;
    private String trieuChung;
    private Integer tongTien;
    private Boolean daThanhToan;
    private String thoiGianTao;
    private List<VitalSignsDTO> vitalSigns;
    private List<DonThuocDTO> donThuocs;
    private List<XetNghiemDTO> xetNghiems;
    private List<PhieuXetNghiemDTO> phieuXetNghiems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BenhNhanDTO implements Serializable {
        private String benhNhanId;
        private String ten;
        private String dienThoai;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BacSiDTO implements Serializable {
        private String bacSiId;
        private String ten;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VitalSignsDTO implements Serializable {
        private Long id;
        private Float temperature;
        private Float height;
        private Float weight;
        private Integer bloodPressureSys;
        private Integer bloodPressureDia;
        private String notes;
        private String thoiGianTao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonThuocDTO implements Serializable {
        private Long donThuocId;
        private String formattedTongTienThuoc;
        private List<DonThuocThuocDTO> donThuocThuocs;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonThuocThuocDTO implements Serializable {
        private ThuocDTO thuoc;
        private String lieu;
        private String tanSuat;
        private int soLuong;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThuocDTO implements Serializable {
        private String tenThuoc;
        private BigDecimal gia;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class XetNghiemDTO implements Serializable {
        private LoaiXetNghiemDTO loaiXetNghiem;
        private String ghiChu;
        private String trangThai;
        private String fileKetQua;
        private String thoiGianTao;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoaiXetNghiemDTO implements Serializable {
        private String tenXetNghiem;
        private Integer gia;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhieuXetNghiemDTO implements Serializable {
        private String maPhieu;
        private Integer tongGia;
        private String thoiGianTao;
        private List<Long> xetNghiemIds;
    }
}