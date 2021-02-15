package br.com.home.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

import br.com.home.ecommerce.model.Email;
import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.service.KafkaServiceProducer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NovoPedidoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final KafkaServiceProducer<PedidoCompra> kafkaPedidoServiceProducer = new KafkaServiceProducer<>();

	private final KafkaServiceProducer<Email> kafkaEmailServiceProducer = new KafkaServiceProducer<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		var email = req.getParameter("email");
		var pedidoId = UUID.randomUUID().toString();
		var total = new BigDecimal(req.getParameter("valor"));

		var pedido = new PedidoCompra(pedidoId, total, email);

		kafkaPedidoServiceProducer.send("ECOMMERCE_NEW_ORDER", email, pedido);

		var titulo = String.format("O pedido %s foi recebido!", pedidoId);
		var corpo = "Obrigado por seu pedido! Nós estamos processando-o";
		var emailCode = new Email(titulo, corpo);

		kafkaEmailServiceProducer.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

		System.out.println("Processamento do novo pedido de compra finalizado!");

		resp.setStatus(HttpServletResponse.SC_OK);
		resp.getWriter().println("Processamento do novo pedido de compra finalizado!");
	}
	
	@Override
	public void destroy() {
		super.destroy();
		kafkaPedidoServiceProducer.close();
		kafkaEmailServiceProducer.close();
	}

}