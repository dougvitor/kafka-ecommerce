package br.com.home.ecommerce;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.service.KafkaServiceConsumer;

public class FraudDetectorServiceConsumer {

	public static void main(String[] args) {

		FraudDetectorServiceConsumer fraudeService = new FraudDetectorServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<PedidoCompra>(
				FraudDetectorServiceConsumer.class.getSimpleName(), "ECOMMERCE_NEW_ORDER", fraudeService::parse,
				PedidoCompra.class,
				Map.of())) {
			kafkaServiceConsumer.run();
		}

	}

	private void parse(ConsumerRecord<String, PedidoCompra> record) {

		System.out.println("-------------------------------------------------------");
		System.out.println("Processando novo pedido, chegando possiveis fraudes");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Pedido Processado");
	}

}
