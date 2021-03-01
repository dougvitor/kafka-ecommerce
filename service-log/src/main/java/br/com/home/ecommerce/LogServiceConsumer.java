package br.com.home.ecommerce;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;

import br.com.home.ecommerce.service.KafkaServiceConsumer;
import br.com.home.ecommerce.service.Message;

public class LogServiceConsumer {

	public static void main(String[] args) throws InterruptedException {

		var logServiceConsumer = new LogServiceConsumer();

		try (var kafkaConsumer = new KafkaServiceConsumer<String>(
				LogServiceConsumer.class.getSimpleName(),
				Pattern.compile("ECOMMERCE.*"), 
				logServiceConsumer::parse, 
				Map.of(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName()))) {
			kafkaConsumer.run();
		}

	}

	private void parse(ConsumerRecord<String, Message<String>> record) {
		System.out.println("-------------------------------------------------------");
		System.out.println("LOG " + record.topic());
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
	}

}
