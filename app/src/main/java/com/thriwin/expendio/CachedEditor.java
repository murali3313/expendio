package com.thriwin.expendio;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Set;

public class CachedEditor implements SharedPreferences.Editor {


    private final SharedPreferences.Editor editor;
    private CachedSharedPreferences cachedSharedPreferences;

    public CachedEditor(SharedPreferences sharedPreferences, CachedSharedPreferences cachedSharedPreferences) {
        this.editor = sharedPreferences.edit();
        this.cachedSharedPreferences = cachedSharedPreferences;
    }

    @Override
    public SharedPreferences.Editor putString(String key, @Nullable String value) {
        cachedSharedPreferences.update(key, value);
        return this.editor.putString(key, value);
    }

    @Override
    public SharedPreferences.Editor putStringSet(String key, @Nullable Set<String> values) {
        return null;
    }

    @Override
    public SharedPreferences.Editor putInt(String key, int value) {
        cachedSharedPreferences.update(key, value);
        return this.editor.putInt(key, value);
    }

    @Override
    public SharedPreferences.Editor putLong(String key, long value) {
        cachedSharedPreferences.update(key, value);
        return this.editor.putLong(key, value);
    }

    @Override
    public SharedPreferences.Editor putFloat(String key, float value) {
        cachedSharedPreferences.update(key, value);
        return this.editor.putFloat(key, value);
    }

    @Override
    public SharedPreferences.Editor putBoolean(String key, boolean value) {
        cachedSharedPreferences.update(key, value);
        return this.editor.putBoolean(key, value);
    }

    @Override
    public SharedPreferences.Editor remove(String key) {
        cachedSharedPreferences.remove(key);
        return this.editor.remove(key);
    }

    @Override
    public SharedPreferences.Editor clear() {
        cachedSharedPreferences.clear();
        return this.editor.clear();
    }

    @Override
    public boolean commit() {
        return this.editor.commit();
    }

    @Override
    public void apply() {
        this.editor.apply();
    }
}
