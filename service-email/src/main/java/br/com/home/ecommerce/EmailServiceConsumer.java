package br.com.home.ecommerce;

import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailServiceConsumer implements ConsumerService<String>{

	public static void main(String[] args) throws InterruptedException, ExecutionException{
		new ServiceProvider().run(EmailServiceConsumer::new);
	}

	@Override
	public String getTopic() {
		return "ECOMMERCE_SEND_EMAIL";
	}

	@Override
	public String getConsumerGroup() {
		return EmailServiceConsumer.class.getName();
	}
	
	@Override
	public void parse(ConsumerRecord<String, Message<String>> record) {
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
