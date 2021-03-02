package br.com.home.ecommerce;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import br.com.home.ecommerce.consumer.KafkaServiceConsumer;

public class ServiceProvider {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void run(ServiceFactory<T> factory) throws InterruptedException, ExecutionException {
		
		var service = factory.create();
		
		try (var kafkaServiceConsumer = new KafkaServiceConsumer(
				service.getConsumerGroup(),
				service.getTopic(), 
				service::parse, 
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}
}
