import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

import java.util.HashMap;

public class BillingSystem {
    private static HashMap<String, Integer> goods_price = new HashMap<>();


    public static void main(String[] args) throws Exception {
        goods_price.put("apple", 3);
        goods_price.put("tomato", 1);
        goods_price.put("orange", 2);
        goods_price.put("lemon", 1);
        goods_price.put("Kiwi", 1);
        goods_price.put("banana", 1);
        goods_price.put("broccoli", 2);
        goods_price.put("carrot", 1);
        goods_price.put("lettuce", 2);
        goods_price.put("potato", 2);
        System.out.println("Products' prices:\n" + goods_price);

        DefaultCamelContext context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:BillingQueue")
                .choice()
                    .when(header("validated"))
                    // TODO: invoke pay() in shopCustomers
                    .to("activemq:queue:shopCustomerQueue") // ??
                .endChoice().otherwise()
                    // TODO: invoke canBuy() in shopCustomers
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Order orderObject= exchange.getIn().getBody(Order.class);
                            String Product= orderObject.getProduct();
                            int Quantity= orderObject.getQuantity();
                            orderObject.setValid(true);
                            exchange.getIn().setBody(orderObject);
                        }
                    })
                    .to("activemq:queue:ResultQueue");
            }
        });

        context.start();
    }
}
