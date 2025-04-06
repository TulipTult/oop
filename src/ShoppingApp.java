import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ShoppingApp {
    // GUI components
    private JFrame frame;
    private JTextField searchField;
    private JComboBox<String> productsDropdown;
    private JButton searchButton;
    private JButton addButton;
    private JLabel nameLabel;
    private JTextArea descriptionArea;
    private ImagePanel imagePanel;
    private Inventory inventory;
    private String selectedImagePath = "";

    public ShoppingApp() {
        inventory = new Inventory();

        // main widow
        frame = new JFrame("Shopping App");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // padding
        ((JPanel)frame.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // algos menu bar
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        // srt menu bar
        JMenu sortMenu = new JMenu("Sort Algorithm");
        ButtonGroup sortGroup = new ButtonGroup();
        
       
        JRadioButtonMenuItem defaultSortItem = new JRadioButtonMenuItem("Default Java Sort", true);
        JRadioButtonMenuItem insertionSortItem = new JRadioButtonMenuItem("Insertion Sort");
        JRadioButtonMenuItem bubbleSortItem = new JRadioButtonMenuItem("Bubble Sort");
        

        sortGroup.add(defaultSortItem);
        sortGroup.add(insertionSortItem);
        sortGroup.add(bubbleSortItem);
        

        sortMenu.add(defaultSortItem);
        sortMenu.add(insertionSortItem);
        sortMenu.add(bubbleSortItem);
        

        JMenu searchMenu = new JMenu("Search Algorithm");
        ButtonGroup searchGroup = new ButtonGroup();

        JRadioButtonMenuItem binarySearchItem = new JRadioButtonMenuItem("Binary Search", true);
        JRadioButtonMenuItem linearSearchItem = new JRadioButtonMenuItem("Linear Search");

        searchGroup.add(binarySearchItem);
        searchGroup.add(linearSearchItem);

        searchMenu.add(binarySearchItem);
        searchMenu.add(linearSearchItem);

        menuBar.add(sortMenu);
        menuBar.add(searchMenu);
        

        defaultSortItem.addActionListener(e -> inventory.setSortAlgorithm(Inventory.SortAlgorithm.DEFAULT));
        insertionSortItem.addActionListener(e -> inventory.setSortAlgorithm(Inventory.SortAlgorithm.INSERTION));
        bubbleSortItem.addActionListener(e -> inventory.setSortAlgorithm(Inventory.SortAlgorithm.BUBBLE));

        binarySearchItem.addActionListener(e -> inventory.setSearchAlgorithm(Inventory.SearchAlgorithm.BINARY));
        linearSearchItem.addActionListener(e -> inventory.setSearchAlgorithm(Inventory.SearchAlgorithm.LINEAR));


        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        
        String[] productNames = inventory.getAllProductNames().toArray(new String[0]);
        productsDropdown = new JComboBox<>(productNames);
        productsDropdown.setPreferredSize(new Dimension(150, 30));
        
  
        searchButton = new JButton("Search");
        
    
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Products: "));
        searchPanel.add(productsDropdown);
        searchPanel.add(searchButton);
        
        frame.add(searchPanel, BorderLayout.NORTH);

        JPanel addPanel = new JPanel();
        addButton = new JButton("Add Item");
        addPanel.add(addButton);
        frame.add(addPanel, BorderLayout.SOUTH);

        JPanel itemPanel = new JPanel(new BorderLayout());
        nameLabel = new JLabel("", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        descriptionArea = new JTextArea(6, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        descPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        
        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(descPanel, BorderLayout.CENTER);

        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imagePanel = new ImagePanel("");
        westPanel.add(imagePanel, BorderLayout.CENTER);
        westPanel.setPreferredSize(new Dimension(400, 500));
        
        frame.add(westPanel, BorderLayout.WEST);
        frame.add(itemPanel, BorderLayout.CENTER);


        productsDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productsDropdown.getSelectedItem() != null) {
                    searchField.setText(productsDropdown.getSelectedItem().toString());
                }
            }
        });


        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchItem();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        frame.setVisible(true);
    }

    private void updateProductsDropdown() {
        productsDropdown.removeAllItems();
        List<String> productNames = inventory.getAllProductNames();
        for (String name : productNames) {
            productsDropdown.addItem(name);
        }
    }

    private void searchItem() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter an item name.");
            return;
        }

        ShoppingItem item = inventory.searchItem(query);
        if (item != null) {
            nameLabel.setText(item.getName());
            descriptionArea.setText(item.getDescription());
            
            Container parent = imagePanel.getParent();
            parent.remove(imagePanel);
            imagePanel = new ImagePanel(item.getImagePath());
            parent.add(imagePanel, BorderLayout.CENTER);
            
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(frame, "Item not found.");
        }
    }
    
    private void showAddItemDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        
        JTextField nameField = new JTextField();
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        
        JButton imageButton = new JButton("Select Image");
        JLabel imagePathLabel = new JLabel("No image selected");
        panel.add(imageButton);
        panel.add(imagePathLabel);
        
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif"));
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    selectedImagePath = "./images/" + selectedFile.getName();
                    
                    File imagesDir = new File("src/images");
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs();
                    }
                    
                    try {
                        File destFile = new File("src/images/" + selectedFile.getName());
                        java.nio.file.Files.copy(
                            selectedFile.toPath(), 
                            destFile.toPath(), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );
                        imagePathLabel.setText(selectedFile.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                            frame, 
                            "Error copying image file: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        
        int result = JOptionPane.showConfirmDialog(
            frame, panel, "Add New Product", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Product name cannot be empty!");
                return;
            }
            
            if (selectedImagePath.isEmpty()) {
                selectedImagePath = "./images/default.png";
            }
            
            inventory.addItem(name, description, selectedImagePath);
            updateProductsDropdown(); 
            JOptionPane.showMessageDialog(frame, "Product added successfully!");
            
            selectedImagePath = "";
        }
    }

    public static void main(String[] args) {
        new ShoppingApp();
    }
}
