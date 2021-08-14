import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public class WebOrderSystem {

    private static HashMap<Integer, String> Customers= new HashMap<>();
                      // <CustomerID, fullname>

    private static DefaultCamelContext context;

    private static void placeOrder(String order) {
        ProducerTemplate prodTemplate = context.createProducerTemplate();
        prodTemplate.sendBody("direct:start", order);
    }


    public static void main(String[] args) throws Exception {

        context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                // Endpoint
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
                        Order orderObject= new Order(Integer.parseInt(entries[0]), entries[1], entries[2], entries[3], Integer.parseInt(entries[4]));
                        exchange.getIn().setBody(orderObject);
                        exchange.getIn().setHeader("validated", false);
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
                int CustomerID; String firstName=""; String lastName="";

                System.out.println("Provide your CustomerID or type 'NEW' if you're a new customer");
                String[] entries = reader.readLine().split(" ");
                try {
                    CustomerID = Integer.parseInt(entries[0]);
                    if ( ! Customers.containsKey(CustomerID) ) {
                        System.err.println("ERROR: CustomerID non existent. Try again please.\n");
                        continue;
                    }
                } catch (Exception e1) {
                    if (entries[0].equals("NEW") ) {
                        System.out.println("Type your first name and last name: <firstName lastName>");
                        entries = reader.readLine().split(" ");
                        try {
                            firstName= entries[0];
                            lastName = entries[1];
                            CustomerID= (int)(Math.random()*1000);
                            Customers.put(CustomerID, firstName + " " + lastName );
                            System.out.println("Your CustomerID is: " + CustomerID);
                        } catch (Exception e2) {
                            System.err.println("ERROR: False input. Try again please.\n");
                            continue;
                        }
                    } else {
                        continue;
                    }
                }

                while (true) {
                    System.out.println("Place your order using the format: <itemName quantity>.\nOr type 'CANCEL' to go back.");
                    entries = reader.readLine().split(" ");

                    try {
                        if (entries[0].equals("CANCEL") ) {
                            break;
                        }
                        String order = entries[0] + " " + Integer.parseInt(entries[1]);
                        placeOrder(CustomerID + " " + firstName + " " + lastName + " " + order);
                        System.out.println("Your order has been issued !\n");
                    } catch (Exception e) {
                        System.err.println("ERROR: Your order can't be processed\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
