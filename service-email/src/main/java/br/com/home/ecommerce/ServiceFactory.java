package br.com.home.ecommerce;

public interface ServiceFactory<T> {
	
	ConsumerService<T> create();

}
