package tlcn.quanlyphongkham.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.entities.SucKhoeDuLieu;
import tlcn.quanlyphongkham.services.NguoiDungService;
import tlcn.quanlyphongkham.services.SucKhoeDuLieuService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

@Controller
public class SucKhoeDuLieuController {
    @Autowired
    private SucKhoeDuLieuService service;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private JavaMailSender mailSender;

    private static final List<String> VALID_CHI_SO = Arrays.asList("huyetAp", "duongHuyet", "canNang");

    @GetMapping("/health-chart")
    public String showHealthChart(Model model, @RequestParam(defaultValue = "huyetAp") String chiSo,
                                  @RequestParam(defaultValue = "7days") String time) {
        System.out.println("GET request: chiSo=" + chiSo + ", time=" + time);
        if (!VALID_CHI_SO.contains(chiSo)) {
            model.addAttribute("error", "Chỉ số không hợp lệ: " + chiSo);
            chiSo = "huyetAp";
        }
        model.addAttribute("sucKhoeDuLieu", new SucKhoeDuLieu());
        model.addAttribute("chiSo", chiSo);
        model.addAttribute("time", time);
        List<SucKhoeDuLieu> data = service.findByNguoiDungIdAndChiSo(chiSo, time);
        model.addAttribute("data", data);
        String warning = data.isEmpty() ? null : service.checkAbnormal(data.get(data.size() - 1), data);
        model.addAttribute("warning", warning);
        if (data.isEmpty()) {
            model.addAttribute("info", "Không có dữ liệu để hiển thị biểu đồ. Vui lòng nhập dữ liệu.");
        }
        return "benhnhan/suckhoedulieu/suckhoedulieu";
    }

    @PostMapping("/health-chart")
    public String saveHealthData(@ModelAttribute SucKhoeDuLieu sucKhoeDuLieu,
                                 @RequestParam String chiSo,
                                 @RequestParam String time,
                                 RedirectAttributes redirectAttributes) {
        System.out.println("POST request: chiSo=" + chiSo + ", time=" + time +
                ", sucKhoeDuLieu=" + sucKhoeDuLieu);
        if (!VALID_CHI_SO.contains(chiSo)) {
            redirectAttributes.addFlashAttribute("error", "Chỉ số không hợp lệ: Vui lòng chọn Huyết áp, Đường huyết hoặc Cân nặng.");
            return "redirect:/health-chart?chiSo=huyetAp&time=" + time;
        }
        try {
            sucKhoeDuLieu.setChiSo(chiSo);
            SucKhoeDuLieu savedData = service.save(sucKhoeDuLieu);
            List<SucKhoeDuLieu> recentData = service.findByNguoiDungIdAndChiSo(chiSo, "30days");
            String warning = service.checkAbnormal(savedData, recentData);
            if (warning != null) {
                redirectAttributes.addFlashAttribute("warning", warning);
                try {
                    String nguoiDungId = service.getCurrentNguoiDungId();
                    NguoiDung nguoiDung = nguoiDungService.findById(nguoiDungId);
                    if (nguoiDung == null || nguoiDung.getEmail() == null || nguoiDung.getEmail().isEmpty()) {
                        throw new IllegalStateException("Email người dùng không được cấu hình");
                    }
                    String userEmail = nguoiDung.getEmail();

                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                    String subject = "Cảnh báo Sức khỏe: " + switch (chiSo) {
                        case "huyetAp" -> "Huyết áp";
                        case "duongHuyet" -> "Đường huyết";
                        case "canNang" -> "Cân nặng";
                        default -> "Sức khỏe";
                    };

                    StringBuilder body = new StringBuilder();
                    body.append("<h2>Cảnh báo Sức khỏe</h2>")
                        .append("<p><strong>").append(warning).append("</strong></p>")
                        .append("<p>Thông tin chi tiết:</p>")
                        .append("<ul>")
                        .append("<li><strong>Chỉ số</strong>: ").append(chiSo.equals("huyetAp") ? "Huyết áp" : chiSo.equals("duongHuyet") ? "Đường huyết" : "Cân nặng").append("</li>");

                    if (chiSo.equals("huyetAp")) {
                        body.append("<li><strong>Tâm thu</strong>: ").append(savedData.getTamThu()).append(" mmHg</li>")
                            .append("<li><strong>Tâm trương</strong>: ").append(savedData.getTamTruong()).append(" mmHg</li>");
                    } else if (chiSo.equals("duongHuyet")) {
                        body.append("<li><strong>Đường huyết</strong>: ").append(savedData.getDuongHuyet()).append(" mg/dL</li>")
                            .append("<li><strong>Thời điểm</strong>: ").append(savedData.getThoiDiemDuongHuyet() != null ? savedData.getThoiDiemDuongHuyet() : "Không xác định").append("</li>");
                    } else if (chiSo.equals("canNang")) {
                        body.append("<li><strong>Cân nặng</strong>: ").append(savedData.getCanNang()).append(" kg</li>");
                    }

                    body.append("<li><strong>Thời gian</strong>: ").append(savedData.getThoiGian()).append("</li>")
                        .append("</ul>")
                        .append("<p>Vui lòng liên hệ bác sĩ để được tư vấn thêm.</p>")
                        .append("<p>Trân trọng");

                    helper.setTo(userEmail);
                    helper.setSubject(subject);
                    helper.setText(body.toString(), true);
                    helper.setFrom("21110889@student.hcmute.edu.vn"); // Thay bằng email của bạn

                    mailSender.send(message);
                    System.out.println("Sent email to: " + userEmail + ", subject: " + subject);
                } catch (MessagingException | IllegalStateException e) {
                    System.err.println("Failed to send email: " + e.getMessage());
                    redirectAttributes.addFlashAttribute("error", "Lưu dữ liệu thành công nhưng không thể gửi email cảnh báo.");
                }
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/health-chart?chiSo=" + chiSo + "&time=" + time;
        }
        return "redirect:/health-chart?chiSo=" + chiSo + "&time=" + time;
    }
}