package br.com.home.ecommerce.service;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import br.com.home.ecommerce.serdes.GsonDeserializer;

public class KafkaServiceConsumer<T> implements Closeable{

	private KafkaConsumer<String, T> consumer;
	private ConsumerFunction<T> parse;
	
	public KafkaServiceConsumer(String groupID, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
		this.consumer = new KafkaConsumer<>(getProperties(type, groupID, properties));
		this.parse = parse;
		
	}

	public KafkaServiceConsumer(String groupID, String topic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
		this(groupID, parse, type, properties);
		this.consumer.subscribe(Collections.singletonList(topic));
	}
	
	public KafkaServiceConsumer(String groupID, Pattern patternTopic, ConsumerFunction<T> parse, Class<T> type, Map<String, String> properties) {
		this(groupID, parse, type, properties);
		this.consumer.subscribe(patternTopic);
	}

	public void run() {
		while(true) {
			ConsumerRecords<String, T> records = this.consumer.poll(Duration.ofMillis(100));
			
			if(!records.isEmpty()) {
				System.out.println("Foram encontrados " + records.count() + " registros");
				records.forEach(record -> parse.consume(record));
			}
		}
	}
	
	private Properties getProperties(final Class<T> type, final String groupID, final Map<String, String> overrideProperties) {
		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
		properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
		properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());
		
		properties.putAll(overrideProperties);
		
		return properties;
	}

	@Override
	public void close() {
		this.consumer.close();
	}

}
