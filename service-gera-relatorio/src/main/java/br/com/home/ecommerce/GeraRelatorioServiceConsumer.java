package br.com.home.ecommerce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import br.com.home.ecommerce.consumer.ConsumerService;
import br.com.home.ecommerce.consumer.ServiceRunner;
import br.com.home.ecommerce.model.Usuario;

public class GeraRelatorioServiceConsumer implements ConsumerService<Usuario>{
	
	private static Path SOURCE =  new File("src/main/resources/relatorio.txt").toPath();

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		new ServiceRunner<>(GeraRelatorioServiceConsumer::new).start(5);
	}
	
	@Override
	public String getTopic() {
		return "ECOMMERCE_USER_GENERATE_READING_REPORT";
	}

	@Override
	public String getConsumerGroup() {
		return GeraRelatorioServiceConsumer.class.getSimpleName();
	}

	public void parse(ConsumerRecord<String, Message<Usuario>> record) throws IOException {
		
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
