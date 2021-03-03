package br.com.home.ecommerce.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalDatabase {
	
private final Connection connection;
	
	public LocalDatabase(String name) throws SQLException{
		String url = String.format("jdbc:sqlite:target/%s", name);
		connection = DriverManager.getConnection(url);
	}
	
	public void createIfNotExists(String sql) {
		try {
			connection.createStatement().execute(sql);
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean execute(String statement, String ... params) throws SQLException {
		return prepare(statement, params).execute();
	}

	public ResultSet executeQuery(String query, String ... params ) throws SQLException {
		return prepare(query, params).executeQuery();
	}
	
	private PreparedStatement prepare(String statement, String... params) throws SQLException {
		var preparedStatement = connection.prepareStatement(statement);
		AtomicInteger count = new AtomicInteger(0);
		
		List.of(params).forEach(param -> {
			try {
				preparedStatement.setString(count.getAndIncrement(), param);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		return preparedStatement;
	}
}
