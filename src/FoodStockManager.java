import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodStockManager extends JFrame {
    private JTextField searchField;
    private JTextField quantityField;
    private JButton buyButton, restockButton;
    private JList<String> foodList;
    private DefaultListModel<String> listModel;
    private JLabel messageLabel;
    private List<FoodItem> allItems;

    static class FoodItem {
        String name;
        double price;
        int quantity;

        FoodItem(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return name + " - $" + String.format("%.2f", price) + " | Stock: " + quantity;
        }
    }

    public FoodStockManager() {
        setTitle("Food Stock & Purchase Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchField = new JTextField();
        searchField.setToolTipText("Search for a food item...");
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        add(topPanel, BorderLayout.NORTH);

        // === CENTER (LIST) ===
        listModel = new DefaultListModel<>();
        foodList = new JList<>(listModel);
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(foodList), BorderLayout.CENTER);

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Quantity + Buttons
        JPanel actionPanel = new JPanel();
        actionPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(5);
        actionPanel.add(quantityField);

        buyButton = new JButton("Buy");
        restockButton = new JButton("Restock");
        actionPanel.add(buyButton);
        actionPanel.add(restockButton);
        bottomPanel.add(actionPanel);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        bottomPanel.add(messageLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // === INITIAL ITEMS ===
        allItems = new ArrayList<>();
        allItems.add(new FoodItem("Apple", 1.20, 3));
        allItems.add(new FoodItem("Banana", 0.80, 2));
        allItems.add(new FoodItem("Bread", 2.50, 2));
        allItems.add(new FoodItem("Chicken", 5.00, 2));
        allItems.add(new FoodItem("Rice", 3.40, 2));
        allItems.add(new FoodItem("Cheese", 4.10, 2));
        allItems.add(new FoodItem("Tomato", 0.90, 2));
        allItems.add(new FoodItem("Potato", 1.10, 2));
        allItems.add(new FoodItem("Eggs", 2.00, 2));
        allItems.add(new FoodItem("Fish", 6.50, 1));

        refreshList(allItems);

        // === SEARCH LISTENER ===
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        // === BUTTON ACTIONS ===
        buyButton.addActionListener(e -> handlePurchase());
        restockButton.addActionListener(e -> handleRestock());

        setVisible(true);
    }

    // === Handle Buying ===
    private void handlePurchase() {
        int index = foodList.getSelectedIndex();
        if (index < 0) {
            messageLabel.setText("⚠️ Select an item to buy.");
            return;
        }

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) {
            messageLabel.setText("⚠️ Enter quantity to buy.");
            return;
        }

        try {
            int needed = Integer.parseInt(qtyText);
            if (needed <= 0) {
                messageLabel.setText("⚠️ Enter a number greater than 0.");
                return;
            }

            FoodItem selected = findSelectedItem();
            if (selected == null) return;

            if (needed > selected.quantity) {
                messageLabel.setText("❌ Not enough stock for " + selected.name + ".");
                return;
            }

            double totalCost = needed * selected.price;
            selected.quantity -= needed;

            messageLabel.setText("✅ Bought " + needed + " " + selected.name +
                    " for $" + String.format("%.2f", totalCost) +
                    ". Remaining: " + selected.quantity);

            refreshList(allItems);
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            messageLabel.setText("⚠️ Enter a valid number.");
        }
    }

    // === Handle Restocking ===
    private void handleRestock() {
        int index = foodList.getSelectedIndex();
        if (index < 0) {
            messageLabel.setText("⚠️ Select an item to restock.");
            return;
        }

        String qtyText = quantityField.getText().trim();
        if (qtyText.isEmpty()) {
            messageLabel.setText("⚠️ Enter quantity to restock.");
            return;
        }

        try {
            int addQty = Integer.parseInt(qtyText);
            if (addQty <= 0) {
                messageLabel.setText("⚠️ Quantity must be positive.");
                return;
            }

            FoodItem selected = findSelectedItem();
            if (selected == null) return;

            selected.quantity += addQty;
            messageLabel.setText("🔄 Restocked " + selected.name +
                    " by " + addQty + " units. New stock: " + selected.quantity);

            refreshList(allItems);
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            messageLabel.setText("⚠️ Invalid number for restock.");
        }
    }

    private FoodItem findSelectedItem() {
        String selectedText = foodList.getSelectedValue();
        if (selectedText == null) return null;
        for (FoodItem item : allItems) {
            if (selectedText.contains(item.name)) return item;
        }
        return null;
    }

    private void filterList() {
        String query = searchField.getText().toLowerCase();
        List<FoodItem> filtered = allItems.stream()
                .filter(i -> i.name.toLowerCase().contains(query))
                .collect(Collectors.toList());
        refreshList(filtered);
    }

    private void refreshList(List<FoodItem> items) {
        listModel.clear();
        for (FoodItem item : items) {
            listModel.addElement(item.toString());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FoodStockManager::new);
    }
}
