package tlcn.quanlyphongkham.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tlcn.quanlyphongkham.entities.HoSoBenh;
import tlcn.quanlyphongkham.entities.VitalSigns;
import tlcn.quanlyphongkham.repositories.HoSoBenhRepository;
import tlcn.quanlyphongkham.repositories.VitalSignsRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class VitalSignsService {

    @Autowired
    private VitalSignsRepository vitalSignsRepository;

    @Autowired
    private HoSoBenhRepository hoSoBenhRepository;

    public void saveVitalSigns(String hoSoId, Map<String, Object> data) {
        HoSoBenh hoSoBenh = hoSoBenhRepository.findById(hoSoId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ bệnh: " + hoSoId));
        
        hoSoBenh.setTrieuChung(parseString(data.get("trieuChung")));
        hoSoBenhRepository.save(hoSoBenh);
        // Kiểm tra xem đã có VitalSigns chưa
        Optional<VitalSigns> existingVitalSigns = vitalSignsRepository.findByHoSoBenhHoSoId(hoSoId);
        VitalSigns vitalSigns = existingVitalSigns.orElse(new VitalSigns());
        
        vitalSigns.setHoSoBenh(hoSoBenh);
        vitalSigns.setTemperature(parseFloat(data.get("nhietDo")));
        vitalSigns.setHeight(parseFloat(data.get("chieuCao")));
        vitalSigns.setWeight(parseFloat(data.get("canNang")));
        vitalSigns.setBloodPressureSys(parseInteger(data.get("bloodPressureSys")));
        vitalSigns.setBloodPressureDia(parseInteger(data.get("bloodPressureDia")));
        vitalSigns.setNotes(parseString(data.get("notes")));

        vitalSignsRepository.save(vitalSigns);
    }

    public Optional<VitalSigns> getVitalSignsByHoSoId(String hoSoId) {
        return vitalSignsRepository.findByHoSoBenhHoSoId(hoSoId);
    }

    private Float parseFloat(Object value) {
        if (value == null || value.toString().isEmpty()) return null;
        try {
            return Float.parseFloat(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(Object value) {
        if (value == null || value.toString().isEmpty()) return null;
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String parseString(Object value) {
        return value != null && !value.toString().isEmpty() ? value.toString() : null;
    }
}