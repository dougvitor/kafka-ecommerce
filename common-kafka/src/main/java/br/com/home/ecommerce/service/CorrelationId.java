package br.com.home.ecommerce.service;

import java.util.UUID;

public class CorrelationId {
	
	private final String id;
	
	public CorrelationId(String titulo) {
		id = String.format("%s(%s)", titulo, UUID.randomUUID().toString());
	}

	public String getId() {
		return id;
	}
	
	public CorrelationId appendCorrelationId(String titulo) {
		return new CorrelationId(String.format("%s/%s", id, titulo));
	}

	@Override
	public String toString() {
		return "CorrelationId [id=" + id + "]";
	}
	
}
