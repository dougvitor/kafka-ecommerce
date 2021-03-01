package br.com.home.ecommerce;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.model.Email;
import br.com.home.ecommerce.service.KafkaServiceConsumer;
import br.com.home.ecommerce.service.Message;

public class EmailServiceConsumer {

	public static void main(String[] args) {
		var emailServiceConsumer = new EmailServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<Email>(
				EmailServiceConsumer.class.getName(),
				"ECOMMERCE_SEND_EMAIL", 
				emailServiceConsumer::parse, 
				Map.of())) {
			kafkaServiceConsumer.run();
		}

	}

	private void parse(ConsumerRecord<String, Message<Email>> record) {
		
		System.out.println("-------------------------------------------------------");
		System.out.println("Enviando e-mail");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Email enviado com sucesso");
	}

}
