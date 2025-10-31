import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodPurchaseApp extends JFrame {
    private JTextField searchField;
    private JTextField quantityField;
    private JButton buyButton;
    private JList<String> foodList;
    private DefaultListModel<String> listModel;
    private JLabel messageLabel;
    private List<FoodItem> allItems;

    // Food item model
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

    public FoodPurchaseApp() {
        setTitle("Food Stock & Purchase System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500);
        setLayout(new BorderLayout(10, 10));

        // ==== TOP PANEL ====
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchField = new JTextField();
        searchField.setToolTipText("Search for a food item...");
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        add(topPanel, BorderLayout.NORTH);

        // ==== CENTER PANEL ====
        listModel = new DefaultListModel<>();
        foodList = new JList<>(listModel);
        foodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(foodList), BorderLayout.CENTER);

        // ==== BOTTOM PANEL ====
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Quantity:"));
        quantityField = new JTextField(5);
        inputPanel.add(quantityField);
        buyButton = new JButton("Buy Selected Item");
        inputPanel.add(buyButton);
        bottomPanel.add(inputPanel);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        bottomPanel.add(messageLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // ==== FOOD ITEMS ====
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

        // ==== SEARCH LISTENER ====
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        // ==== BUY BUTTON LOGIC ====
        buyButton.addActionListener(e -> processPurchase());

        setVisible(true);
    }

    private void processPurchase() {
        int index = foodList.getSelectedIndex();
        if (index < 0) {
            messageLabel.setText("⚠️ Please select an item first.");
            return;
        }

        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            messageLabel.setText("⚠️ Enter how many items you want to buy.");
            return;
        }

        try {
            int needed = Integer.parseInt(quantityText);
            if (needed <= 0) {
                messageLabel.setText("⚠️ Quantity must be greater than zero.");
                return;
            }

            String selectedItemText = foodList.getSelectedValue();
            FoodItem selectedItem = findItemByDisplay(selectedItemText);

            if (selectedItem == null) return;

            if (needed > selectedItem.quantity) {
                messageLabel.setText("❌ Not enough stock for " + selectedItem.name + ".");
                return;
            }

            double totalCost = needed * selectedItem.price;
            selectedItem.quantity -= needed;

            messageLabel.setText("✅ You bought " + needed + " " + selectedItem.name +
                    " for $" + String.format("%.2f", totalCost) +
                    ". Remaining stock: " + selectedItem.quantity);

            refreshList(allItems);
            quantityField.setText("");

        } catch (NumberFormatException ex) {
            messageLabel.setText("⚠️ Invalid quantity. Enter a number.");
        }
    }

    private FoodItem findItemByDisplay(String text) {
        for (FoodItem item : allItems) {
            if (text.contains(item.name)) return item;
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
        SwingUtilities.invokeLater(FoodPurchaseApp::new);
    }
}
