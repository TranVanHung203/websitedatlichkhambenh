package tlcn.quanlyphongkham.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tlcn.quanlyphongkham.dtos.BacSiInfo;
import tlcn.quanlyphongkham.dtos.BenhNhanInfo;
import tlcn.quanlyphongkham.entities.BacSi;
import tlcn.quanlyphongkham.entities.TinNhan;
import tlcn.quanlyphongkham.repositories.BacSiRepository;
import tlcn.quanlyphongkham.repositories.BenhNhanRepository;
import tlcn.quanlyphongkham.repositories.TinNhanRepository;

@Controller
public class TinNhanController {

	@Autowired
	private TinNhanRepository tinNhanRepository;
	@Autowired
	private BacSiRepository bacSiRepository;

	@Autowired
	private BenhNhanRepository benhNhanRepository;

	// API: Trả về JSON lịch sử tin nhắn
	@GetMapping("/api/chat/history")
	@ResponseBody
	public ResponseEntity<List<TinNhan>> getHistory(@RequestParam String benhNhanId, @RequestParam String bacSiId) {
		List<TinNhan> messages = tinNhanRepository.findByBenhNhanIdAndBacSiIdOrderByThoiGianAsc(benhNhanId, bacSiId);
		return ResponseEntity.ok(messages);
	}

	// View: Trả về giao diện chat cho bệnh nhân
	@GetMapping("/chat/bacsi")
	public String chatWithDoctor(@RequestParam("bacSiId") String bacSiId, Model model) {
		BacSi bacSi = bacSiRepository.findById(bacSiId).orElse(null);
		String bacSiName = (bacSi != null) ? bacSi.getTen() : "Không rõ";

		model.addAttribute("bacSiId", bacSiId);
		model.addAttribute("bacSiName", bacSiName);

		return "web/chat/chat";
	}

	@GetMapping("/api/chat/contacts")
	@ResponseBody
	public List<BacSiInfo> getContacts(@RequestParam String benhNhanId) {
		return tinNhanRepository.findBacSiInfoByBenhNhanId(benhNhanId);
	}

	@GetMapping("/chat/msgbn")
	public String showChatContactsBN() {
		return "web/chat/danh-sach";
	}
	
	@GetMapping("/chat/msgbs")
	public String showChatContactsBS() {
		return "web/chat/msgbs";
	}

	@GetMapping("/api/chat/contacts/bs")
	@ResponseBody
	public List<BenhNhanInfo> getContactsForDoctor(@RequestParam String bacSiId) {
	    return tinNhanRepository.findBenhNhanInfoByBacSiId(bacSiId);
	}
	
	
	
	



}
