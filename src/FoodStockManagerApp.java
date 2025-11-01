import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class FoodItem {
    private String name;
    private double price;
    private int quantity;

    public FoodItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return name + " - K" + price + " (" + quantity + " in stock)";
    }
}

class FoodStockManager {
    private List<FoodItem> items = new ArrayList<>();

    public FoodStockManager() {
        items.add(new FoodItem("Apples", 12.00, 20));
        items.add(new FoodItem("Bananas", 10.00, 20));
        items.add(new FoodItem("Bread", 26.0, 20));
        items.add(new FoodItem("Milk", 17.0, 20));
        items.add(new FoodItem("Eggs", 5.0, 20));
        items.add(new FoodItem("Rice", 27.8, 20));
        items.add(new FoodItem("Chicken", 160.00, 20));
        items.add(new FoodItem("Beef", 218.0, 20));
        items.add(new FoodItem("Tomatoes", 6.00, 20));
        items.add(new FoodItem("Potatoes", 140.5, 20));
    }

    public List<FoodItem> getItems() { return items; }

    public void addItem(FoodItem item) {
        items.add(item);
    }

    public List<FoodItem> search(String keyword) {
        return items.stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public double buyItem(String itemName, int quantity) {
        for (FoodItem item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                if (item.getQuantity() >= quantity) {
                    item.setQuantity(item.getQuantity() - quantity);
                    return item.getPrice() * quantity;
                } else {
                    throw new IllegalArgumentException("Not enough stock for " + itemName);
                }
            }
        }
        throw new IllegalArgumentException("Item not found: " + itemName);
    }
}

public class FoodStockManagerApp extends JFrame {
    private JTextField searchField, quantityField, nameField, priceField, stockField;
    private JButton buyButton, addButton;
    private JList<FoodItem> itemList;
    private DefaultListModel<FoodItem> listModel;
    private FoodStockManager manager;

    public FoodStockManagerApp() {
        manager = new FoodStockManager();
        setTitle("Food Stock Manager");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel: Search + Buy Section
        JPanel topPanel = new JPanel(new GridLayout(2, 1));

        // Search
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        topPanel.add(searchPanel);

        // Buy Section
        JPanel buyPanel = new JPanel();
        buyPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        buyPanel.add(quantityField);
        buyButton = new JButton("Buy");
        buyPanel.add(buyButton);
        topPanel.add(buyPanel);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: List
        listModel = new DefaultListModel<>();
        updateList(manager.getItems());
        itemList = new JList<>(listModel);
        add(new JScrollPane(itemList), BorderLayout.CENTER);

        // Bottom Panel: Add New Item Section
        JPanel addPanel = new JPanel();
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Item"));
        addPanel.add(new JLabel("Name:"));
        nameField = new JTextField(8);
        addPanel.add(nameField);

        addPanel.add(new JLabel("Price:"));
        priceField = new JTextField(5);
        addPanel.add(priceField);

        addPanel.add(new JLabel("Stock:"));
        stockField = new JTextField(5);
        addPanel.add(stockField);

        addButton = new JButton("Add Item");
        addPanel.add(addButton);
        add(addPanel, BorderLayout.SOUTH);

        // Listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchItems(); }
        });

        buyButton.addActionListener(e -> buyItem());
        addButton.addActionListener(e -> addItem());
    }

    private void searchItems() {
        String keyword = searchField.getText();
        updateList(manager.search(keyword));
    }

    private void buyItem() {
        FoodItem selected = itemList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select an item to buy.");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText());
            double total = manager.buyItem(selected.getName(), qty);
            JOptionPane.showMessageDialog(this, "Total cost: K" + total);
            updateList(manager.getItems());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addItem() {
        try {
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            if (name.isEmpty() || price <= 0 || stock <= 0) {
                JOptionPane.showMessageDialog(this, "Please fill all fields correctly.");
                return;
            }

            manager.addItem(new FoodItem(name, price, stock));
            updateList(manager.getItems());
            JOptionPane.showMessageDialog(this, name + " added successfully!");

            nameField.setText("");
            priceField.setText("");
            stockField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your values.");
        }
    }

    private void updateList(List<FoodItem> items) {
        listModel.clear();
        for (FoodItem item : items) {
            listModel.addElement(item);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FoodStockManagerApp().setVisible(true));
    }
}
