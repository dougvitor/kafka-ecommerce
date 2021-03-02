package br.com.home.ecommerce;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.consumer.KafkaServiceConsumer;
import br.com.home.ecommerce.model.Email;
import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.producer.KafkaServiceProducer;

public class EmailNovoPedidoServiceConsumer {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		EmailNovoPedidoServiceConsumer emailService = new EmailNovoPedidoServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<PedidoCompra>(
				EmailNovoPedidoServiceConsumer.class.getSimpleName(), 
				"ECOMMERCE_NEW_ORDER", 
				emailService::parse,
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}

	private void parse(ConsumerRecord<String, Message<PedidoCompra>> record) throws InterruptedException, ExecutionException {
		try(var kafkaEmailServiceProducer = new KafkaServiceProducer<Email>()){
			var message = record.value();

			System.out.println("-------------------------------------------------------");
			System.out.println("Processando novo pedido de compra, preparando e-mail");
			System.out.println(record.value());

			var pedido = message.getPayload();
			var titulo = String.format("O pedido %s foi recebido!", pedido.getPedidoId());
			var corpo = "Obrigado por seu pedido! Nós estamos processando-o";
			var emailCode = new Email(titulo, corpo);
			
			var id = message.getId().appendCorrelationId(EmailNovoPedidoServiceConsumer.class.getSimpleName());
			kafkaEmailServiceProducer.send(
					"ECOMMERCE_SEND_EMAIL", 
					pedido.getEmail(), 
					id,
					emailCode);

		}
		
	}
}
