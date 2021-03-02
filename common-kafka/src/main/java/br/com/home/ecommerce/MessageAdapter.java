package br.com.home.ecommerce;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings({"rawtypes","unchecked"})
public class MessageAdapter implements JsonSerializer<Message>, JsonDeserializer<Message>{

	@Override
	public JsonElement serialize(Message message, Type type, JsonSerializationContext context) {
		JsonObject obj = new JsonObject();
		Object payload = message.getPayload();
		obj.addProperty("type", payload.getClass().getName());
		obj.add("payload", context.serialize(payload));
		obj.add("correlationId", context.serialize(message.getId()));
		return obj;
	}

	@Override
	public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		var obj = json.getAsJsonObject();
		var type = obj.get("type").getAsString();
		var correlationId = (CorrelationId) context.deserialize(obj.get("correlationId"), CorrelationId.class);
		try {
			var payload = context.deserialize(obj.get("payload"), Class.forName(type));
			return new Message(correlationId, payload);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new JsonParseException(e);
		}
	}

}
