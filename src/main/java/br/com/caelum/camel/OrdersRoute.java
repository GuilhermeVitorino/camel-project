package br.com.caelum.camel;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class OrdersRoute {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addComponent("activemq", ActiveMQComponent.activeMQComponent("tcp://localhost:61616"));

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {

				errorHandler(
						deadLetterChannel("activemq:queue:invoices.DLQ").
							maximumRedeliveries(3).//tente 3 vezes
							redeliveryDelay(5000). //espera 5 segundo entre as tentativas
						onRedelivery(new Processor(){
							@Override
							public void process (Exchange exchange) throws Exception {
								int counter = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER);
								int max = (int) exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER);
								System.out.println("Redelivery - " + counter + "/" + max );
							}
						})
				);

				from("activemq:queue:invoices").
					routeId("orders-route").
					to("validator:order.xsd").
					multicast().
						to("direct:soap").
						to("direct:http");

				from("direct:soap").
					routeId("soap-route").
					to("xslt:order-to-soap.xslt").
					log("calling soap service: ${body}").
					setHeader(Exchange.CONTENT_TYPE, constant("text/xml")).
				to("http4://localhost:8080/webservices/financial");

				from("direct:http").
					routeId("http-route").
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