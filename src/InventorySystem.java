import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

public class InventorySystem {
    public static void main(String[] args) throws Exception {
        int AvailableSurfboards= 15;
        int AvailableDivingSuits= 15;

        DefaultCamelContext context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:InventoryQueue")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Order orderObject= exchange.getIn().getBody(Order.class);

                        orderObject.setValid(orderObject.getNumberOfSurfboards()<=AvailableSurfboards && orderObject.getNumberOfDivingSuits()<=AvailableDivingSuits);
                        exchange.getIn().setBody(orderObject);
                    }
                })
                .to("activemq:queue:ResultQueue");
            }
        });

        context.start();
    }
}
