package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class OrdersRoute {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {

				from("file:input-orders?delay=5s&noop=true").
					setProperty("orderId", xpath("/order/id/text()")).
					setProperty("clientId", xpath("/order/payment/client-email/text()")).
					split().
						xpath("order/itens/item").
                    filter().
                        xpath("/item/format[text()='EBOOK']").
					setProperty("ebookId", xpath("/item/book/code/text()")).
						marshal().
						xmljson().
					setHeader(Exchange.HTTP_QUERY,
						simple("clientId=${property.clientId}&orderId=${property.orderId}&ebookId=${property.ebookId}")).
				to("http4://localhost:8080/webservices/ebook/item");
			}
		});

		context.start();
		Thread.sleep(20000);
		context.stop();
	}
}