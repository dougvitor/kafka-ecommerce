package br.com.home.ecommerce;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import br.com.home.ecommerce.service.CorrelationId;
import br.com.home.ecommerce.service.KafkaServiceProducer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GeraRelatorioServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private final KafkaServiceProducer<String> batchServiceProducer = new KafkaServiceProducer<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			batchServiceProducer.send(
					"ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", 
					"ECOMMERCE_USER_GENERATE_READING_REPORT",
					new CorrelationId(GeraRelatorioServlet.class.getSimpleName()),
					"ECOMMERCE_USER_GENERATE_READING_REPORT");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		System.out.println("Enviado emails para todos os usuários");
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println("Relatórios gerados com sucesso!");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		batchServiceProducer.close();
	}

}
