package tlcn.quanlyphongkham.dtos;


public class TaiKhoanProfileDTO {
    
    private String tenDangNhap;

    private String email;

    // Chỉ định các trường cần thiết khác nếu có
    // Nếu không muốn cập nhật mật khẩu, không cần đưa vào

    // Getter và Setter
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	
}
