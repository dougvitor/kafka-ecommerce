package br.com.home.ecommerce.model;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class PedidoCompra {
	
	private String userId;
	
	private String pedidoId;
	
	private BigDecimal total;

	public PedidoCompra(String userId, String pedidoId, BigDecimal total) {
		super();
		this.userId = userId;
		this.pedidoId = pedidoId;
		this.total = total;
	}

	public String getEmail() {
		return "email@gmail.com";
	}
	
}
