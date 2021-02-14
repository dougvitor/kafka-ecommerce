package br.com.home.ecommerce.model;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class PedidoCompra {
	
	private final String userId;
	
	private final String pedidoId;
	
	private final BigDecimal total;

	private final String email;

	public PedidoCompra(String userId, String pedidoId, BigDecimal total, String email) {
		super();
		this.userId = userId;
		this.pedidoId = pedidoId;
		this.total = total;
		this.email = email;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}
	
}
