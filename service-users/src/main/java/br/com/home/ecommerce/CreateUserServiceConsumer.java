package br.com.home.ecommerce;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import br.com.home.ecommerce.consumer.ConsumerService;
import br.com.home.ecommerce.consumer.ServiceRunner;
import br.com.home.ecommerce.database.LocalDatabase;
import br.com.home.ecommerce.model.PedidoCompra;

public class CreateUserServiceConsumer implements ConsumerService<PedidoCompra>{
	
	private LocalDatabase database;

	public CreateUserServiceConsumer() throws SQLException {
		database = new LocalDatabase("users_database.sqlite");
		
		String createTableSQL = "create table Usuario ("
				+ "uuid varchar(200) primary key,"
				+ "email varchar(200))";
		
		database.createIfNotExists(createTableSQL);
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
		var query = "select uuid from Usuario"
		+ " where email = ? limit 1";
		
		var results = database.executeQuery(query, email);
		return !results.next();
	}
	
	private void inseriNovoUsuario(String email) throws SQLException {
		var preparedStatement = "insert into Usuario (uuid, email) values (?,?)";
		var uuid = UUID.randomUUID().toString();
		
		database.execute(preparedStatement, uuid, email);
		
		System.out.println(String.format("Usuario %s e email %s adicionado", uuid, email));
	
	}

}
