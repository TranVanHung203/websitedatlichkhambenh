// Hàm hiển thị thông báo lỗi
function showError(message) {
    var errorMessageDiv = document.getElementById("error-message");
    errorMessageDiv.innerText = message; // Cập nhật thông điệp
    errorMessageDiv.style.display = "block"; // Hiện thông điệp
}

// Kiểm tra và hiển thị thông báo lỗi nếu có
document.addEventListener("DOMContentLoaded", function() {
    var errorMessage = /*[[${error}]]*/ 'null'; // Nhận thông báo lỗi từ Thymeleaf
    if (errorMessage !== 'null') {
        showError("Email đã tồn tại, vui lòng sử dụng email khác."); // Gọi hàm hiển thị thông báo
    }
});
