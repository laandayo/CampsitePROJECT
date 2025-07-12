package com.lan.campsiteproject.controller.campsite;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lan.campsiteproject.model.Campsite;
import com.lan.campsiteproject.model.Gear;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private Campsite selectedCampsite;
    private Map<Gear, Integer> gearMap;
    private int numPeople;
    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_CAMPSITE = "selectedCampsite";
    private static final String KEY_GEAR_MAP = "gearMap";
    private static final String KEY_NUM_PEOPLE = "numPeople";

    private CartManager() {
        gearMap = new HashMap<>();
        numPeople = 1;
        Log.d("CartManager", "CartManager initialized with empty gearMap and numPeople=1");
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
            Log.d("CartManager", "New CartManager instance created");
        }
        return instance;
    }

    public void addCampsite(Campsite campsite, Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in addCampsite");
            return;
        }
        this.selectedCampsite = campsite;
        Log.d("CartManager", "Adding campsite: " + (campsite != null ? campsite.getCampName() : "null"));
        saveToPreferences(context);
    }

    public Campsite getSelectedCampsite() {
        return selectedCampsite;
    }

    public void setNumPeople(int num, Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in setNumPeople");
            return;
        }
        this.numPeople = Math.max(1, num);
        Log.d("CartManager", "Setting numPeople: " + numPeople);
        saveToPreferences(context);
    }

    public int getNumPeople() {
        return numPeople;
    }

    public void addGear(Gear gear, Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in addGear");
            return;
        }
        if (gear == null) {
            Log.e("CartManager", "Attempted to add null gear");
            return;
        }
        gearMap.put(gear, gearMap.getOrDefault(gear, 0) + 1);
        Log.d("CartManager", "Adding gear: " + gear.getGearName() + ", new quantity: " + gearMap.get(gear));
        saveToPreferences(context);
    }

    public void removeGear(Gear gear, Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in removeGear");
            return;
        }
        if (gear == null) {
            Log.e("CartManager", "Attempted to remove null gear");
            return;
        }
        gearMap.remove(gear);
        Log.d("CartManager", "Removing gear: " + gear.getGearName());
        saveToPreferences(context);
    }

    public Map<Gear, Integer> getGearMap() {
        return gearMap;
    }

    public void updateGearQuantity(Gear gear, int qty, Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in updateGearQuantity");
            return;
        }
        if (gear == null) {
            Log.e("CartManager", "Attempted to update quantity for null gear");
            return;
        }
        if (qty <= 0) {
            gearMap.remove(gear);
            Log.d("CartManager", "Removing gear due to quantity <= 0: " + gear.getGearName());
        } else {
            gearMap.put(gear, qty);
            Log.d("CartManager", "Updating gear quantity: " + gear.getGearName() + " to " + qty);
        }
        saveToPreferences(context);
    }

    public void clearCart(Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null in clearCart");
            return;
        }
        selectedCampsite = null;
        gearMap.clear();
        numPeople = 1;
        Log.d("CartManager", "Cart cleared");
        saveToPreferences(context);
    }

    public int getCampsiteQuantity() {
        return selectedCampsite != null ? 1 : 0;
    }

    private void saveToPreferences(Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null, cannot save to SharedPreferences");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        if (selectedCampsite != null) {
            String campsiteJson = gson.toJson(selectedCampsite);
            editor.putString(KEY_CAMPSITE, campsiteJson);
            Log.d("CartManager", "Saving campsiteJson: " + campsiteJson);
        } else {
            editor.remove(KEY_CAMPSITE);
            Log.d("CartManager", "Removing campsite from SharedPreferences");
        }

        String gearMapJson = gson.toJson(gearMap);
        editor.putString(KEY_GEAR_MAP, gearMapJson);
        Log.d("CartManager", "Saving gearMapJson: " + gearMapJson);

        editor.putInt(KEY_NUM_PEOPLE, numPeople);
        Log.d("CartManager", "Saving numPeople: " + numPeople);

        editor.apply();
        Log.d("CartManager", "Preferences saved");
    }

    public void restoreFromPreferences(Context context) {
        if (context == null) {
            Log.e("CartManager", "Context is null, cannot restore from SharedPreferences");
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String campsiteJson = prefs.getString(KEY_CAMPSITE, null);
        Log.d("CartManager", "Restoring campsiteJson: " + campsiteJson);
        if (campsiteJson != null && !campsiteJson.isEmpty()) {
            try {
                selectedCampsite = gson.fromJson(campsiteJson, Campsite.class);
                Log.d("CartManager", "Campsite restored: " + (selectedCampsite != null ? selectedCampsite.getCampName() : "null"));
            } catch (Exception e) {
                Log.e("CartManager", "Error deserializing campsite: " + e.getMessage());
                selectedCampsite = null;
            }
        } else {
            selectedCampsite = null;
            Log.d("CartManager", "No campsite data to restore");
        }

        String gearMapJson = prefs.getString(KEY_GEAR_MAP, null);
        Log.d("CartManager", "Restoring gearMapJson: " + gearMapJson);
        if (gearMapJson != null && !gearMapJson.isEmpty() && !gearMapJson.equals("{}")) {
            try {
                Type type = new TypeToken<Map<Gear, Integer>>(){}.getType();
                Map<Gear, Integer> restoredGearMap = gson.fromJson(gearMapJson, type);
                gearMap.clear();
                if (restoredGearMap != null) {
                    gearMap.putAll(restoredGearMap);
                    Log.d("CartManager", "GearMap restored with " + gearMap.size() + " items");
                } else {
                    Log.d("CartManager", "Restored gearMap is null");
                }
            } catch (Exception e) {
                Log.e("CartManager", "Error deserializing gearMap: " + e.getMessage());
                gearMap.clear();
            }
        } else {
            gearMap.clear();
            Log.d("CartManager", "No gearMap data to restore or empty JSON");
        }

        numPeople = prefs.getInt(KEY_NUM_PEOPLE, 1);
        Log.d("CartManager", "Restored numPeople: " + numPeople);
    }
}