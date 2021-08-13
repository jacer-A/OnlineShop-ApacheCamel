import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

import java.util.HashMap;

public class InventorySystem {
    private static HashMap<String, Integer> goods_quantity = new HashMap<>();

    public static void main(String[] args) throws Exception {
        goods_quantity.put("apple", 30);
        goods_quantity.put("tomato", 5);
        goods_quantity.put("orange", 20);
        goods_quantity.put("lemon", 20);
        goods_quantity.put("Kiwi", 30);
        goods_quantity.put("banana", 40);
        goods_quantity.put("broccoli", 15);
        goods_quantity.put("carrot", 30);
        goods_quantity.put("lettuce", 15);
        goods_quantity.put("potato", 30);
        System.out.println("Products left:\n" + goods_quantity);


        DefaultCamelContext context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:InventoryQueue")
                .choice()
                    .when(header("validated"))
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Order orderObject= exchange.getIn().getBody(Order.class);
                            String Product= orderObject.getProduct();
                            int Quantity= orderObject.getQuantity();
                            goods_quantity.put(Product, goods_quantity.get(Product)-Quantity);
                            exchange.getIn().setBody("Products left:\n" + goods_quantity);
                        }
                    })
                    .to("stream:out")
                .endChoice().otherwise()
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Order orderObject= exchange.getIn().getBody(Order.class);
                            String Product= orderObject.getProduct();
                            int Quantity= orderObject.getQuantity();
                            orderObject.setValid( goods_quantity.containsKey(Product) && goods_quantity.get(Product) >= Quantity);
                            exchange.getIn().setBody(orderObject);
                        }
                    })
                    .to("activemq:queue:ResultQueue");
            }
        });
        context.start();
    }
}
