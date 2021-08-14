import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class WebOrderSystem {
    private static DefaultCamelContext context;

    private static void placeOrder(String order) {
        ProducerTemplate prodTemplate= context.createProducerTemplate();
        prodTemplate.sendBody("direct:start", order);
    }

    public static void main(String[] args) throws Exception {

        context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String msg= exchange.getIn().getBody(String.class);
                        String[] entries= msg.split(" ");
                        String order= entries[0] + ", " + entries[1] + ", " + entries[2] + ", " + entries[3] + ", " + entries[4];
                        exchange.getIn().setBody(order);
                    }
                })
                .to("activemq:queue:WebOrderTranslatorQueue");


                // WebOrderTranslator
                from("activemq:queue:WebOrderTranslatorQueue")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String order= exchange.getIn().getBody(String.class);
                        String[] entries= order.split(", ");
                        Order orderObject= new Order(Integer.parseInt(entries[4]), entries[0], entries[1], Integer.parseInt(entries[2]), Integer.parseInt(entries[3]));
                        exchange.getIn().setBody(orderObject);
                    }
                })
                .multicast()
                    .to("activemq:queue:BillingQueue")
                    .to("activemq:queue:InventoryQueue")
                .end();
            }
        });

        context.start();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.println("Order format: <FullName NumberOfChairs NumberOfTables>");
                System.out.println("Place your order:");
                String[] entries = reader.readLine().split(" ");

                String order = "";
                try {
                    int CustomerID= (int)(Math.random()*1000);
                    order += entries[0] + " " + entries[1] + " " + Integer.parseInt(entries[2]) + " " + Integer.parseInt(entries[3]) + " " + CustomerID;
                    placeOrder(order);
                    System.out.println("Your order has been issued !\n");
                } catch (Exception e) {
                    System.out.println("ERROR: Your order can't be processed\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
