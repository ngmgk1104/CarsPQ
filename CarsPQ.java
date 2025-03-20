package cs1501_p3;

import java.util.Iterator;
import java.util.Comparator;
import java.io.*;
import java.util.NoSuchElementException;

public class CarsPQ implements CarsPQ_Inter {
    private Car[] priceHeap;
    private Car[] mileageHeap;
    private int size;
    private int capacity;
    
    public CarsPQ(String filename) {
        capacity = 10;
        size = 0;
        priceHeap = new Car[capacity];
        mileageHeap = new Car[capacity];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(":");
                Car car = new Car(parts[0], parts[1], parts[2], 
                                Integer.parseInt(parts[3]), 
                                Integer.parseInt(parts[4]), 
                                parts[5]);
                add(car);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("File not found: " + filename + ". Initializing empty CarsPQ.");
            e.printStackTrace();
        }
    }
    
    public void add(Car c) throws IllegalStateException {
        try {
            // Check if car with same VIN already exists
            get(c.getVIN());
            // If car found, throw exception
            throw new IllegalStateException("Car with VIN " + c.getVIN() + " already exists");
        } catch (NoSuchElementException e) {
            // If heaps are full, double their size
            if (size == capacity) resize();
            // Add new car to both heaps at the end
            priceHeap[size] = c;
            mileageHeap[size] = c;
            // Increment size counter
            size++;
            // Maintain heap properties by bubbling up the new car
            bubbleUpPrice(size - 1);
            bubbleUpMileage(size - 1);
        }
    }
    /*Method was included because Aidan test file need it
    public boolean contains(String vin) {
        try {
            get(vin);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }*/
    
    public void updatePrice(String vin, int newPrice) throws NoSuchElementException {
        Car car = get(vin);
        car.setPrice(newPrice);
        rebuildHeaps();
    }
    
    public void updateMileage(String vin, int newMileage) throws NoSuchElementException {
        Car car = get(vin);
        car.setMileage(newMileage);
        rebuildHeaps();
    }
    
    public void updateColor(String vin, String newColor) throws NoSuchElementException {
        Car car = get(vin);
        car.setColor(newColor);
    }
    
    public Car get(String vin) throws NoSuchElementException {
        // Iterate through all cars in the price heap
        for (int i = 0; i < size; i++) {
            // Check if current car's VIN matches the searched VIN and return the car if found
            if (priceHeap[i].getVIN().equals(vin)) return priceHeap[i];
        }
        // If car not found, throw exception
        throw new NoSuchElementException("Car with VIN " + vin + " not found");
    }
    
    public void remove(String vin) throws NoSuchElementException {
        // Initialize index to track position of car to remove in price heap
        int priceIndex = -1;
        // Search for car with matching VIN in price heap
        for (int i = 0; i < size; i++) {
            // Check for null and matching VIN
            if (priceHeap[i] != null && priceHeap[i].getVIN().equals(vin)) {
                // Store index when car is found
                priceIndex = i;
                break;
            }
        }
        // If car wasn't found (index still -1), throw exception
        if (priceIndex == -1) throw new NoSuchElementException("Car with VIN " + vin + " not found");
        
        // Find the same car in the mileage heap
        int mileageIndex = -1;
        for (int i = 0; i < size; i++) {
            if (mileageHeap[i] != null && mileageHeap[i].getVIN().equals(vin)) {
                mileageIndex = i;
                break;
            }
        }
        
        // Decrease size as we're removing an element
        size--;
        
        // Replace removed car with last car in price heap
        priceHeap[priceIndex] = priceHeap[size];
        priceHeap[size] = null;
        
        // Replace removed car with last car in mileage heap
        mileageHeap[mileageIndex] = mileageHeap[size];
        mileageHeap[size] = null;
        
        // Maintain heap properties by heapifying from the affected indices
        if (size > 0) {
            heapifyPrice(priceIndex);
            heapifyMileage(mileageIndex);
        }
    }
    
    public Car getLowPrice() {
        // Return root of price heap if not empty, null otherwise
        if (size > 0) return priceHeap[0];
        else return null;
    }
    
    public Car getLowPrice(String make, String model) {
        // Initialize variable to store lowest price car found
        Car lowest = null;
        // Iterate through all cars in price heap
        for (int i = 0; i < size; i++) {
            Car car = priceHeap[i];
            // Check if current car matches make and model
            if (car.getMake().equals(make) && car.getModel().equals(model)) {
                // Update lowest if this is first match or has lower price
                if (lowest == null || car.getPrice() < lowest.getPrice()) {
                    lowest = car;
                }
            }
        }
        // Return the lowest price car found (or null if none found)
        return lowest;
    }
    
    public Car getLowMileage() {
        // Return root of mileage heap if not empty, null otherwise
        if (size > 0) return mileageHeap[0];
        else return null;
    }
    
    public Car getLowMileage(String make, String model) {
        // Initialize variable to store lowest mileage car found
        Car lowest = null;
        // Iterate through all cars in mileage heap
        for (int i = 0; i < size; i++) {
            Car car = mileageHeap[i];
            // Check if current car matches make and model
            if (car.getMake().equals(make) && car.getModel().equals(model)) {
                // Update lowest if this is first match or has lower mileage
                if (lowest == null || car.getMileage() < lowest.getMileage()) {
                    lowest = car;
                }
            }
        }
        // Return the lowest mileage car found (or null if none found)
        return lowest;
    }
    
    private void resize() {
        // Double the capacity
        capacity *= 2;
        // Create new arrays with doubled size
        Car[] newPriceHeap = new Car[capacity];
        Car[] newMileageHeap = new Car[capacity];
        // Copy existing elements to new arrays
        System.arraycopy(priceHeap, 0, newPriceHeap, 0, size);
        System.arraycopy(mileageHeap, 0, newMileageHeap, 0, size);
        // Update heap references to new arrays
        priceHeap = newPriceHeap;
        mileageHeap = newMileageHeap;
    }
    
    private void bubbleUpPrice(int index) {
        // Continue while not at root and parent is larger
        while (index > 0) {
            // Calculate parent index
            int parent = (index - 1) / 2;
            // If current price less than parent's price, swap
            if (priceHeap[index].getPrice() < priceHeap[parent].getPrice()) {
                Car temp = priceHeap[index];
                priceHeap[index] = priceHeap[parent];
                priceHeap[parent] = temp;
                // Move up to parent position
                index = parent;
            } else {
                // Heap property satisfied, stop bubbling
                break;
            }
        }
    }
    
    private void bubbleUpMileage(int index) {
        // Continue while not at root and parent is larger
        while (index > 0) {
            // Calculate parent index
            int parent = (index - 1) / 2;
            // If current mileage less than parent's mileage, swap
            if (mileageHeap[index].getMileage() < mileageHeap[parent].getMileage()) {
                Car temp = mileageHeap[index];
                mileageHeap[index] = mileageHeap[parent];
                mileageHeap[parent] = temp;
                // Move up to parent position
                index = parent;
            } else {
                // Heap property satisfied, stop bubbling
                break;
            }
        }
    }
    
    private void rebuildHeaps() {
        // Start from last non-leaf node and work up to root
        for (int i = size / 2 - 1; i >= 0; i--) {
            // Heapify both price and mileage heaps
            heapifyPrice(i);
            heapifyMileage(i);
        }
    }
    
    private void heapifyPrice(int index) {
        // Start with current index as smallest
        int smallest = index;
        // Calculate left and right child indices
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        // If left child exists and is smaller than current smallest
        if (left < size && priceHeap[left].getPrice() < priceHeap[smallest].getPrice()) {
            smallest = left;
        }
        // If right child exists and is smaller than current smallest
        if (right < size && priceHeap[right].getPrice() < priceHeap[smallest].getPrice()) {
            smallest = right;
        }
        // If smallest is not current index, swap and continue heapifying
        if (smallest != index) {
            Car temp = priceHeap[index];
            priceHeap[index] = priceHeap[smallest];
            priceHeap[smallest] = temp;
            heapifyPrice(smallest);
        }
    }
    
    private void heapifyMileage(int index) {
        // Start with current index as smallest
        int smallest = index;
        // Calculate left and right child indices
        int left = 2 * index + 1;
        int right = 2 * index + 2;
        // If left child exists and is smaller than current smallest
        if (left < size && mileageHeap[left].getMileage() < mileageHeap[smallest].getMileage()) {
            smallest = left;
        }
        // If right child exists and is smaller than current smallest
        if (right < size && mileageHeap[right].getMileage() < mileageHeap[smallest].getMileage()) {
            smallest = right;
        }
        // If smallest is not current index, swap and continue heapifying
        if (smallest != index) {
            Car temp = mileageHeap[index];
            mileageHeap[index] = mileageHeap[smallest];
            mileageHeap[smallest] = temp;
            heapifyMileage(smallest);
        }
    }
    
}