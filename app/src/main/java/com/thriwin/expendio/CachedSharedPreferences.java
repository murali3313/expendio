package com.thriwin.expendio;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.thriwin.expendio.Utils.isNull;

public class CachedSharedPreferences implements SharedPreferences {


    private boolean isGetAllCalled = false;
    private SharedPreferences sharedPreferences;
    private CachedEditor cachedEditor;

    private Map<String, Object> cachedValues = new HashMap<>();

    public void update(String key, Object value) {
        this.cachedValues.put(key, value);
    }

    public CachedSharedPreferences(SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.cachedEditor = new CachedEditor(sharedPreferences, this);
    }

    @Override
    public Map<String, Object> getAll() {
        return getCachedValues();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        if (isNull(cachedValues) || isNull(cachedValues.get(key))) {
            String value = sharedPreferences.getString(key, defValue);
            cachedValues.put(key, value);
            return value;
        } else {
            String value = (String) cachedValues.get(key);
            return isNull(value) ? defValue : value;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        return null;
    }

    @Override
    public int getInt(String key, int defValue) {
        if (isNull(cachedValues) || isNull(cachedValues.get(key))) {
            Integer value = sharedPreferences.getInt(key, defValue);
            cachedValues.put(key, value);
            return value;
        } else {
            Integer value = (Integer) cachedValues.get(key);
            return isNull(value) ? defValue : value;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        return 0;
    }

    @Override
    public float getFloat(String key, float defValue) {
        return 0;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        if (isNull(cachedValues) || isNull(cachedValues.get(key))) {
            Boolean value = sharedPreferences.getBoolean(key, defValue);
            cachedValues.put(key, value);
            return value;
        } else {
            Boolean value = (Boolean) cachedValues.get(key);
            return isNull(value) ? defValue : value;
        }
    }

    @Override
    public boolean contains(String key) {
        return false;
    }

    @Override
    public Editor edit() {
        return cachedEditor;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {

    }

    private Map<String, Object> getCachedValues() {
        if (isNull(cachedValues) || !isGetAllCalled) {
            isGetAllCalled = true;
            cachedValues = (Map<String, Object>) sharedPreferences.getAll();
        }
        return new HashMap<>(cachedValues);
    }

    public void remove(String key) {
        this.cachedValues.remove(key);
    }

    public void clear() {
        cachedValues.clear();
    }
}
