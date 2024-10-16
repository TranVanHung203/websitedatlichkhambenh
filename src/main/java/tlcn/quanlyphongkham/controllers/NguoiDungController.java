package tlcn.quanlyphongkham.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tlcn.quanlyphongkham.entities.NguoiDung;
import tlcn.quanlyphongkham.services.NguoiDungService;

import java.util.List;

@RestController
@RequestMapping("/api/nguoidung")
public class NguoiDungController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @GetMapping
    public List<NguoiDung> getAllNguoiDung() {
        return nguoiDungService.getAllNguoiDung();
    }

    @PostMapping
    public NguoiDung createNguoiDung(@RequestBody NguoiDung nguoiDung) {
        return nguoiDungService.saveNguoiDung(nguoiDung);
    }
}