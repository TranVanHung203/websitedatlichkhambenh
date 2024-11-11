package tlcn.quanlyphongkham.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietBacSiDTO {
    private String bacSiId;
    private String bangCap;
    private String hoiNghiNuocNgoai;
    private String chungChi;
    private String daoTaoChuyenNganh;
    private String linhVucChuyenSau;
    private String gioiThieu;
}
