package tlcn.quanlyphongkham.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        
        switch (period.toLowerCase()) {
            case "day":
                if (date != null) {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
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
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
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
                if (month != null) {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
                        .filter(slot -> (slot.getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                            String.format("%02d", slot.getLichKhamBenh().getNgayThangNam().getMonthValue())).equals(month))
                        .collect(Collectors.groupingBy(
                            slot -> slot.getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                                String.format("%02d", slot.getLichKhamBenh().getNgayThangNam().getMonthValue()),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((monthKey, statusMap) -> result.put(monthKey, statusMap));
                } else {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
                        .collect(Collectors.groupingBy(
                            slot -> slot.getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                                String.format("%02d", slot.getLichKhamBenh().getNgayThangNam().getMonthValue()),
                            Collectors.groupingBy(
                                slot -> slot.getTrangThai(),
                                Collectors.counting()
                            )
                        ))
                        .forEach((monthKey, statusMap) -> result.put(monthKey, statusMap));
                }
                break;
                
            case "year":
                if (year != null) {
                    slotThoiGianRepository.findAll().stream()
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
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
                        .filter(slot -> slot.getLichKhamBenh().getNgayThangNam() != null)
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
        
        switch (period.toLowerCase()) {
            case "day":
                if (date != null) {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString().equals(date))
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((dateKey, total) -> result.put(dateKey, total));
                } else {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().toString(),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((dateKey, total) -> result.put(dateKey, total));
                }
                break;
                
            case "month":
                if (month != null) {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> (hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                            String.format("%02d", hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getMonthValue())).equals(month))
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                                String.format("%02d", hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getMonthValue()),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((monthKey, total) -> result.put(monthKey, total));
                } else {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear() + "-" + 
                                String.format("%02d", hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getMonthValue()),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((monthKey, total) -> result.put(monthKey, total));
                }
                break;
                
            case "year":
                if (year != null) {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
                        .filter(hsb -> String.valueOf(hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear()).equals(year))
                        .filter(hsb -> hsb.getTongTien() != null)
                        .collect(Collectors.groupingBy(
                            hsb -> String.valueOf(hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam().getYear()),
                            Collectors.summingLong(hsb -> hsb.getTongTien().longValue())
                        ))
                        .forEach((yearKey, total) -> result.put(yearKey, total));
                } else {
                    hoSoBenhRepository.findAll().stream()
                        .filter(hsb -> hsb.getSlotThoiGian() != null && hsb.getSlotThoiGian().getLichKhamBenh().getNgayThangNam() != null)
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

    public Map<String, Long> getTestUsageStatistics(String period, String date, String month, String year, String sortOrder) {
        Map<String, Long> result = new HashMap<>();
        List<Object[]> testUsage;

        switch (period.toLowerCase()) {
            case "day":
                testUsage = xetNghiemRepository.findTestUsageByDay(date != null ? LocalDate.parse(date) : null);
                break;
            case "month":
                testUsage = xetNghiemRepository.findTestUsageByMonth(month);
                break;
            case "year":
                testUsage = xetNghiemRepository.findTestUsageByYear(year);
                break;
            default:
                return result;
        }

        for (Object[] row : testUsage) {
            String testType = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            result.put(testType, count);
        }

        return sortOrder.equalsIgnoreCase("asc") ?
            result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                )) :
            result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }

    public Map<String, Map<String, Number>> getRevisitRateStatistics(String period, String date, String month, String year) {
        Map<String, Map<String, Number>> result = new HashMap<>();
        List<Object[]> revisitData;

        switch (period.toLowerCase()) {
            case "day":
                revisitData = hoSoBenhRepository.findRevisitRateByDay(date != null ? LocalDate.parse(date) : null);
                break;
            case "month":
                revisitData = hoSoBenhRepository.findRevisitRateByMonth(month);
                break;
            case "year":
                revisitData = hoSoBenhRepository.findRevisitRateByYear(year);
                break;
            default:
                return result;
        }

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
            result.put(timePeriod, stats);
        }

        return result;
    }
}