package tlcn.quanlyphongkham.model;

import java.time.LocalDate;

public class NguoiDung {

    private String username;   // Tên đăng nhập
    private String password;   // Mật khẩu
    private String email;      // Email
    private String fullname;    // Họ và tên
    private LocalDate birthdate; // Ngày sinh
    private String gender;     // Giới tính
    private String phone;      // Số điện thoại
    private String address;    // Địa chỉ

    // Constructor
    public NguoiDung() {
    }

    // Getters và Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
