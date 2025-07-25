package com.lan.campsiteproject.controller.campsite;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.lan.campsiteproject.R;
import com.lan.campsiteproject.adapter.CampsiteAdapter;
import com.lan.campsiteproject.aichat.AiChatActivity;
import com.lan.campsiteproject.controller.SettingsActivity;
import com.lan.campsiteproject.controller.orders.OrderHistoryActivity;
import com.lan.campsiteproject.controller.user.ChatListActivity;
import com.lan.campsiteproject.controller.user.LoginActivity;
import com.lan.campsiteproject.map.MapActivity;
import com.lan.campsiteproject.model.Campsite;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListCampsiteActivity extends AppCompatActivity {

    private static final String TAG = "ListCampsiteActivity";
    private RecyclerView recyclerView;
    private CampsiteAdapter adapter;
    private CartManager cartManager;
    private TextView cartBadge;
    private List<Campsite> fullList = new ArrayList<>();
    private EditText edtSearch;
    private BottomSheetDialog bottomSheetDialog;
    private Button btnRefresh;

    private static final int PAGE_SIZE = 6;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private DocumentSnapshot lastVisible = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listcampsite);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Initialize views
        edtSearch = findViewById(R.id.edt_search);
        ImageButton btnCart = findViewById(R.id.btn_cart);
        ImageButton btnFilter = findViewById(R.id.btn_filter);
        cartBadge = findViewById(R.id.cart_badge);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCampsite);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading && !isLastPage && layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        loadNextPage();
                    }
                }
            }
        });

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // ví dụ: 8dp
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
        cartManager = CartManager.getInstance();
        adapter = new CampsiteAdapter(this, new ArrayList<>(), cartManager);
        recyclerView.setAdapter(adapter);



        // Setup search functionality
        setupSearch();

        // Setup cart button
        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        Button aiChatButton = findViewById(R.id.aiChat);
        aiChatButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AiChatActivity.class);
            startActivity(intent);
        });

        // Setup filter button
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());

        // Setup multi-action button
        setupMultiActionButton();

        // Load campsites
        loadCampsites();


        btnRefresh = findViewById(R.id.btn_refresh_list);
        btnRefresh.setOnClickListener(v -> {
            loadCampsites(); // Tải lại trang đầu tiên để giữ phân trang
            recyclerView.setVisibility(View.VISIBLE);
            btnRefresh.setVisibility(View.GONE);
            TextView noResultText = findViewById(R.id.txt_no_result);
            if (noResultText != null) {
                noResultText.setVisibility(View.GONE); // Ẩn TextView
            }
        });

    }

    private void setupSearch() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchCampsites(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupMultiActionButton() {
        Button multiActionButton = findViewById(R.id.multiActionButton);
        multiActionButton.setOnLongClickListener(v -> {
            View popupView = LayoutInflater.from(this).inflate(R.layout.popup_multi_action, null);
            PopupWindow popupWindow = new PopupWindow(popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, true);

            // Setup popup menu buttons
            popupView.findViewById(R.id.btnChat).setOnClickListener(btn -> {
                startActivity(new Intent(this, ChatListActivity.class));
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnMap).setOnClickListener(btn -> {
                startActivity(new Intent(this, MapActivity.class));
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnCart).setOnClickListener(btn -> {
                startActivity(new Intent(this, CartActivity.class));
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnOrder).setOnClickListener(btn -> {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(this, OrderHistoryActivity.class);
                    intent.putExtra("bookerId", user.getUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Bạn cần đăng nhập để xem lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnLogout).setOnClickListener(btn -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                popupWindow.dismiss();
            });

            popupView.findViewById(R.id.btnSettings).setOnClickListener(btn -> {
                startActivity(new Intent(this, SettingsActivity.class));
                popupWindow.dismiss();
            });

            popupWindow.showAsDropDown(multiActionButton, -100, -200);
            return true;
        });
    }

    private final List<String> vietnamProvinces = new ArrayList<String>() {{
        add("An Giang"); add("Bà Rịa - Vũng Tàu"); add("Bắc Giang"); add("Bắc Kạn"); add("Bạc Liêu");
        add("Bắc Ninh"); add("Bến Tre"); add("Bình Định"); add("Bình Dương"); add("Bình Phước");
        add("Bình Thuận"); add("Cà Mau"); add("Cao Bằng"); add("Đắk Lắk"); add("Đắk Nông");
        add("Điện Biên"); add("Đồng Nai"); add("Đồng Tháp"); add("Gia Lai"); add("Hà Giang");
        add("Hà Nam"); add("Hà Nội"); add("Hà Tĩnh"); add("Hải Dương"); add("Hải Phòng");
        add("Hậu Giang"); add("Hòa Bình"); add("Hưng Yên"); add("Khánh Hòa"); add("Kiên Giang");
        add("Kon Tum"); add("Lai Châu"); add("Lâm Đồng"); add("Lạng Sơn"); add("Lào Cai");
        add("Long An"); add("Nam Định"); add("Nghệ An"); add("Ninh Bình"); add("Ninh Thuận");
        add("Phú Thọ"); add("Phú Yên"); add("Quảng Bình"); add("Quảng Nam"); add("Quảng Ngãi");
        add("Quảng Ninh"); add("Quảng Trị"); add("Sóc Trăng"); add("Sơn La"); add("Tây Ninh");
        add("Thái Bình"); add("Thái Nguyên"); add("Thanh Hóa"); add("Thừa Thiên Huế");
        add("Tiền Giang"); add("TP. Hồ Chí Minh"); add("Trà Vinh"); add("Tuyên Quang");
        add("Vĩnh Long"); add("Vĩnh Phúc"); add("Yên Bái"); add("Cần Thơ"); add("Đà Nẵng");
    }};

    private final List<String> selectedProvinces = new ArrayList<>();

    private void showFilterBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Initialize views
        EditText edtSearchProvince = bottomSheetView.findViewById(R.id.edt_search_province);
        RecyclerView recyclerViewProvinces = bottomSheetView.findViewById(R.id.recycler_provinces);
        SeekBar seekbarQuantity = bottomSheetView.findViewById(R.id.seekbar_quantity);
        SeekBar seekbarPrice = bottomSheetView.findViewById(R.id.seekbar_price);
        Button btnApplyFilter = bottomSheetView.findViewById(R.id.btnApplyFilter);
        Button btnResetFilter = bottomSheetView.findViewById(R.id.btnResetFilter);
        TextView txtQuantity = bottomSheetView.findViewById(R.id.txt_quantity_value);
        TextView txtPrice = bottomSheetView.findViewById(R.id.txt_price_value);

        // Setup province list
        List<String> filteredProvinces = new ArrayList<>(vietnamProvinces);
        ProvinceAdapter provinceAdapter = new ProvinceAdapter(filteredProvinces, selectedProvinces);
        recyclerViewProvinces.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProvinces.setAdapter(provinceAdapter);

        // Setup province search
        edtSearchProvince.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().toLowerCase();
                filteredProvinces.clear();
                for (String province : vietnamProvinces) {
                    if (province.toLowerCase().contains(keyword)) {
                        filteredProvinces.add(province);
                    }
                }
                provinceAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup quantity seekbar
        seekbarQuantity.setProgress(1);
        txtQuantity.setText("Số khách: " + seekbarQuantity.getProgress());
        seekbarQuantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtQuantity.setText("Số khách: " + progress);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Setup price seekbar
        seekbarPrice.setProgress(1000);
        txtPrice.setText("Giá tối thiểu: " + String.format(Locale.getDefault(), "%,d", seekbarPrice.getProgress()) + " VND");
        seekbarPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtPrice.setText("Giá tối thiểu: " + String.format(Locale.getDefault(), "%,d", progress) + " VND");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Setup reset button
        btnResetFilter.setOnClickListener(v -> {
            selectedProvinces.clear();
            seekbarQuantity.setProgress(0);
            seekbarPrice.setProgress(0);
            provinceAdapter.notifyDataSetChanged();
            adapter.updateCampsites(fullList);  // ✅ load lại danh sách gốc
            TextView noResultText = findViewById(R.id.txt_no_result);
            if (noResultText != null) {
                noResultText.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            bottomSheetDialog.dismiss();
        });


        // Setup apply button
        btnApplyFilter.setOnClickListener(v -> {
            int selectedGuests = seekbarQuantity.getProgress();
            int selectedMinPrice = seekbarPrice.getProgress();
            filterCampsites(new ArrayList<>(selectedProvinces), selectedGuests, selectedMinPrice);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void loadCampsites() {
        isLoading = true;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("campsites")
                .orderBy("campName")
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Campsite> newCampsites = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            newCampsites.add(doc.toObject(Campsite.class));
                        }
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        fullList.clear();
                        fullList.addAll(newCampsites);
                        adapter.updateCampsites(fullList);
                        isLastPage = newCampsites.size() < PAGE_SIZE;
                    } else {
                        isLastPage = true;
                    }
                    isLoading = false;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
    }
    private void loadNextPage() {
        if (isLoading || isLastPage || lastVisible == null) return;
        isLoading = true;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("campsites")
                .orderBy("campName")
                .startAfter(lastVisible)
                .limit(PAGE_SIZE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Campsite> newCampsites = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            newCampsites.add(doc.toObject(Campsite.class));
                        }
                        lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        fullList.addAll(newCampsites); // vẫn giữ nếu bạn cần search/filter
                        adapter.appendCampsites(newCampsites);
                        isLastPage = newCampsites.size() < PAGE_SIZE;
                    } else {
                        isLastPage = true;
                    }
                    isLoading = false;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải thêm dữ liệu", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
    }



    private void searchCampsites(String keyword) {
        List<Campsite> filtered = new ArrayList<>();
        for (Campsite camp : fullList) {
            if (camp.getCampName().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(camp);
            }
        }
        adapter.updateCampsites(filtered);
    }

    private void updateCartBadge() {
        int count = cartManager.getCampsiteQuantity();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        // Refresh list when returning from other activities
        adapter.updateCampsites(fullList);
    }

    @Override
    public void onBackPressed() {
        // Reset to full list when back button is pressed
        adapter.updateCampsites(fullList);

        TextView noResultText = findViewById(R.id.txt_no_result);
        if (noResultText != null) {
            noResultText.setVisibility(View.GONE);
        }
        recyclerView.setVisibility(View.VISIBLE);

        super.onBackPressed();
    }

    private void filterCampsites(List<String> provinces, int guests, int minPrice) {
        Log.d(TAG, "Filtering with provinces: " + provinces + ", guests: " + guests + ", minPrice: " + minPrice);

        List<Campsite> filtered = new ArrayList<>();
        for (Campsite camp : fullList) {
            boolean matchesProvince = (provinces == null || provinces.isEmpty() ||
                    provinces.stream().anyMatch(p -> camp.getCampAddress().contains(p)));
            boolean matchesGuests = camp.getQuantity() >= guests;
            boolean matchesPrice = camp.getCampPrice() >= minPrice;

            Log.d(TAG, "Checking campsite: " + camp.getCampName() +
                    ", Address: " + camp.getCampAddress() +
                    ", matchesProvince: " + matchesProvince +
                    ", matchesGuests: " + matchesGuests +
                    ", matchesPrice: " + matchesPrice);

            if (matchesProvince && matchesGuests && matchesPrice) {
                filtered.add(camp);
            }
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "Không có địa điểm phù hợp với bộ lọc của bạn", Toast.LENGTH_LONG).show();
            TextView noResultText = findViewById(R.id.txt_no_result);
            if (noResultText != null) {
                noResultText.setVisibility(View.VISIBLE);
                noResultText.setText("Không tìm thấy campsite phù hợp\nVui lòng thử lại với điều kiện khác");

            }
            if (btnRefresh != null) {
                btnRefresh.setVisibility(View.VISIBLE);
            }

            recyclerView.setVisibility(View.GONE);
        } else {
            TextView noResultText = findViewById(R.id.txt_no_result);
            if (noResultText != null) {
                noResultText.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Tìm thấy " + filtered.size() + " campsite phù hợp", Toast.LENGTH_SHORT).show();
        }

        adapter.updateCampsites(filtered);
    }

    private static class ProvinceAdapter extends RecyclerView.Adapter<ProvinceAdapter.ProvinceViewHolder> {
        private final List<String> provinces;
        private final List<String> selectedProvinces;

        public ProvinceAdapter(List<String> provinces, List<String> selectedProvinces) {
            this.provinces = provinces;
            this.selectedProvinces = selectedProvinces;
        }

        @Override
        public ProvinceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_province, parent, false);
            return new ProvinceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProvinceViewHolder holder, int position) {
            String province = provinces.get(position);
            holder.checkBox.setText(province);
            holder.checkBox.setChecked(selectedProvinces.contains(province));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedProvinces.contains(province)) {
                        selectedProvinces.add(province);
                    }
                } else {
                    selectedProvinces.remove(province);
                }
            });
        }

        @Override
        public int getItemCount() {
            return provinces.size();
        }

        static class ProvinceViewHolder extends RecyclerView.ViewHolder {
            final CheckBox checkBox;

            ProvinceViewHolder(View itemView) {
                super(itemView);
                checkBox = itemView.findViewById(R.id.checkbox_province);
            }
        }
    }
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


}