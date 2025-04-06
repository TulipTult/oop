// imports all java io packages and util packages
import java.io.*;
import java.util.*;

public class Inventory {

    //vars
    private List<ShoppingItem> items = new ArrayList<>();
    private final String FILE_PATH = "src/inventory.txt";
    private final String ALTERNATE_PATH = "inventory.txt";

    //inits loadItems
    //constructor
    public Inventory() {
        loadItems();
    }

    //loads items from inventory.txt file
    private void loadItems() {
        // Clear the existing items list to avoid duplicates
        items.clear();
        // Try loading from src directory first; load from bin if not found
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file = new File(ALTERNATE_PATH);
        }
        //load message
        System.out.println("Loading inventory from: " + file.getAbsolutePath());
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines and comments
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }
                
                // Split the line by '|' and adds each item to "parts" array
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    System.out.println("Loading item: " + parts[0].trim());
                    items.add(new ShoppingItem(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
            // Sort items by name after loading
            // javas .sort method uses a algo called TimSort which is a hybrid of merge sort and insertion sort
            // Timsort time: O(n log n) (worst case) O(n) (best case)
            // Comparator is a healper class that defines how two objects compare
            items.sort(Comparator.comparing(ShoppingItem::getName));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // searches for an item by name
    public ShoppingItem searchItem(String name) {

        int begin = 0;
        int end = items.size() - 1;
        int mid;

        // Binary search for the item by name
        while (begin <= end) {
            mid = (begin + end) / 2;
            if (items.get(mid).getName().equalsIgnoreCase(name)) {
                return items.get(mid);
            } else if (items.get(mid).getName().compareToIgnoreCase(name) < 0) {
                begin = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return null; 
    }

    public void addItem(String name, String description, String imagePath) {
        items.add(new ShoppingItem(name, description, imagePath));
        items.sort(Comparator.comparing(ShoppingItem::getName));
        saveItems();
    }


     // Get a list of all product names in the inventory
     // @return List of product names
    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        for (ShoppingItem item : items) {
            names.add(item.getName());
        }
        return names;
    }

    private void saveItems() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file = new File(ALTERNATE_PATH);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (ShoppingItem item : items) {
                bw.write(item.getName() + " | " + item.getDescription() + " | " + item.getImagePath());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
