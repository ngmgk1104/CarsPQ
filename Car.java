package cs1501_p3;

public class Car implements Car_Inter {
    private String vin;
    private String make;
    private String model;
    private int price;
    private int mileage;
    private String color;

    public Car(String vin, String make, String model, int price, int mileage, String color){
        this.vin = vin;
        this.make = make;
        this.model = model;
        this.price = price;
        this.mileage = mileage;
        this.color = color;
    }

    public String getVIN(){
        return this.vin;
    }

    public String getMake(){
        return this.make;
    }

    public String getModel(){
        return this.model;
    }

    public int getPrice(){
        return this.price;
    }

    public int getMileage(){
        return this.mileage;
    }

    public String getColor(){
        return this.color;
    }

    public void setPrice(int newPrice){
        this.price = newPrice;
    }

    public void setMileage(int newMileage){
        this.mileage = newMileage;
    }

    public void setColor(String newColor){
        this.color = newColor;
    }
}
