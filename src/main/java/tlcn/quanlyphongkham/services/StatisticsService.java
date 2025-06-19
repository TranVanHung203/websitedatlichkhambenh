package tlcn.quanlyphongkham.services;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.SlotThoiGianRepository;
import tlcn.quanlyphongkham.repositories.XetNghiemRepository;

@Service
public class StatisticsService {

    @Autowired
    private SlotThoiGianRepository slotThoiGianRepository;

    @Autowired
    private HoSoBenhRepository hoSoBenhRepository;

    @Autowired
    private XetNghiemRepository xetNghiemRepository;

    public Map<String, Map<String, Long>> getAppointmentStatistics(String period, String date, String month, String year) {
        Map<String, Map<String, Long>> result = new HashMap<>();
        int selectedYear = year != null ? Integer.parseInt(year) : LocalDate.now().getYear();

        switch (period.toLowerCase()) {
            case "day":
                if (date != null) {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh() != null && slot.getLichKhamBenh().getNgayThangNam() != null)
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam().toString().equals(date))
                        .collect(Collectors.groupingBy(
                            slot -> slot.getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((dateKey, statusMap) -> result.put(dateKey, statusMap));
                } else {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh() != null && slot.getLichKhamBenh().getNgayThangNam() != null)
                        .collect(Collectors.groupingBy(
                            slot -> slot.getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((dateKey, statusMap) -> result.put(dateKey, statusMap));
                }
                break;

            case "month":
                Map<String, Map<String, Long>> monthlyData = slotThoiGianRepository.findAll().stream()
                    .filter(slot -> slot.getLichKhamBenh() != null && slot.getLichKhamBenh().getNgayThangNam() != null)
                    .filter(slot -> slot.getLichKhamBenh().getNgayThangNam().getYear() == selectedYear)
                    .collect(Collectors.groupingBy(
                        slot -> "Tháng " + slot.getLichKhamBenh().getNgayThangNam().getMonthValue() + "/" + selectedYear,
                        Collectors.groupingBy(
                            slot -> slot.getTrangThai(),
                            Collectors.counting()
                        )
                    ));

                // Fill data for all 12 months
                for (int i = 1; i <= 12; i++) {
                    String monthKey = "Tháng " + i + "/" + selectedYear;
                    result.put(monthKey, monthlyData.getOrDefault(monthKey, new HashMap<>()));
                    result.get(monthKey).putIfAbsent("pending", 0L);
                    result.get(monthKey).putIfAbsent("checked-in", 0L);
                    result.get(monthKey).putIfAbsent("completed", 0L);
                }
                break;

            case "year":
                if (year != null) {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh() != null && slot.getLichKhamBenh().getNgayThangNam() != null)
                        .filter(slot -> String.valueOf(slot.getLichKhamBenh().getNgayThangNam().getYear()).equals(year))
                        .collect(Collectors.groupingBy(
                            slot -> String.valueOf(slot.getLichKhamBenh().getNgayThangNam().getYear()),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((yearKey, statusMap) -> result.put(yearKey, statusMap));
                } else {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh() != null && slot.getLichKhamBenh().getNgayThangNam() != null)
                        .collect(Collectors.groupingBy(
                            slot -> String.valueOf(slot.getLichKhamBenh().getNgayThangNam().getYear()),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((yearKey, statusMap) -> result.put(yearKey, statusMap));
                }
                break;
        }

        return result;
    }

    public Map<String, Long> getRevenueStatistics(String period, String date, String month, String year) {
        Map<String, Long> result = new HashMap<>();
        int selectedYear = year != null ? Integer.parseInt(year) : LocalDate.now().getYear();

        switch (period.toLowerCase()) {
            case "day":
                if (date != null) {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString().equals(date))
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((dateKey, total) -> result.put(dateKey, total));
                } else {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((dateKey, total) -> result.put(dateKey, total));
                }
                break;

            case "month":
                Map<String, Long> monthlyRevenue = hoSoBenhRepository.findAll().stream()
                    .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                    .filter(hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear() == selectedYear)
                    .filter(hsb -> hsb.getTongTien() != null)
                    .collect(Collectors.groupingBy(
                        hsb -> "Tháng " + hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getMonthValue() + "/" + selectedYear,
                        Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                    ));

                // Fill data for all 12 months
                for (int i = 1; i <= 12; i++) {
                    String monthKey = "Tháng " + i + "/" + selectedYear;
                    result.put(monthKey, monthlyRevenue.getOrDefault(monthKey, 0L));
                }
                break;

            case "year":
                if (year != null) {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> String.valueOf(hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear()).equals(year))
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> String.valueOf(hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear()),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((yearKey, total) -> result.put(yearKey, total));
                } else {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> String.valueOf(hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear()),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((yearKey, total) -> result.put(yearKey, total));
                }
                break;
        }

        return result;
    }

    public Map<String, Map<String, Long>> getTestUsageStatistics(String period, String date, String month, String year) {
        Map<String, Map<String, Long>> result = new HashMap<>();
        int selectedYear = year != null ? Integer.parseInt(year) : LocalDate.now().getYear();

        switch (period.toLowerCase()) {
            case "day":
                List<Object[]> testUsage = xetNghiemRepository.findTestUsageByDay(date != null ? LocalDate.parse(date) : null);
                Map<String, Long> dayResult = new HashMap<>();
                for (Object[] row : testUsage) {
                    String testType = (String) row[0];
                    Long count = ((Number) row[1]).longValue();
                    dayResult.put(testType, count);
                }
                result.put(date != null ? date : "Today", dayResult);
                break;

            case "month":
                List<Object[]> testUsageByYear = xetNghiemRepository.findTestUsageByYearAndMonth(String.valueOf(selectedYear));
                Map<String, Map<String, Long>> monthlyTestUsage = new HashMap<>();
                for (Object[] row : testUsageByYear) {
                    String testType = (String) row[0];
                    Long count = ((Number) row[1]).longValue();
                    Integer month1 = ((Number) row[2]).intValue(); // Lấy tháng từ cột thứ 3
                    String monthKey = "Tháng " + month1 + "/" + selectedYear;
                    monthlyTestUsage.computeIfAbsent(monthKey, k -> new HashMap<>()).merge(testType, count, Long::sum);
                }
                // Điền dữ liệu cho 12 tháng
                for (int i = 1; i <= 12; i++) {
                    String monthKey = "Tháng " + i + "/" + selectedYear;
                    result.put(monthKey, monthlyTestUsage.getOrDefault(monthKey, new HashMap<>()));
                }
                break;

            case "year":
                testUsage = xetNghiemRepository.findTestUsageByYear(year);
                Map<String, Long> yearResult = new HashMap<>();
                for (Object[] row : testUsage) {
                    String testType = (String) row[0];
                    Long count = ((Number) row[1]).longValue();
                    yearResult.put(testType, count);
                }
                result.put(year != null ? year : String.valueOf(LocalDate.now().getYear()), yearResult);
                break;
        }

        return result;
    }

    public Map<String, Map<String, Number>> getRevisitRateStatistics(String period, String date, String month, String year) {
        Map<String, Map<String, Number>> result = new HashMap<>();
        List<Object[]> revisitData;
        int selectedYear = year != null ? Integer.parseInt(year) : LocalDate.now().getYear();

        switch (period.toLowerCase()) {
            case "day":
                revisitData = hoSoBenhRepository.findRevisitRateByDay(date != null ? LocalDate.parse(date) : null);
                break;

            case "month":
                // Sử dụng findRevisitRateByMonth với month = null để lấy tất cả các tháng
                revisitData = hoSoBenhRepository.findRevisitRateByMonth(null);
                Map<String, Map<String, Number>> tempResult = new HashMap<>();
                for (Object[] row : revisitData) {
                    String timePeriod = (String) row[0]; // Định dạng YYYY-MM
                    long totalPatients = ((Number) row[1]).longValue();
                    long revisitPatients = ((Number) row[2]).longValue();
                    long totalVisits = ((Number) row[3]).longValue();
                    double revisitRate = totalPatients > 0 ? (revisitPatients * 100.0 / totalPatients) : 0.0;
                    double averageRevisitCount = totalPatients > 0 ? (totalVisits * 1.0 / totalPatients) : 0.0;

                    // Chuyển đổi YYYY-MM thành Tháng X/YYYY
                    if (timePeriod != null && timePeriod.matches("\\d{4}-\\d{2}")) {
                        String[] parts = timePeriod.split("-");
                        int yearPart = Integer.parseInt(parts[0]);
                        int monthPart = Integer.parseInt(parts[1]);
                        if (yearPart == selectedYear) { // Chỉ lấy dữ liệu của năm được chọn
                            String monthKey = "Tháng " + monthPart + "/" + yearPart;
                            Map<String, Number> stats = new HashMap<>();
                            stats.put("revisitRate", revisitRate);
                            stats.put("totalPatients", totalPatients);
                            stats.put("revisitPatients", revisitPatients);
                            stats.put("averageRevisitCount", averageRevisitCount);
                            tempResult.put(monthKey, stats);
                        }
                    }
                }

                // Điền dữ liệu cho 12 tháng
                for (int i = 1; i <= 12; i++) {
                    String monthKey = "Tháng " + i + "/" + selectedYear;
                    result.put(monthKey, tempResult.getOrDefault(monthKey, new HashMap<>() {{
                        put("revisitRate", 0.0);
                        put("totalPatients", 0L);
                        put("revisitPatients", 0L);
                        put("averageRevisitCount", 0.0);
                    }}));
                }
                break;

            case "year":
                revisitData = hoSoBenhRepository.findRevisitRateByYear(year);
                break;

            default:
                return result;
        }

        if (!period.equalsIgnoreCase("month")) {
            Map<String, Map<String, Number>> tempResult = new HashMap<>();
            for (Object[] row : revisitData) {
                String timePeriod = (String) row[0];
                long totalPatients = ((Number) row[1]).longValue();
                long revisitPatients = ((Number) row[2]).longValue();
                long totalVisits = ((Number) row[3]).longValue();
                double revisitRate = totalPatients > 0 ? (revisitPatients * 100.0 / totalPatients) : 0.0;
                double averageRevisitCount = totalPatients > 0 ? (totalVisits * 1.0 / totalPatients) : 0.0;

                Map<String, Number> stats = new HashMap<>();
                stats.put("revisitRate", revisitRate);
                stats.put("totalPatients", totalPatients);
                stats.put("revisitPatients", revisitPatients);
                stats.put("averageRevisitCount", averageRevisitCount);
                tempResult.put(timePeriod, stats);
            }
            result.putAll(tempResult);
        }

        return result;
    }
}