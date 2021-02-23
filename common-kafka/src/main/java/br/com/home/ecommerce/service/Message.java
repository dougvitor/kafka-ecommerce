package br.com.home.ecommerce.service;

public class Message<T> {
	
	private final CorrelationId id;
	
	private T payload;

	public Message(CorrelationId id, T payload) {
		super();
		this.id = id;
		this.payload = payload;
	}

	public CorrelationId getId() {
		return id;
	}
	
	public T getPayload() {
		return payload;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", payload=" + payload + "]";
	}
	
}
