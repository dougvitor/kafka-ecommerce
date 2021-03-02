package br.com.home.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.Message;
import br.com.home.ecommerce.consumer.KafkaServiceConsumer;
import br.com.home.ecommerce.model.Usuario;

public class GeraRelatorioServiceConsumer {
	
	private static Path SOURCE =  new File("src/main/resources/relatorio.txt").toPath();

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		GeraRelatorioServiceConsumer fraudeService = new GeraRelatorioServiceConsumer();

		try (var kafkaServiceConsumer = new KafkaServiceConsumer<Usuario>(
				GeraRelatorioServiceConsumer.class.getSimpleName(), 
				"ECOMMERCE_USER_GENERATE_READING_REPORT", 
				fraudeService::parse,
				Map.of())) {
			kafkaServiceConsumer.run();
		}
	}

	private void parse(ConsumerRecord<String, Message<Usuario>> record) throws IOException {
		
		var message = record.value();

		System.out.println("-------------------------------------------------------");
		Usuario user = message.getPayload();
		System.out.println(String.format("Processando relatorio para o usuário %s", user.getUuid()));
		
		var target = new File(user.getRelatorioPath());
		
		IO.copyTo(SOURCE, target);
		IO.append(target, String.format("Criado para o usuário %s", user.getUuid()));
		
		System.out.println(String.format("Arquivo do relatório gerado %s", target.getAbsolutePath()));
		
	}
}
