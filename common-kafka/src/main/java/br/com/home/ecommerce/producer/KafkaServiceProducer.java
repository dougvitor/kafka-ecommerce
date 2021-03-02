package br.com.home.ecommerce.producer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import br.com.home.ecommerce.CorrelationId;
import br.com.home.ecommerce.Message;

public class KafkaServiceProducer<T> implements Closeable{
	
	private final KafkaProducer<String, Message<T>> producer;
	
	public KafkaServiceProducer() {
		this.producer = new KafkaProducer<>(properties());
	}
	
	public void send(String topico, String key, CorrelationId id, T payload) throws InterruptedException, ExecutionException{
		var future = sendAsync(topico, key, id, payload);
		future.get();
	}

	public Future<RecordMetadata> sendAsync(String topico, String key, CorrelationId id, T payload) {
		var value = new Message<>(id.appendCorrelationId(String.format("_%s", topico)), payload);
		ProducerRecord<String, Message<T>> record = new ProducerRecord<>(topico, key, value);
		var future = this.producer.send(record, sendCallback());
		return future;
	}
	
	private static Callback sendCallback() {
		Callback callback = (data, ex) ->{
			if(ex != null) {
				ex.printStackTrace();
				return;
			}
			System.out.println("Sucesso enviando " + data.topic() + ":::partion " + data.partition() + "/ offset " + data.offset() + "/ timestamp " + data.timestamp());
		};
		return callback;
	}	
	
	private static Properties properties() {
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9090");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
		properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
		return properties;
	}

	@Override
	public void close() {
		producer.close();
	}

}
