package com.mgmtp.jfunk.application;

import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public enum MultimapSerializer implements JsonSerializer<Multimap<?, ?>> {
	INSTANCE;

	private static final Type asMapReturnType = getAsMapMethod().getGenericReturnType();

	@Override
	public JsonElement serialize(Multimap<?, ?> multimap, Type multimapType, JsonSerializationContext context) {
		return context.serialize(multimap.asMap(), asMapType(multimapType));
	}

	private static Type asMapType(Type multimapType) {
		return TypeToken.of(multimapType).resolveType(asMapReturnType).getType();
	}

	private static Method getAsMapMethod() {
		try {
			return Multimap.class.getDeclaredMethod("asMap");
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}
}
