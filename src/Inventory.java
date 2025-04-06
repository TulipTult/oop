// packages and imports
import java.io.*;
import java.util.*;

public class Inventory {

    private List<ShoppingItem> items = new ArrayList<>();
    //paths:
    private final String FILE_PATH = "src/inventory.txt";
    private final String ALTERNATE_PATH = "inventory.txt";
    
    public enum SortAlgorithm {
        DEFAULT, INSERTION, BUBBLE
    }
    

    public enum SearchAlgorithm {
        BINARY, LINEAR
    }
    
    // current sort method
    private SortAlgorithm currentSortAlgorithm = SortAlgorithm.DEFAULT;
    // current search method
    private SearchAlgorithm currentSearchAlgorithm = SearchAlgorithm.BINARY;

    public Inventory() {
        loadItems();
    }
    
    // Cchange sort
    public void setSortAlgorithm(SortAlgorithm algorithm) {
        this.currentSortAlgorithm = algorithm;
        System.out.println("Now sorting with: " + algorithm);
    }
    
    // Change search
    public void setSearchAlgorithm(SearchAlgorithm algorithm) {
        this.currentSearchAlgorithm = algorithm;
        System.out.println("Now searching with: " + algorithm);
    }

    // read items from inventory.txt
    private void loadItems() {
        //clear list
        items.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file = new File(ALTERNATE_PATH);
        }
        System.out.println("Getting items from: " + file.getAbsolutePath());
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // clean up 
                if (line.trim().isEmpty() || line.trim().startsWith("//")) {
                    continue;
                }
                
                // Ssplit the line into parts
                // format: name | description | imagePath
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    System.out.println("Found item: " + parts[0].trim());
                    items.add(new ShoppingItem(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
            // sort items after loading
            sortItems();
        } catch (IOException e) {
            System.err.println("Oops! Couldn't read the file: " + e.getMessage());
        }
    }
    
    // Sort items using whichever method was picked
    private void sortItems() {
        switch (currentSortAlgorithm) {
            case DEFAULT:
                // Tim sort worse case O(n log n), best case O(n)
                items.sort(Comparator.comparing(ShoppingItem::getName));
                break;
            case INSERTION:
                // Insertion sort - O(n^2) worst case, O(n) best case (ts case already sorted so it should be O(log n))
                insertionSort();
                break;
            case BUBBLE:
                // why ever use this?
                bubbleSort();
                break;
        }
    }
    
    
    private void insertionSort() {
        for (int i = 1; i < items.size(); i++) {

            ShoppingItem key = items.get(i);
            int j = i - 1;
            

            while (j >= 0 && items.get(j).getName().compareToIgnoreCase(key.getName()) > 0) {
                items.set(j + 1, items.get(j));
                j--;
            }

            items.set(j + 1, key);
        }
    }
    

    private void bubbleSort() {
        int n = items.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (items.get(j).getName().compareToIgnoreCase(items.get(j + 1).getName()) > 0) {
                    ShoppingItem temp = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temp);
                }
            }
        }
    }
    public ShoppingItem searchItem(String name) {
        return currentSearchAlgorithm == SearchAlgorithm.BINARY ? 
            binarySearch(name) : linearSearch(name);
    }
    
    private ShoppingItem binarySearch(String name) {
        int begin = 0;
        int end = items.size() - 1;
        int mid;

        while (begin <= end) {
            // Look at the mid
            mid = (begin + end) / 2;
            if (items.get(mid).getName().equalsIgnoreCase(name)) {
               
                return items.get(mid);
            } else if (items.get(mid).getName().compareToIgnoreCase(name) < 0) {
                
                begin = mid + 1;
            } else {
                
                end = mid - 1;
            }
        }
        // not found
        return null;
    }
    
    // why use this too?
    private ShoppingItem linearSearch(String name) {
        for (ShoppingItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        // not found
        return null;
    }

    // Add a new item to our inventory
    public void addItem(String name, String description, String imagePath) {
        ShoppingItem newItem = new ShoppingItem(name, description, imagePath);
        

        switch (currentSortAlgorithm) {
            case DEFAULT:
                items.add(newItem);
                items.sort(Comparator.comparing(ShoppingItem::getName));
                break;
            case INSERTION:
                if (items.isEmpty() || newItem.getName().compareToIgnoreCase(items.get(items.size() - 1).getName()) > 0) {
                    items.add(newItem);
                } else {
                    int insertIndex = findInsertionPoint(newItem.getName());
                    items.add(insertIndex, newItem);
                }
                break;
            case BUBBLE:
                items.add(newItem);
                bubbleUpNewItem();
                break;
        }
        // save ts to inventory.txt
        saveItems();
    }
    
    private int findInsertionPoint(String name) {
        int low = 0;
        int high = items.size() - 1;
        
        while (low <= high) {
            int mid = (low + high) / 2;
            int comparison = name.compareToIgnoreCase(items.get(mid).getName());
            
            if (comparison < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        
        return low;
    }
    
    private void bubbleUpNewItem() {
        int n = items.size();
        for (int i = n - 1; i > 0; i--) {
            if (items.get(i).getName().compareToIgnoreCase(items.get(i - 1).getName()) < 0) {
                // Swap
                ShoppingItem temp = items.get(i);
                items.set(i, items.get(i - 1));
                items.set(i - 1, temp);
            } else {
                // Stop if item is in right place
                break;
            }
        }
    }

    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        for (ShoppingItem item : items) {
            names.add(item.getName());
        }
        return names;
    }

    // Save method
    private void saveItems() {
        // Figure out which file to use
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file = new File(ALTERNATE_PATH);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            // Write each item on its own line
            for (ShoppingItem item : items) {
                bw.write(item.getName() + " | " + item.getDescription() + " | " + item.getImagePath());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Oops! Couldn't save to file: " + e.getMessage());
        }
    }
}
