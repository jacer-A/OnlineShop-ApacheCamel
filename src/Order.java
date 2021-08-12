import java.io.Serializable;

public class Order implements Serializable {
    private int CustomerID;
    private String FirstName;
    private String LastName;
    private int OverallItems;
    private int NumberOfSurfboards;
    private int NumberOfDivingSuits;
    private int OrderID;
    private boolean Valid;
    private boolean validationResult;

    public int getCustomerID() { return CustomerID; }
    public String getFirstName() { return FirstName; }
    public String getLastName() { return LastName; }
    public int getNumberOfSurfboards() {return NumberOfSurfboards; }
    public int getNumberOfDivingSuits() {return NumberOfDivingSuits; }
    public int getOrderID() { return OrderID; }
    public boolean getValid() {return Valid; }
    public void setValid(boolean b) {Valid= b; }
    public boolean validated() {return validationResult; }
    public void validate() {validationResult= true; }

    @Override
    public String toString() {
        return "OrderID: " + OrderID + " | " +
                "Full name: " + FirstName + " " + LastName + ", " +
                "Number Of Surfboards: " + NumberOfSurfboards + ", " +
                "Number Of DivingSuits: " + NumberOfDivingSuits;
    }


    public Order(int CustomerID, String FirstName, String LastName,int NumberOfSurfboards, int NumberOfDivingSuits) {
        this.CustomerID= CustomerID;
        this.FirstName= FirstName;
        this.LastName= LastName;
        this.OverallItems= NumberOfDivingSuits + NumberOfSurfboards;
        this.NumberOfSurfboards= NumberOfSurfboards;
        this.NumberOfDivingSuits= NumberOfDivingSuits;
        this.OrderID= (int)(Math.random()*100000);
        this.Valid= true;
        this.validationResult= false;
    }
}