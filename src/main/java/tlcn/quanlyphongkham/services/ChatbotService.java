package tlcn.quanlyphongkham.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.LichKhamBenh;
import tlcn.quanlyphongkham.entities.SlotThoiGian;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.LichKhamBenhRepository;
import tlcn.quanlyphongkham.repositories.SlotThoiGianRepository;

@Service
public class ChatbotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatbotService.class);

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    private final BacSiRepository bacSiRepository;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    @Autowired
    private LichKhamBenhRepository lichKhamBenhRepository;

    @Autowired
    private SlotThoiGianRepository slotThoiGianRepository;
    @Autowired
    public ChatbotService(BacSiRepository bacSiRepository, ObjectMapper objectMapper) {
        this.bacSiRepository = bacSiRepository;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @PostConstruct
    public void init() {
        logger.info("ChatbotService initialized with Gemini API key");
    }

    public String handleUserInput(String userInput) {
        String input = userInput.toLowerCase().trim();

        // Xử lý lời chào
        if (input.matches("^(chào|xin chào|hello|hi)$")) {
            return "Chào bạn! Tôi là trợ lý y tế. Bạn cần tôi hỗ trợ gì liên quan đến sức khỏe hôm nay?";
        }
        if (input.contains("lịch khám")) {
        	List<BacSi> bacSis = bacSiRepository.findAll();
            String bacSiInfo = bacSis.stream()
                    .map(this::formatBacSiInfo)
                    .collect(Collectors.joining("\n\n"));

            String prompt = "Bạn là một trợ lý ảo chuyên tư vấn **về sức khỏe và y tế** cho bệnh nhân.\n\n"
                    + "Dưới đây là danh sách bác sĩ hiện có trong hệ thống hãy xem và ghi nhớ thật kỹ,hãy thống kê số lượng để không bỏ sót dữ liệu:\n\n"
                    + bacSiInfo+ "\n\n Duới đây là thông tin lịch khám,ca sáng bắt đầu từ 	7h30 - 11h30 là kết thúc , ca chiều là từ 13h - 16h30, ca ngoài giờ là từ 17h - 19h30 mỗi 30 phút là một ca VD 7h30-8h là một ca, ca tiếp theo là 8h-8h30"              
                    +" \"Sáng\": [\"07:30\", \"08:00\", \"08:30\", \"09:00\", \"09:30\", \"10:00\", \"10:30\", \"11:00\"],\r\n"
                    + "                \"Chiều\": [\"13:00\", \"13:30\", \"14:00\", \"14:30\", \"15:00\", \"15:30\", \"16:00\"],\r\n"
                    + "                \"Ngoài Giờ\": [\"17:00\", \"17:30\", \"18:00\", \"18:30\", \"19:00\"]"
                    +"ca nào nào trong danh sách booked tức là đã có người đặt thì không hiển thị cho người dùng xem và bảo ca đó đã có người đặt"
                    +getAllDoctorsSchedulesForCurrentMonth()
                    + "\n\nNgười dùng vừa hỏi: \"" + userInput + "\"\n\n"
                    + "Vui lòng:\n"
                    + "- Trả lời ngắn gọn, dễ hiểu, lịch sự và chính xác. Khi tư vấn cho người bệnh bác sĩ trong một chuyên khoa rất nhiều, hãy đề xuất cho hết đừng có một người mà đề xuất miết, một lần để xuất 4- 5 người thôi\n"
                    + "- Chỉ trả lời các câu hỏi liên quan đến **lĩnh vực y tế** như: bệnh lý, khám chữa bệnh, bác sĩ, chi phí khám, triệu chứng, lời khuyên sức khỏe...\n"
                    + "- Có thể sử dụng thông tin từ danh sách bác sĩ ở trên nếu phù hợp.\n"
                    + "- Nếu câu hỏi không thuộc lĩnh vực y tế (ví dụ: thể thao, công nghệ, thời tiết, giải trí...), hãy trả lời rằng bạn chỉ hỗ trợ các vấn đề y tế và không thể tư vấn ngoài lĩnh vực.\n"
                    + "- Trình bày rõ ràng, có thể ngắt dòng nếu cần để dễ đọc. Hỏi cái gì thì trả lời cái đó nếu không biết thì bảo chưa được cung cấp, hiển thị cho đẹp, và phải đọc dữ liệu cho kỹ trước khi trả lời.\n\n"
                    + "Bắt đầu trả lời:";

            try {
                return callGeminiApi(prompt);
            } catch (Exception e) {
                logger.error("Lỗi khi gọi Gemini: {}", e.getMessage());
                return "Xin lỗi, hệ thống đang gặp sự cố khi tư vấn. Vui lòng thử lại sau.";
            }
        }

        // Mọi câu hỏi khác đều vào đây
        List<BacSi> bacSis = bacSiRepository.findAll();
        String bacSiInfo = bacSis.stream()
                .map(this::formatBacSiInfo)
                .collect(Collectors.joining("\n\n"));

        String prompt = "Bạn là một trợ lý ảo chuyên tư vấn **về sức khỏe và y tế** cho bệnh nhân.\n\n"
                + "Dưới đây là danh sách bác sĩ hiện có trong hệ thống hãy xem và ghi nhớ thật kỹ,hãy thống kê số lượng để không bỏ sót dữ liệu:\n\n"
                + bacSiInfo
                + "\n\nNgười dùng vừa hỏi: \"" + userInput + "\"\n\n"
                + "Vui lòng:\n"
                + "- Trả lời ngắn gọn, dễ hiểu, lịch sự và chính xác. Khi tư vấn cho người bệnh bác sĩ trong một chuyên khoa rất nhiều, hãy đề xuất cho hết đừng có một người mà đề xuất miết, một lần để xuất 4- 5 người thôi\n"
                + "- Chỉ trả lời các câu hỏi liên quan đến **lĩnh vực y tế** như: bệnh lý, khám chữa bệnh, bác sĩ, chi phí khám, triệu chứng, lời khuyên sức khỏe...\n"
                + "- Có thể sử dụng thông tin từ danh sách bác sĩ ở trên nếu phù hợp.\n"
                + "- Nếu câu hỏi không thuộc lĩnh vực y tế (ví dụ: thể thao, công nghệ, thời tiết, giải trí...), hãy trả lời rằng bạn chỉ hỗ trợ các vấn đề y tế và không thể tư vấn ngoài lĩnh vực.\n"
                + "- Trình bày rõ ràng, có thể ngắt dòng nếu cần để dễ đọc. Hỏi cái gì thì trả lời cái đó nếu không biết thì bảo chưa được cung cấp, hiển thị cho đẹp, và phải đọc dữ liệu cho kỹ trước khi trả lời.\n\n"
                + "Bắt đầu trả lời:";

        try {
            return callGeminiApi(prompt);
        } catch (Exception e) {
            logger.error("Lỗi khi gọi Gemini: {}", e.getMessage());
            return "Xin lỗi, hệ thống đang gặp sự cố khi tư vấn. Vui lòng thử lại sau.";
        }
    }

    private String formatBacSiInfo(BacSi bs) {
        StringBuilder sb = new StringBuilder();
        // Thông tin cá nhân
        sb.append("- Tên bác sĩ: ").append(bs.getTen()).append("\n");
        sb.append("- id: ").append(bs.getBacSiId()).append("\n");
        sb.append("  Giới tính: ").append(bs.getGioiTinh() != null ? bs.getGioiTinh() : "Chưa cung cấp").append("\n");
        sb.append("  Ngày sinh: ").append(bs.getNgaySinh() != null ? bs.getNgaySinh() : "Chưa cung cấp").append("\n");
        sb.append("  Điện thoại: ").append(bs.getDienThoai() != null ? bs.getDienThoai() : "Chưa cung cấp").append("\n");
        
        sb.append("  URL Avatar: ").append(bs.getUrlAvatar() != null ? bs.getUrlAvatar() : "Chưa cung cấp").append("\n");

        // Thông tin chuyên khoa
        sb.append("  Chuyên khoa: ").append(bs.getChuyenKhoa().getTen()).append("\n");
        sb.append("  Mô tả chuyên khoa: ").append(bs.getChuyenKhoa().getMoTa() != null ? bs.getChuyenKhoa().getMoTa() : "Chưa cung cấp").append("\n");

        // Chi phí khám
        sb.append("  Giá khám: ").append(bs.getGiaKham()).append(" VNĐ").append("\n");

        // Thông tin chi tiết bác sĩ
        if (bs.getChiTietBacSi() != null) {
            sb.append("  Thông tin chi tiết:\n");
            sb.append("    Bằng cấp: ").append(bs.getChiTietBacSi().getBangCap() != null ? bs.getChiTietBacSi().getBangCap() : "Chưa cung cấp").append("\n");
            sb.append("    Hội nghị nước ngoài: ").append(bs.getChiTietBacSi().getHoiNghiNuocNgoai() != null ? bs.getChiTietBacSi().getHoiNghiNuocNgoai() : "Chưa cung cấp").append("\n");
            sb.append("    Chứng chỉ: ").append(bs.getChiTietBacSi().getChungChi() != null ? bs.getChiTietBacSi().getChungChi() : "Chưa cung cấp").append("\n");
            sb.append("    Đào tạo chuyên ngành: ").append(bs.getChiTietBacSi().getDaoTaoChuyenNganh() != null ? bs.getChiTietBacSi().getDaoTaoChuyenNganh() : "Chưa cung cấp").append("\n");
            sb.append("    Lĩnh vực chuyên sâu: ").append(bs.getChiTietBacSi().getLinhVucChuyenSau() != null ? bs.getChiTietBacSi().getLinhVucChuyenSau() : "Chưa cung cấp").append("\n");
            sb.append("    Giới thiệu: ").append(bs.getChiTietBacSi().getGioiThieu() != null ? bs.getChiTietBacSi().getGioiThieu() : "Chưa cung cấp").append("\n");
        } else {
            sb.append("  Thông tin chi tiết: Chưa cung cấp\n");
        }

        return sb.toString();
    }

    private String callGeminiApi(String fullPrompt) throws IOException, InterruptedException {
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, String>> parts = new ArrayList<>();
        parts.add(Map.of("text", fullPrompt));
        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", Map.of(
            "temperature", 0.7,
            "topK", 40,
            "topP", 0.95,
            "maxOutputTokens", 1024
        ));

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Gemini API lỗi: " + response.statusCode() + " - " + response.body());
        }

        JsonNode responseJson = objectMapper.readTree(response.body());
        JsonNode candidates = responseJson.get("candidates");
        if (candidates != null && candidates.isArray() && candidates.size() > 0) {
            JsonNode contentNode = candidates.get(0).get("content");
            if (contentNode != null) {
                JsonNode partsNode = contentNode.get("parts");
                if (partsNode != null && partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).get("text").asText();
                }
            }
        }
        throw new IOException("Phản hồi Gemini không hợp lệ.");
    }
    public Map<String, Object> getAllDoctorsSchedulesForCurrentMonth() {
        // Get current date and determine the month
        LocalDate currentDate = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(currentDate);
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        // Get all doctors
        List<BacSi> allDoctors = bacSiRepository.findAll();

        // Initialize result map
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> doctorSchedules = new ArrayList<>();

        // Process schedules for each doctor
        for (BacSi bacSi : allDoctors) {
            String doctorId = bacSi.getBacSiId();

            // Get all schedules for the doctor in the current month
            List<LichKhamBenh> lichKhamBenhList = lichKhamBenhRepository.findByBacSiIdAndNgayThangNamBetween(
                    doctorId, startOfMonth, endOfMonth);

            // Group schedules by date
            Map<LocalDate, List<LichKhamBenh>> schedulesByDate = lichKhamBenhList.stream()
                    .collect(Collectors.groupingBy(LichKhamBenh::getNgayThangNam));

            // Process each date for the doctor
            for (Map.Entry<LocalDate, List<LichKhamBenh>> entry : schedulesByDate.entrySet()) {
                LocalDate date = entry.getKey();
                List<LichKhamBenh> dailySchedules = entry.getValue();

                // Collect shifts (ca) for the date
                List<String> caList = dailySchedules.stream()
                        .map(LichKhamBenh::getCa)
                        .collect(Collectors.toList());

                // Collect booked slots for the date
                List<String> bookedSlots = new ArrayList<>();
                for (LichKhamBenh lichKhamBenh : dailySchedules) {
                    List<SlotThoiGian> slots = slotThoiGianRepository.findByLichKhamBenh_MaLichKhamBenh(
                            lichKhamBenh.getMaLichKhamBenh());
                    bookedSlots.addAll(slots.stream()
                            .map(slot -> slot.getThoiGianBatDau().toString())
                            .collect(Collectors.toList()));
                }

                // Create schedule entry for this doctor and date
                Map<String, Object> scheduleEntry = new HashMap<>();
                scheduleEntry.put("doctorId", doctorId);
                scheduleEntry.put("date", date);
                scheduleEntry.put("caList", caList);
                scheduleEntry.put("bookedSlots", bookedSlots);

                doctorSchedules.add(scheduleEntry);
            }
        }

        // Add the aggregated schedules to the result
        result.put("schedules", doctorSchedules);
        result.put("month", currentMonth.toString());

        return result;
    }
}