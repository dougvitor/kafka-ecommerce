package br.com.home.ecommerce;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.service.KafkaServiceConsumer;
import br.com.home.ecommerce.service.KafkaServiceProducer;

public class FraudDetectorServiceConsumer {
	
	private final KafkaServiceProducer<PedidoCompra> producer = new KafkaServiceProducer<>();

	public static void main(String[] args) {

		FraudDetectorServiceConsumer fraudeService = new FraudDetectorServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<PedidoCompra>(
				FraudDetectorServiceConsumer.class.getSimpleName(), 
				"ECOMMERCE_NEW_ORDER", 
				fraudeService::parse,
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
		
		var pedidoCompra = record.value();
		
		if(isFraude(pedidoCompra)) {
			System.out.println("É uma fraude!!!");
			producer.send("ECOMMERCE_ORDER_REJECT", pedidoCompra.getUserId(), pedidoCompra);
		}else {
			System.out.println("Aprovado: " + pedidoCompra);
			producer.send("ECOMMERCE_ORDER_APPROVED", pedidoCompra.getUserId(), pedidoCompra);
		}

		System.out.println("Pedido Processado");
	}

	private boolean isFraude(PedidoCompra pedidoCompra) {
		return pedidoCompra.getTotal().compareTo(new BigDecimal("4500")) > 0;
	}
}
