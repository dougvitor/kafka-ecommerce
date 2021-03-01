package br.com.home.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.service.KafkaServiceConsumer;
import br.com.home.ecommerce.service.Message;

public class CreateUserServiceConsumer {
	
	private final Connection connection;
	
	public CreateUserServiceConsumer() throws SQLException{
		
		String url = "jdbc:sqlite:target/users_database.db";
		connection = DriverManager.getConnection(url);
		try {
			String createTableSQL = "create table Usuario ("
					+ "uuid varchar(200) primary key,"
					+ "email varchar(200))";
			connection.createStatement().execute(createTableSQL);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SQLException {

		CreateUserServiceConsumer userService = new CreateUserServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<PedidoCompra>(
				CreateUserServiceConsumer.class.getSimpleName(), 
				"ECOMMERCE_NEW_ORDER", 
				userService::parse,
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}
	
	private void parse(ConsumerRecord<String, Message<PedidoCompra>> record) throws SQLException {
		
		var message = record.value();
		
		System.out.println("-------------------------------------------------------");
		System.out.println("Processando novo pedido, chegando possiveis fraudes");
		System.out.println(record.value());
		
		var pedidoCompra = message.getPayload();
		
		if(isNovoUsuario(pedidoCompra.getEmail())) {
			inseriNovoUsuario(pedidoCompra.getEmail());
		}
	}

	private boolean isNovoUsuario(String email) throws SQLException {
		var selectSQL = "select uuid from Usuario"
		+ " where email = ? limit 1";
		var existsStatement = connection.prepareStatement(selectSQL);
		existsStatement.setString(1, email);
		return !existsStatement.executeQuery().next();
	}
	
	private void inseriNovoUsuario(String email) throws SQLException {
		var insertSQL = "insert into Usuario (uuid, email)"
				+ "values (?,?)";
		
		var insertStatement = connection.prepareStatement(insertSQL);
		String uuid = UUID.randomUUID().toString();
		insertStatement.setString(1, uuid);
		insertStatement.setString(2, email);
		
		insertStatement.execute();
		
		System.out.println(String.format("Usuario %s e email %s adicionado", uuid, email));
	
	}
	
}
