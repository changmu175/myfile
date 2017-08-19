package com.xdja.imp.data.net;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by gbc on 2016/10/11.
 */
final class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }
    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader reader = value.charStream();
        JsonReader jsonReader = gson.newJsonReader(reader);
        try {
            //return gson.fromJson(jsonReader, adapter.getClass());
            return adapter.read(jsonReader);
        } catch (IOException e) {
            return (T) new Object();
        }
        finally {
            value.close();
        }
    }
}
