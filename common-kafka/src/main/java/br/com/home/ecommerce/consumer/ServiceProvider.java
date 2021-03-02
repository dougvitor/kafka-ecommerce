package br.com.home.ecommerce.consumer;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void>{

	private final ServiceFactory<T> factory;

	public ServiceProvider(ServiceFactory<T> factory) {
		this.factory = factory;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Void call() throws InterruptedException, ExecutionException {
		
		var service = factory.create();
		
		try (var kafkaServiceConsumer = new KafkaServiceConsumer(
				service.getConsumerGroup(),
				service.getTopic(), 
				service::parse, 
				Map.of())) {
			kafkaServiceConsumer.run();
		}
		
		return null;
	}
}
