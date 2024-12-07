package tlcn.quanlyphongkham.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "DonThuocThuoc")
public class DonThuocThuoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "don_thuoc_id")
    private DonThuoc donThuoc;

    @ManyToOne
    @JoinColumn(name = "thuoc_id")
    private Thuoc thuoc;

    @Column(name = "lieu")
    private String lieu;

    @Column(name = "tan_suat")
    private String tanSuat;

    // Thêm cột số lượng
    @Column(name = "so_luong",nullable = false, columnDefinition = "int default 0")
    private int soLuong; // Kiểu số lượng, có thể sử dụng int hoặc Long tùy vào yêu cầu.
}
