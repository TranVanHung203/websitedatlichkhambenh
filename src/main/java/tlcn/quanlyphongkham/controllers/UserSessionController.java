package tlcn.quanlyphongkham.controllers;



import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.BenhNhan;
import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.security.CustomUserDetails;
import tlcn.quanlyphongkham.services.BacSiService;
import tlcn.quanlyphongkham.services.BenhNhanService;
import tlcn.quanlyphongkham.services.NguoiDungService;

@RestController
@RequestMapping("/api/user")
public class UserSessionController {
    @Autowired
    private NguoiDungService userDetailsService;
    @Autowired
    private BacSiService bacSiService;
    @Autowired
    private BenhNhanService benhNhanService;
	public String getNguoiDungId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();

			if (principal instanceof CustomUserDetails) {
				CustomUserDetails customUserDetails = (CustomUserDetails) principal;
				return customUserDetails.getNguoiDungId(); // Get the NguoiDungId as String

			} else
				return null;
		} else
			return null;
	}
	String nguoiDungId;
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication auth) {
    	nguoiDungId = getNguoiDungId();
        NguoiDung user = userDetailsService.findById(nguoiDungId);

        if (user.getVaiTro().isEmpty()) return ResponseEntity.status(401).build();

 
        Map<String, Object> data = new HashMap<>();

        if ("BenhNhan".equals(user.getVaiTro())) {
            data.put("isBenhNhan", true);
            BenhNhan benhnhan= benhNhanService.findById(nguoiDungId);
            data.put("id", benhnhan.getBenhNhanId());
           
        } else {
            data.put("isBenhNhan", false);
            BacSi bacsi= bacSiService.findByNguoiDungId(nguoiDungId);
            data.put("id", bacsi.getBacSiId());
        }

        return ResponseEntity.ok(data);
    }
}
