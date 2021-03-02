package br.com.home.ecommerce.consumer;

@FunctionalInterface
public interface ServiceFactory<T> {
	
	ConsumerService<T> create();

}