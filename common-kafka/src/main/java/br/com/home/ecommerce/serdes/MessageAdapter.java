package br.com.home.ecommerce.serdes;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.com.home.ecommerce.service.Message;

@SuppressWarnings("rawtypes")
public class MessageAdapter implements JsonSerializer<Message>{

	@Override
	public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		Object payload = message.getPayload();
		obj.addProperty("type", payload.getClass().getName());
		obj.add("payload", context.serialize(payload));
		obj.add("correlationId", context.serialize(message.getId()));
		return obj;
	}

}
