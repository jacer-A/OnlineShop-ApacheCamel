import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;

import java.util.HashMap;

public class BillingSystem {

    public static class Customer {
        private int CustomerID;
        private String firstName;
        private String lastName;
        private int Money;
        private static HashMap<String, Integer> goods_quantity = new HashMap<>();

        public boolean canBuy(int Amount) { return Money>=Amount; }
        public void pay(int Amount, Order Order) {
            Money -= Amount;
            goods_quantity.put(Order.getProduct(), Order.getQuantity());
        }

        public Customer(int CustomerID, String FirstName, String LastName) {
            this.CustomerID= CustomerID;
            this.firstName= FirstName;
            this.lastName= LastName;
            this.Money= (int)(Math.random()*1000);
        }

        @Override
        public String toString() {
            return  "CustomerID= " + CustomerID +
                    ", fullName= " + firstName + " " + lastName +
                    ", Money= " + Money +
                    ", goods= " + goods_quantity +
                    "\n";
        }
    }
    private static HashMap<Integer, Customer> Customers= new HashMap<>();

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
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Order orderObject= exchange.getIn().getBody(Order.class);

                            int CustomerID= orderObject.getCustomerID();
                            Customer customer= Customers.get(CustomerID);

                            String Product= orderObject.getProduct();
                            int Quantity= orderObject.getQuantity();
                            customer.pay(goods_price.get(Product)*Quantity, orderObject);

                            exchange.getIn().setBody(Customers);
                        }
                    })
                    .to("stream:out")
                .endChoice().otherwise()
                    .process(new Processor() {
                        @Override
                        public void process(Exchange exchange) throws Exception {
                            Order orderObject= exchange.getIn().getBody(Order.class);

                            int CustomerID= orderObject.getCustomerID();
                            String firstName= orderObject.getFirstName();
                            String lastName= orderObject.getLastName();
                            Customer newCustomer= new Customer(CustomerID, firstName, lastName);
                            if ( ! Customers.containsKey(CustomerID))
                                Customers.put(CustomerID, newCustomer);

                            String Product= orderObject.getProduct();
                            int Quantity= orderObject.getQuantity();
                            orderObject.setValid(newCustomer.canBuy(goods_price.get(Product)*Quantity));

                            exchange.getIn().setBody(orderObject);
                        }
                    })
                    .to("activemq:queue:ResultQueue");
            }
        });

        context.start();
    }
}
