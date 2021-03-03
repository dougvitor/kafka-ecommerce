package br.com.home.ecommerce;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import br.com.home.ecommerce.database.LocalDatabase;
import br.com.home.ecommerce.model.PedidoCompra;

public class PedidoCompraDatabase implements Closeable{
	
	private LocalDatabase database;

	public PedidoCompraDatabase() throws SQLException {
		database = new LocalDatabase("orders_database.sqlite");
		String createTableSQL = "create table Pedido (uuid varchar(200) primary key)";
		database.createIfNotExists(createTableSQL);
	}

	public boolean saveNew(PedidoCompra pedidoCompra) throws SQLException {
		if(isProcessada(pedidoCompra)) {
			return false;
		}
		
		var statement = "insert into Pedido (uuid) values (?)";
		database.execute(statement, pedidoCompra.getPedidoId());
		return true;
	}
	
	private boolean isProcessada(PedidoCompra pedidoCompra) throws SQLException {
		var querySQL = "select uuid from Pedido where uuid = ? limit 1";
		var result = database.executeQuery(querySQL, pedidoCompra.getPedidoId());
		return result.next();
	}

	@Override
	public void close() throws IOException {
		try {
			database.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}
}
