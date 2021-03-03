package br.com.home.ecommerce.model;

import java.math.BigDecimal;

public class PedidoCompra {
	
	private final String pedidoId;
	
	private final BigDecimal total;

	private final String email;

	public PedidoCompra(String pedidoId, BigDecimal total, String email) {
		super();
		this.pedidoId = pedidoId;
		this.total = total;
		this.email = email;
	}
	
	public String getPedidoId() {
		return pedidoId;
	}

	public BigDecimal getTotal() {
		return total;
	}
	
	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "PedidoCompra [pedidoId=" + pedidoId + ", total=" + total + ", email=" + email + "]";
	}

}
