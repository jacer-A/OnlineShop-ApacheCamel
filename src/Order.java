import java.io.Serializable;

public class Order implements Serializable {
    private int CustomerID;
    private String FirstName;
    private String LastName;
    private int NumberOfChairs;
    private int NumberOfTables;
    private int OrderID;
    private boolean Valid;
    private boolean validationResult;

    public int getCustomerID() { return CustomerID; }
    public String getFirstName() { return FirstName; }
    public String getLastName() { return LastName; }
    public int getNumberOfChairs() {return NumberOfChairs; }
    public int getNumberOfTables() {return NumberOfTables; }
    public int getOrderID() { return OrderID; }
    public boolean getValid() {return Valid; }
    public void setValid(boolean b) {Valid= b; }
    public boolean validated() {return validationResult; }
    public void validate() {validationResult= true; }

    @Override
    public String toString() {
        return "OrderID: " + OrderID + " | " +
                "Full name: " + FirstName + " " + LastName + ", " +
                "Number Of Chairs: " + NumberOfChairs + ", " +
                "Number Of Tables: " + NumberOfTables;
    }


    public Order(int CustomerID, String FirstName, String LastName,int NumberOfChairs, int NumberOfTables) {
        this.CustomerID= CustomerID;
        this.FirstName= FirstName;
        this.LastName= LastName;
        this.NumberOfChairs= NumberOfChairs;
        this.NumberOfTables= NumberOfTables;
        this.OrderID= (int)(Math.random()*100000);
        this.Valid= true;
        this.validationResult= false;
    }
}