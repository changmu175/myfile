package com.securevoip.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by fernando on 9/10/14.
 */
public class AssetsProperties {
    /**
     * Created by fernando on 9/11/14.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Property {
        String value() default "";
    }

    private static final String TAG = "AssetsProperties";
    private static final String DEFAUT_ANNOTATION_VALUE = "";

    private Resources mResources;
    private String mPropertiesFileName = "";
    private java.util.Properties mProperties = new java.util.Properties();

    public AssetsProperties(Context context, String propertiesFileName) {
        mResources = context.getResources();
        mPropertiesFileName = propertiesFileName;
        openProperties(mResources);
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(mProperties.getProperty(key));
        } catch (Exception e) {
            logParseError(key, "int");
            return defaultValue;
        }
    }

    public float getFloat(String key, float defaultValue) {
        try {
            return Float.parseFloat(mProperties.getProperty(key));
        } catch (Exception e) {
            logParseError(key, "float");
            return defaultValue;
        }
    }

    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(mProperties.getProperty(key));
        } catch (Exception e) {
            logParseError(key, "double");
            return defaultValue;
        }
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(mProperties.getProperty(key));
        } catch (Exception e) {
            logParseError(key, "boolean");
            return defaultValue;
        }
    }

    public String getString(String key, String defaultValue) {
        return mProperties.getProperty(key, defaultValue);
    }

    public ArrayList<String> getArrayList(String key, ArrayList<String> defaultArrayList) {
        try {
            return getArrayListWithString(mProperties.getProperty(key));
        } catch (Exception e) {
            return defaultArrayList;
        }
    }

    @SuppressLint("Annotator")
    private ArrayList<String> getArrayListWithString(String str) {
        String[] keys = str.split("\\,");
        ArrayList<String> arrayList = new ArrayList<>();
        for (String key : keys) {
            String value = getString(key, "");
            arrayList.add(value);
        }
        return arrayList;
    }

    private void openProperties(Resources resources) {
        try {
            InputStream inputStream = resources.getAssets().open(mPropertiesFileName );
            mProperties.load(inputStream);
            loadPropertiesValues();
        } catch (IOException e) {
            Log.wtf(TAG, e);
        }
    }

    protected void loadPropertiesValues() {
        Class<? extends AssetsProperties> thisClass = this.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Property annotation = field.getAnnotation(Property.class);

                if (annotation.value().equals(DEFAUT_ANNOTATION_VALUE)) {
                    setFieldValue(field, fieldName);
                } else {
                    setFieldValue(field, annotation.value());
                }
            }
        }
    }

    private void setFieldValue(Field field, String propertiesName) {
        Object value = getPropertyValue(field.getType(), propertiesName);
        try {
            field.set(this, value);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "AssetsProperties : impossible to set value of field: "
                    + field.getName() + " for " + propertiesName);
        }
    }

    private Object getPropertyValue(Class<?> clazz, String key) {
        if (clazz == String.class) {
            return getString(key, "");
        } else if (clazz == float.class || clazz == Float.class) {
            return getFloat(key, 0);
        } else if (clazz == double.class || clazz == Double.class) {
            return getDouble(key, 0);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return getBoolean(key, false);
        } else if (clazz == int.class || clazz == Integer.class) {
            return getInt(key, 0);
        } else if (clazz == ArrayList.class) {
            return getArrayList(key, new ArrayList<String>());
        } else {
            return null;
        }
    }

    private void logParseError(String key, String target) {
        Log.e(TAG, "AssetsProperties can't parse property " + key + " as " + target);
    }
}
