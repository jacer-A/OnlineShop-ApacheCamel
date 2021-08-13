import java.io.Serializable;

public class Order implements Serializable {
    private int OrderID;
    private int CustomerID;
    private String FirstName;
    private String LastName;
    private String Product;
    private int Quantity;
    private boolean Valid;
    private boolean validationResult;

    public int getOrderID() { return OrderID; }
    public int getCustomerID() { return CustomerID; }
    public String getFirstName() { return FirstName; }
    public String getLastName() { return LastName; }
    public String getProduct() {return Product; }
    public int getQuantity() {return Quantity; }
    public boolean getValid() {return Valid; }
    public void setValid(boolean b) {Valid= b; }
    public boolean validated() {return validationResult; }
    public void validate() {validationResult= true; }

    @Override
    public String toString() {
        return "OrderID: " + OrderID + " | " +
                "Full name: " + FirstName + " " + LastName + ", " +
                "Product: " + Product + ", " +
                "Quantity: " + Quantity;
    }


    public Order(int CustomerID, String FirstName, String LastName,String Product, int Quantity) {
        this.CustomerID= CustomerID;
        this.FirstName= FirstName;
        this.LastName= LastName;
        this.Product= Product;
        this.Quantity= Quantity;
        this.OrderID= (int)(Math.random()*100000);
        this.Valid= true;
        this.validationResult= false;
    }
}
