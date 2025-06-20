NỘI QUY KHI CODE:
- KHI COMMMIT/ PUSH, KHÔNG TÍCH CHỌN Ô FILE .env, server.js





Thứ tự Workflow:
- Kết nối SQL Server thông qua Restful api:
  + Copy đường dẫn này rồi vào file máy tính, đến đường dẫn đó
  + ![image](https://github.com/user-attachments/assets/365d7be8-5198-4a6f-a5b3-fb43029b409c)
  + Chuột phải vào khoảng không, chọn Cmd hoặc gõ Cmd vào trên thanh
  + Gõ "node server.js"
  + Nếu hiện như ở dưới thì đã kết nối thành công
  + ![image](https://github.com/user-attachments/assets/fc4ab498-ec7f-441b-a76a-b9ca658cfa36)

- MUỐN TẠO RA 1 FEATURE MỚI
- Vào phần Remote, đưa chuột vào nhánh develop và chọn "New branch"
![image](https://github.com/user-attachments/assets/6c1ad667-f001-41af-b084-485c32acd6ed)

- Khi đặt tên cho nhánh mới, hãy đặt tên mình- tên chức năng mình sẽ làm, ví dụ "phu-login+register" hay "lan-logout"
- nếu có tạo file activity mới, hãy nhớ thêm gán tên file đó vào trong file AndroidManifest thì mới chạy được file đó
- (ví dụ như dưới, file test1st được gọi trong file AndroidManifest)
- (đụng đến file Model thì không cần thiết)
![image](https://github.com/user-attachments/assets/9de13057-775e-4952-94c8-9531ab3bbdd4)
![image](https://github.com/user-attachments/assets/8b459109-d654-4d8a-a504-1c4b1aa240d3)

- (vì thế khi commit, hãy nhớ tick cả file AndroidManifest nữa)

- Khi đã xong xuôi, test chạy tạm ổn, hãy tạm thời commit trước (chưa push) rồi hãy đưa chuột vào tên project rồi chuột phải, xuống phần git và chọn pull, chọn nhánh develop để so sánh giữa 2 nhánh với nhau xem có conflict không, nếu xảy ra conflict thì hãy tự fix trước khi push

![image](https://github.com/user-attachments/assets/785ced93-82ba-4d05-84ee-4e027f52d2aa)
![image](https://github.com/user-attachments/assets/d93492fb-8765-481e-8c80-986c961c91a4)
![image](https://github.com/user-attachments/assets/dd6c5ef1-a5a9-4648-bcd7-5b3a08456c93)

- Sau khi được rồi thì push lên nhánh mình đang làm thôi

