import java.io.*;
import java.util.*;

public class Inventory {
    private List<ShoppingItem> items = new ArrayList<>();
    private final String FILE_PATH = "src/inventory.txt";
    private final String ALTERNATE_PATH = "inventory.txt";

    public Inventory() {
        loadItems();
    }

    private void loadItems() {
        items.clear();
        // Try loading from src directory first, then fallback to bin directory
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file = new File(ALTERNATE_PATH);
        }
        System.out.println("Loading inventory from: " + file.getAbsolutePath());
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines and comments
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    System.out.println("Loading item: " + parts[0].trim());
                    items.add(new ShoppingItem(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
            items.sort(Comparator.comparing(ShoppingItem::getName));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ShoppingItem searchItem(String name) {
        for (ShoppingItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(String name, String description, String imagePath) {
        items.add(new ShoppingItem(name, description, imagePath));
        items.sort(Comparator.comparing(ShoppingItem::getName));
        saveItems();
    }

    /**
     * Get a list of all product names in the inventory
     * @return List of product names
     */
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
