package br.com.home.ecommerce.consumer;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.Message;

public interface ConsumerService<T> {
	
	void parse(ConsumerRecord<String, Message<T>> record) throws IOException;
	
	String getTopic();
	
	String getConsumerGroup();

}
