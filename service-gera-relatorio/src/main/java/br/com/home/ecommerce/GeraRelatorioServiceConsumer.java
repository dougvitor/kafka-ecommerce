package br.com.home.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.model.User;
import br.com.home.ecommerce.service.KafkaServiceConsumer;

public class GeraRelatorioServiceConsumer {
	
	private static Path SOURCE =  new File("src/main/resources/relatorio.txt").toPath();

	public static void main(String[] args) {

		GeraRelatorioServiceConsumer fraudeService = new GeraRelatorioServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<User>(
				GeraRelatorioServiceConsumer.class.getSimpleName(), 
				"USER_GENERATE_READING_REPORT", 
				fraudeService::parse,
				User.class,
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}

	private void parse(ConsumerRecord<String, User> record) throws IOException {

		System.out.println("-------------------------------------------------------");
		User user = record.value();
		System.out.println(String.format("Processando relatorio para o usuário %s", user));
		
		var target = new File(user.getRelatorioPath());
		
		IO.copyTo(SOURCE, target);
		IO.append(target, String.format("Criado para o usuário %s", user.getUuid()));
		
		System.out.println(String.format("Arquivo do relatório gerado", target.getAbsolutePath()));
		
	}
}
