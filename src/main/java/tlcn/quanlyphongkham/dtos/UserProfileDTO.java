package tlcn.quanlyphongkham.dtos;

import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.NguoiDung;

public class UserProfileDTO {
    private NguoiDung nguoiDung;
    private BenhNhan benhNhan;

    public UserProfileDTO(NguoiDung nguoiDung, BenhNhan benhNhan) {
        this.nguoiDung = nguoiDung;
        this.benhNhan = benhNhan;
    }

    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }
}
