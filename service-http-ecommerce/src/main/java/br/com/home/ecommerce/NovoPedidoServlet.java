package br.com.home.ecommerce;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

import br.com.home.ecommerce.model.PedidoCompra;
import br.com.home.ecommerce.producer.KafkaServiceProducer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NovoPedidoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final KafkaServiceProducer<PedidoCompra> kafkaPedidoServiceProducer = new KafkaServiceProducer<>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			var email = req.getParameter("email");
			var pedidoId = req.getParameter("uuid");
			var total = new BigDecimal(req.getParameter("valor"));
			
			if(pedidoId == null || email == null || total == null) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().println("Parametros obrigatórios não foram enviados");
				return;
			}
			
			var pedido = new PedidoCompra(pedidoId, total, email);
			
			try(var database = new PedidoCompraDatabase()){
				if(database.saveNew(pedido)) {
					kafkaPedidoServiceProducer.send(
							"ECOMMERCE_NEW_ORDER", 
							email,
							new CorrelationId(NovoPedidoServlet.class.getSimpleName()),
							pedido);
					
					System.out.println("Processamento do novo pedido de compra finalizado!");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("Processamento do novo pedido de compra finalizado!");
				}else {
					System.out.println("Recebido pedido de compra já enviado anteriormente");
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.getWriter().println("Recebido pedido de compra já enviado anteriormente");
				}
			}
		} catch (InterruptedException | ExecutionException | SQLException e) {
			e.printStackTrace();
		}

		
	}
	
	@Override
	public void destroy() {
		super.destroy();
		kafkaPedidoServiceProducer.close();
	}

}
