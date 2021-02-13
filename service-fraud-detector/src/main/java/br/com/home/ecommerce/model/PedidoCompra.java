package br.com.home.ecommerce.model;

import java.math.BigDecimal;

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

	public BigDecimal getTotal() {
		return total;
	}

	@Override
	public String toString() {
		return "PedidoCompra [userId=" + userId + ", pedidoId=" + pedidoId + ", total=" + total + "]";
	}

	public String getUserId() {
		return userId;
	}
	
}
