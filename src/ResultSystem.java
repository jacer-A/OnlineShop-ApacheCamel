import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;


public class ResultSystem {
    public static class Aggregation implements AggregationStrategy {
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

            if (oldExchange != null) {
                Order orderObject= oldExchange.getIn().getBody(Order.class);
                Order orderObject2= newExchange.getIn().getBody(Order.class);
                if (orderObject.getValid() && orderObject2.getValid())
                        orderObject.validate();
                newExchange.getIn().setHeader("validated", orderObject.validated());
            }
            return newExchange;
        }
    }

    public static void main(String[] args) throws Exception {

        DefaultCamelContext context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Aggregator
                from("activemq:queue:ResultQueue")
                .aggregate(constant(0), new Aggregation()).completionSize(2)
                .choice()
                    .when(header("validated"))
                    .multicast()
                        .to("stream:out")
                        .to("activemq:queue:InventoryQueue")
                        .to("activemq:queue:BillingQueue")
                    .end()
                .endChoice().otherwise()
                    .to("stream:err");
            }
        });

        context.start();
    }
}
