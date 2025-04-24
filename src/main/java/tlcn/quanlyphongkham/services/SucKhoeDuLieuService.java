package tlcn.quanlyphongkham.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.entities.SucKhoeDuLieu;
import tlcn.quanlyphongkham.repositories.SucKhoeDuLieuRepository;
import tlcn.quanlyphongkham.security.CustomUserDetails;

@Service
public class SucKhoeDuLieuService {
    @Autowired
    private SucKhoeDuLieuRepository repository;

    private static final List<String> VALID_CHI_SO = Arrays.asList("huyetAp", "duongHuyet", "canNang");

    public String getCurrentNguoiDungId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                return customUserDetails.getNguoiDungId();
            }
        }
        throw new RuntimeException("Không tìm thấy nguoiDungId");
    }

    public SucKhoeDuLieu save(SucKhoeDuLieu data) {
        if (!VALID_CHI_SO.contains(data.getChiSo())) {
            System.err.println("Lỗi: Chi so không hợp lệ: " + data.getChiSo());
            throw new IllegalArgumentException("Chỉ số không hợp lệ: Vui lòng chọn Huyết áp, Đường huyết hoặc Cân nặng.");
        }
        if (data.getChiSo().equals("huyetAp") && (data.getTamThu() == null || data.getTamTruong() == null)) {
            throw new IllegalArgumentException("Huyết áp yêu cầu tâm thu và tâm trương");
        }
        if (data.getChiSo().equals("duongHuyet") && data.getDuongHuyet() == null) {
            throw new IllegalArgumentException("Đường huyết yêu cầu giá trị đường huyết");
        }
        if (data.getChiSo().equals("canNang") && data.getCanNang() == null) {
            throw new IllegalArgumentException("Cân nặng yêu cầu giá trị cân nặng");
        }
        System.out.println("Saving data: chi_so=" + data.getChiSo() + ", tamThu=" + data.getTamThu() +
                ", tamTruong=" + data.getTamTruong() + ", duongHuyet=" + data.getDuongHuyet() +
                ", canNang=" + data.getCanNang() + ", nguoiDungId=" + data.getNguoiDungId());
        data.setNguoiDungId(getCurrentNguoiDungId());
        if (data.getThoiGian() == null) {
            data.setThoiGian(LocalDateTime.now());
        }
        return repository.save(data);
    }

    public List<SucKhoeDuLieu> findByNguoiDungIdAndChiSo(String chiSo, String timeRange) {
        if (!VALID_CHI_SO.contains(chiSo)) {
            throw new IllegalArgumentException("Chỉ số không hợp lệ: " + chiSo);
        }
        String nguoiDungId = getCurrentNguoiDungId();
        LocalDateTime startTime = LocalDateTime.now();
        switch (timeRange) {
            case "7days":
                startTime = startTime.minusDays(7);
                break;
            case "30days":
                startTime = startTime.minusDays(30);
                break;
            case "90days":
                startTime = startTime.minusDays(90);
                break;
            default:
                startTime = LocalDateTime.now().minusYears(1);
        }
        List<SucKhoeDuLieu> result = repository.findByNguoiDungIdAndChiSoAndThoiGianAfterOrderByThoiGianAsc(nguoiDungId, chiSo, startTime);
        System.out.println("findByNguoiDungIdAndChiSo: chiSo=" + chiSo + ", timeRange=" + timeRange + ", nguoiDungId=" + nguoiDungId + ", result.size=" + result.size());
        return result;
    }

    public String checkAbnormal(SucKhoeDuLieu data, List<SucKhoeDuLieu> recentData) {
        System.out.println("Checking abnormal: chi_so=" + data.getChiSo() + ", data=" + data + ", recentData.size=" + (recentData != null ? recentData.size() : 0));
        if (data == null || data.getChiSo() == null) {
            System.out.println("Data or chiSo is null");
            return null;
        }
        if (data.getChiSo().equals("huyetAp")) {
            if (data.getTamThu() != null && data.getTamTruong() != null) {
                if (data.getTamThu() > 140 || data.getTamTruong() > 90) {
                    System.out.println("Huyet ap warning: tamThu=" + data.getTamThu() + ", tamTruong=" + data.getTamTruong());
                    return "Huyết áp của bạn cao! Hãy liên hệ bác sĩ.";
                }
            }
        } else if (data.getChiSo().equals("duongHuyet")) {
            if (data.getDuongHuyet() != null) {
                if (data.getDuongHuyet() < 70) {
                    System.out.println("Duong huyet low warning: duongHuyet=" + data.getDuongHuyet());
                    return "Đường huyết của bạn thấp! Hãy kiểm tra ngay.";
                } else if (data.getDuongHuyet() > 180) {
                    System.out.println("Duong huyet high warning: duongHuyet=" + data.getDuongHuyet());
                    return "Đường huyết của bạn cao! Hãy liên hệ bác sĩ.";
                }
            }
        } else if (data.getChiSo().equals("canNang")) {
            if (data.getCanNang() != null && recentData != null && !recentData.isEmpty()) {
                SucKhoeDuLieu lastValidRecord = recentData.stream()
                        .filter(d -> d.getCanNang() != null && !d.getId().equals(data.getId()))
                        .reduce((first, second) -> second)
                        .orElse(null);
                if (lastValidRecord != null) {
                    double lastWeight = lastValidRecord.getCanNang();
                    System.out.println("Comparing canNang: current=" + data.getCanNang() + ", last=" + lastWeight + ", diff=" + Math.abs(data.getCanNang() - lastWeight));
                    if (Math.abs(data.getCanNang() - lastWeight) > 5) {
                        return "Cân nặng của bạn biến động mạnh! Hãy kiểm tra chế độ ăn uống.";
                    }
                } else {
                    System.out.println("No valid previous canNang record found");
                }
            } else {
                System.out.println("canNang or recentData is null/empty: canNang=" + data.getCanNang() + ", recentData=" + (recentData != null ? recentData.size() : "null"));
            }
        }
        return null;
    }
}