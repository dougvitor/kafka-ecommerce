package br.com.home.ecommerce.serdes;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonDeserializer<T> implements Deserializer<T> {

	public static final String TYPE_CONFIG = "br.com.home.ecommerce.type_config";
	
	private final Gson gson = new GsonBuilder().create();

	private Class<T> type;
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {
		
		try {
			String typeName = String.valueOf(configs.get(TYPE_CONFIG));
			this.type = (Class<T>) Class.forName(typeName);
			
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Tipo para deserialização não existe no Classpath.", e);
		}
	}

	@Override
	public T deserialize(String topic, byte[] data) {
		return gson.fromJson(new String(data), type);
	}

}
