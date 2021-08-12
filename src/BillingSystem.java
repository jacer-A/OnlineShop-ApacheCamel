import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

public class BillingSystem {
    public static final int SurfboardPrice= 50;
    public static final int DivingSuitPrice= 100;

    /*
    public static class Customer {
        private int CustomerID;
        private String FullName;
        private int Money;

        public boolean canBuy(int Amount) { return Money>=Amount; }

        public Customer(int CustomerID, String FirstName, String LastName, int Money) {
            this.CustomerID= CustomerID;
            this.FullName= FirstName + " " + LastName;
            this.Money= Money;
        }
    }
    */

    public static void main(String[] args) throws Exception {
        DefaultCamelContext context= new DefaultCamelContext();

        ActiveMQComponent activeMqComp = ActiveMQComponent.activeMQComponent();
        context.addComponent("activemq", activeMqComp);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:BillingQueue")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        Order orderObject= exchange.getIn().getBody(Order.class);

                        int Money= 1000;

                        //Customer customer= new Customer(orderObject.getCustomerID(), orderObject.getFirstName(), orderObject.getLastName(), Money);

                        //orderObject.setValid(customer.canBuy(orderObject.getNumberOfSurfboards()*SurfboardPrice + orderObject.getNumberOfDivingSuits()*DivingSuitPrice));
                        exchange.getIn().setBody(orderObject);
                    }
                })
                .to("activemq:queue:ResultQueue");
            }
        });

        context.start();
    }
}
