package br.com.home.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.producer.KafkaServiceProducer;

public class NewOrderMainProducer {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		try (var kafkaPedidoServiceProducer = new KafkaServiceProducer<PedidoCompra>()) {
			var email = Math.random() + "@email.com";
			for (int i = 0; i < 10; i++) {

				var pedidoId = UUID.randomUUID().toString();
				var total = new BigDecimal(Math.random() * 5000 + 1);
				var pedido = new PedidoCompra(pedidoId, total, email);
				var id = new CorrelationId(NewOrderMainProducer.class.getSimpleName());

				kafkaPedidoServiceProducer.send("ECOMMERCE_NEW_ORDER", email, id, pedido);

			}
		}

	}
}
