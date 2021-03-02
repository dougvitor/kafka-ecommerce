package br.com.home.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.Message;
import br.com.home.ecommerce.consumer.KafkaServiceConsumer;
import br.com.home.ecommerce.model.Usuario;
import br.com.home.ecommerce.producer.KafkaServiceProducer;

public class BatchEnvioMensagemServiceConsumer {

	private final Connection connection;
	
	private final KafkaServiceProducer<Usuario> usuarioServiceProducer = new KafkaServiceProducer<>();

	public BatchEnvioMensagemServiceConsumer() throws SQLException {

		String url = "jdbc:sqlite:target/users_database.db";
		connection = DriverManager.getConnection(url);
		try {
			String createTableSQL = "create table Usuario (" + "uuid varchar(200) primary key," + "email varchar(200))";
			connection.createStatement().execute(createTableSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException {

		BatchEnvioMensagemServiceConsumer batchService = new BatchEnvioMensagemServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<String>(
				BatchEnvioMensagemServiceConsumer.class.getSimpleName(), 
				"ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", 
				batchService::parse,
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}

	private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException {
		
		var message = record.value();

		System.out.println("-------------------------------------------------------");
		System.out.println("Processando novo batch");
		System.out.println(String.format("Tópico: %s", message.getPayload()));
		
		for(Usuario user : getAllUsers()) {
			usuarioServiceProducer.sendAsync(
					message.getPayload(), 
					user.getUuid(),
					message.getId().appendCorrelationId(BatchEnvioMensagemServiceConsumer.class.getSimpleName()),
					user);
		}

	}

	private List<Usuario> getAllUsers() throws SQLException {
		
		var results = connection.prepareStatement("select uuid from Usuario").executeQuery();
		List<Usuario> users = new ArrayList<>();
		
		while(results.next()) {
			users.add(new Usuario(results.getString(1)));
		}
		
		return users;
	}

}
