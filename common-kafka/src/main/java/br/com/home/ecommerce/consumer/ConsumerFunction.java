package br.com.home.ecommerce.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.Message;

@FunctionalInterface
public interface ConsumerFunction<T> {
	
	void consume(ConsumerRecord<String, Message<T>> record) throws Exception;

}
