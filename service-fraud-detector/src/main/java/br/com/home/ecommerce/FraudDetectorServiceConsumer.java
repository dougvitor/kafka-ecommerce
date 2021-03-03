package br.com.home.ecommerce;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.consumer.ConsumerService;
import br.com.home.ecommerce.consumer.ServiceRunner;
import br.com.home.ecommerce.database.LocalDatabase;
import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.producer.KafkaServiceProducer;

public class FraudDetectorServiceConsumer implements ConsumerService<PedidoCompra>{
	
	private final KafkaServiceProducer<PedidoCompra> producer = new KafkaServiceProducer<>();
	
	private LocalDatabase database;

	public FraudDetectorServiceConsumer() throws SQLException {
		database = new LocalDatabase("frauds_database.sqlite");
		String createTableSQL = "create table Pedido (uuid varchar(200) primary key, is_fraud boolean)";
		database.createIfNotExists(createTableSQL);
	}

	public static void main(String[] args){
		new ServiceRunner<>(FraudDetectorServiceConsumer::new).start(1);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return FraudDetectorServiceConsumer.class.getSimpleName();
	}

	@Override
	public void parse(ConsumerRecord<String, Message<PedidoCompra>> record) throws InterruptedException, ExecutionException, SQLException {
		
		var message = record.value();

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
		
		var pedidoCompra = message.getPayload();
		
		if(isProcessada(pedidoCompra)) {
			System.out.println(String.format("Pedido %s já foi processado anteriormente.", pedidoCompra.getPedidoId()));
			return;
		}
		
		var statement = "";
		
		if(isFraude(pedidoCompra)) {
			System.out.println("É uma fraude!!!");
			
			statement = "insert into Pedido (uuid, is_fraud) values (?, true)";
			
			producer.send(
					"ECOMMERCE_ORDER_REJECT", 
					pedidoCompra.getEmail(),
					message.getId().appendCorrelationId(FraudDetectorServiceConsumer.class.getSimpleName()),
					pedidoCompra);
		}else {
			System.out.println("Aprovado: " + pedidoCompra);
			
			statement = "insert into Pedido (uuid, is_fraud) values (?, false)";
			
			producer.send(
					"ECOMMERCE_ORDER_APPROVED", 
					pedidoCompra.getEmail(), 
					message.getId().appendCorrelationId(FraudDetectorServiceConsumer.class.getSimpleName()),
					pedidoCompra);
		}
		
		database.execute(statement, pedidoCompra.getPedidoId());

		System.out.println("Pedido Processado");
	}

	private boolean isFraude(PedidoCompra pedidoCompra) {
		return pedidoCompra.getTotal().compareTo(new BigDecimal("4500")) > 0;
	}
	
	private boolean isProcessada(PedidoCompra pedidoCompra) throws SQLException {
		var querySQL = "select uuid from Pedido where uuid = ? limit 1";
		var result = database.executeQuery(querySQL, pedidoCompra.getPedidoId());
		return result.next();
	}
}
