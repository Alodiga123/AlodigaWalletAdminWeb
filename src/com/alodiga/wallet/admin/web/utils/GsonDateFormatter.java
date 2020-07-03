package com.alodiga.wallet.admin.web.utils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

public class GsonDateFormatter implements JsonDeserializer<Date> {

    private final DateFormat dateFormat;

    public GsonDateFormatter() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
    }

	public synchronized Date deserialize(JsonElement jsonElement, Type type,JsonDeserializationContext jsonDeserializationContext) {
		long epoch = jsonElement.getAsLong();
		return new Date(epoch);

	}



}
