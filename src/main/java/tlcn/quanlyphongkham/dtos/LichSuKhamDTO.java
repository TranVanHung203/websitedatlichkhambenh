package tlcn.quanlyphongkham.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class LichSuKhamDTO {
	private String tenBacSi;
	private String ngayKham;
	private String chanDoan;
	private String thuoc;
	private String lieu;
	private String tanSuat;
}
