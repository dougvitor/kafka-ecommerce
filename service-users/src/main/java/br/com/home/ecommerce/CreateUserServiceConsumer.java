package br.com.home.ecommerce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.consumer.ConsumerService;
import br.com.home.ecommerce.consumer.ServiceRunner;
import br.com.home.ecommerce.model.PedidoCompra;

public class CreateUserServiceConsumer implements ConsumerService<PedidoCompra>{
	
	private final Connection connection;
	
	private CreateUserServiceConsumer() throws SQLException{
		
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
	
	public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException {
		new ServiceRunner<>(CreateUserServiceConsumer::new).start(1);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_NEW_ORDER";
	}

	@Override
	public String getConsumerGroup() {
		return CreateUserServiceConsumer.class.getSimpleName();
	}
	
	@Override
	public void parse(ConsumerRecord<String, Message<PedidoCompra>> record) throws SQLException {
		
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
