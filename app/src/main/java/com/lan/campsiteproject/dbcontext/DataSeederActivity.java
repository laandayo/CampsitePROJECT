package com.lan.campsiteproject.dbcontext;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.util.Arrays;
import java.util.List;

public class DataSeederActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertSampleData();
    }

    private void insertSampleData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // === 30 CAMPSITE MẪU
        List<Campsite> campsiteList = Arrays.asList(
                new Campsite("camp1", 300000, "owner1", "Đà Lạt", "Camp Rừng Thông", "Gần thác Datanla", "https://example.com/image1.jpg", true, "Lan"),
                new Campsite("camp2", 250000, "owner2", "Vũng Tàu", "Camp Biển Xanh", "Ngay cạnh biển", "https://example.com/image2.jpg", true, "Hòa"),
                new Campsite("camp3", 350000, "owner3", "Bảo Lộc", "Camp Đồi Chè", "Không khí trong lành", "https://example.com/image3.jpg", true, "Nam"),
                new Campsite("camp4", 400000, "owner4", "Đà Nẵng", "Camp Núi Sơn Trà", "View biển", "https://example.com/image4.jpg", true, "Hùng"),
                new Campsite("camp5", 280000, "owner5", "Huế", "Camp Làng Cổ", "Cạnh dòng sông Hương", "https://example.com/image5.jpg", true, "Yến"),
                new Campsite("camp6", 300000, "owner6", "Hà Giang", "Camp Cao Nguyên", "View núi đá", "https://example.com/image6.jpg", true, "Linh"),
                new Campsite("camp7", 330000, "owner7", "Ninh Bình", "Camp Hang Múa", "Gần Tràng An", "https://example.com/image7.jpg", true, "Minh"),
                new Campsite("camp8", 360000, "owner8", "Hội An", "Camp Phố Cổ", "Gần chùa Cầu", "https://example.com/image8.jpg", true, "Thảo"),
                new Campsite("camp9", 270000, "owner9", "Sapa", "Camp Đồi Mây", "View ruộng bậc thang", "https://example.com/image9.jpg", true, "Phúc"),
                new Campsite("camp10", 310000, "owner10", "Hồ Tràm", "Camp Hồ Tràm", "Gần resort cao cấp", "https://example.com/image10.jpg", true, "Trang"),
                new Campsite("camp11", 290000, "owner11", "Mộc Châu", "Camp Hoa Mận", "Sát đồi hoa", "https://example.com/image11.jpg", true, "Tú"),
                new Campsite("camp12", 320000, "owner12", "Hạ Long", "Camp Bãi Biển", "Ngay bãi Cháy", "https://example.com/image12.jpg", true, "Dũng"),
                new Campsite("camp13", 300000, "owner13", "Vĩnh Hy", "Camp Rừng Biển", "Rừng sát biển", "https://example.com/image13.jpg", true, "Thanh"),
                new Campsite("camp14", 310000, "owner14", "Đắk Lắk", "Camp Thác Nước", "Gần thác Dray Nur", "https://example.com/image14.jpg", true, "Hiếu"),
                new Campsite("camp15", 350000, "owner15", "Hồ Dầu Tiếng", "Camp Bờ Hồ", "Yên bình và riêng tư", "https://example.com/image15.jpg", true, "Loan"),
                new Campsite("camp16", 260000, "owner16", "Tây Ninh", "Camp Núi Bà", "Dưới chân núi Bà Đen", "https://example.com/image16.jpg", true, "An"),
                new Campsite("camp17", 370000, "owner17", "Phan Thiết", "Camp Biển Lặng", "Biển xanh sạch", "https://example.com/image17.jpg", true, "Khoa"),
                new Campsite("camp18", 390000, "owner18", "Kon Tum", "Camp Rừng Nguyên Sinh", "Không khí trong lành", "https://example.com/image18.jpg", true, "Mai"),
                new Campsite("camp19", 240000, "owner19", "Đắk Nông", "Camp Hồ Tà Đùng", "Gần hồ tuyệt đẹp", "https://example.com/image19.jpg", true, "Ngân"),
                new Campsite("camp20", 330000, "owner20", "Buôn Ma Thuột", "Camp Vườn Cafe", "Nằm giữa rẫy cà phê", "https://example.com/image20.jpg", true, "Thịnh"),
                new Campsite("camp21", 310000, "owner21", "Phú Yên", "Camp Gành Đá", "Sát biển và đá", "https://example.com/image21.jpg", true, "Phát"),
                new Campsite("camp22", 270000, "owner22", "Quy Nhơn", "Camp Eo Gió", "Gần biển đẹp", "https://example.com/image22.jpg", true, "Hằng"),
                new Campsite("camp23", 250000, "owner23", "Nha Trang", "Camp Hòn Chồng", "View biển đẹp", "https://example.com/image23.jpg", true, "Đức"),
                new Campsite("camp24", 280000, "owner24", "Hòn Sơn", "Camp Đảo Yên Tĩnh", "Biển hoang sơ", "https://example.com/image24.jpg", true, "Quang"),
                new Campsite("camp25", 360000, "owner25", "Nam Du", "Camp Hải Đảo", "Cắm trại giữa đảo", "https://example.com/image25.jpg", true, "Châu"),
                new Campsite("camp26", 340000, "owner26", "Long Hải", "Camp Biển", "Cát trắng nắng vàng", "https://example.com/image26.jpg", true, "Trí"),
                new Campsite("camp27", 320000, "owner27", "Lý Sơn", "Camp Núi Lửa", "View biển núi", "https://example.com/image27.jpg", true, "Lộc"),
                new Campsite("camp28", 300000, "owner28", "Côn Đảo", "Camp Thiêng Liêng", "Gần rừng nguyên sinh", "https://example.com/image28.jpg", true, "Khánh"),
                new Campsite("camp29", 260000, "owner29", "Trà Vinh", "Camp Rừng Ngập Mặn", "Đặc trưng sinh thái", "https://example.com/image29.jpg", true, "Nhân"),
                new Campsite("camp30", 250000, "owner30", "Bình Thuận", "Camp Đồi Cát", "View hoàng hôn", "https://example.com/image30.jpg", true, "Duy")
        );

        for (Campsite c : campsiteList) {
            DocumentReference docRef = db.collection("campsites").document(c.getCampId());
            batch.set(docRef, c);
        }

        // === 30 GEAR MẪU THẬT
        List<Gear> gearList = Arrays.asList(
                new Gear("gear1", 80000, "owner1", "Lều 2 người", "Chống nước, thoáng khí", "https://example.com/gear1.jpg"),
                new Gear("gear2", 50000, "owner2", "Túi ngủ", "Giữ ấm, nhẹ", "https://example.com/gear2.jpg"),
                new Gear("gear3", 100000, "owner3", "Bếp gas mini", "Nấu ăn tiện", "https://example.com/gear3.jpg"),
                new Gear("gear4", 30000, "owner4", "Đèn pin", "LED siêu sáng", "https://example.com/gear4.jpg"),
                new Gear("gear5", 60000, "owner5", "Thảm picnic", "Chống thấm", "https://example.com/gear5.jpg"),
                new Gear("gear6", 90000, "owner6", "Ghế xếp", "Gọn nhẹ", "https://example.com/gear6.jpg"),
                new Gear("gear7", 120000, "owner7", "Bộ nồi", "Dã ngoại đa năng", "https://example.com/gear7.jpg"),
                new Gear("gear8", 40000, "owner8", "Đèn lều", "Ánh sáng ấm", "https://example.com/gear8.jpg"),
                new Gear("gear9", 30000, "owner9", "Bình nước", "Chống rò", "https://example.com/gear9.jpg"),
                new Gear("gear10", 150000, "owner10", "Balo 35L", "Đệm lưng thoáng khí", "https://example.com/gear10.jpg"),
                new Gear("gear11", 60000, "owner11", "Bạt che", "Mái che tạm", "https://example.com/gear11.jpg"),
                new Gear("gear12", 50000, "owner12", "Dao đa năng", "Gọn, tiện", "https://example.com/gear12.jpg"),
                new Gear("gear13", 70000, "owner13", "Lưới chống muỗi", "Bảo vệ ban đêm", "https://example.com/gear13.jpg"),
                new Gear("gear14", 65000, "owner14", "Tấm trải lều", "Chống ẩm", "https://example.com/gear14.jpg"),
                new Gear("gear15", 90000, "owner15", "Gối hơi", "Thoải mái", "https://example.com/gear15.jpg"),
                new Gear("gear16", 40000, "owner16", "Túi chống nước", "Bảo vệ đồ", "https://example.com/gear16.jpg"),
                new Gear("gear17", 75000, "owner17", "Dây thừng", "Treo đồ", "https://example.com/gear17.jpg"),
                new Gear("gear18", 50000, "owner18", "Kẹp nồi", "An toàn", "https://example.com/gear18.jpg"),
                new Gear("gear19", 45000, "owner19", "Găng tay", "Chống lạnh", "https://example.com/gear19.jpg"),
                new Gear("gear20", 30000, "owner20", "Ống hút nước", "Gấp gọn", "https://example.com/gear20.jpg"),
                new Gear("gear21", 85000, "owner21", "Bàn gấp", "Ăn uống", "https://example.com/gear21.jpg"),
                new Gear("gear22", 55000, "owner22", "Võng vải", "Thư giãn", "https://example.com/gear22.jpg"),
                new Gear("gear23", 40000, "owner23", "Đèn đội đầu", "Rảnh tay", "https://example.com/gear23.jpg"),
                new Gear("gear24", 50000, "owner24", "Ống nhòm", "Nhìn xa", "https://example.com/gear24.jpg"),
                new Gear("gear25", 47000, "owner25", "Thìa dĩa", "Nhựa dẻo", "https://example.com/gear25.jpg"),
                new Gear("gear26", 62000, "owner26", "Chảo mini", "Nấu ăn nhanh", "https://example.com/gear26.jpg"),
                new Gear("gear27", 56000, "owner27", "Hộp y tế", "Đầy đủ cơ bản", "https://example.com/gear27.jpg"),
                new Gear("gear28", 43000, "owner28", "Túi rác", "Giữ sạch", "https://example.com/gear28.jpg"),
                new Gear("gear29", 79000, "owner29", "Kính râm", "Chống UV", "https://example.com/gear29.jpg"),
                new Gear("gear30", 95000, "owner30", "Bình đun nước", "Nấu nhanh", "https://example.com/gear30.jpg")
        );

        for (Gear g : gearList) {
            DocumentReference docRef = db.collection("gear").document(g.getGearId());
            batch.set(docRef, g);
        }

        // === GỬI BATCH FIRESTORE
        batch.commit()
                .addOnSuccessListener(aVoid -> Log.d("Seeder", "✅ Nhập 30 campsite + 30 gear thành công"))
                .addOnFailureListener(e -> Log.e("Seeder", "❌ Lỗi khi chèn dữ liệu", e));
    }
}
