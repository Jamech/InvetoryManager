import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoodSearchApp extends JFrame {
    private JTextField searchField;
    private JList<String> foodList;
    private DefaultListModel<String> listModel;
    private List<FoodItem> allItems;

    // Simple inner class for food item
    static class FoodItem {
        String name;
        double price;

        FoodItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        @Override
        public String toString() {
            return name + " - $" + String.format("%.2f", price);
        }
    }

    public FoodSearchApp() {
        setTitle("Food Search App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout(10, 10));

        // Top search bar
        searchField = new JTextField();
        searchField.setToolTipText("Search food item...");
        add(searchField, BorderLayout.NORTH);

        // Food list
        listModel = new DefaultListModel<>();
        foodList = new JList<>(listModel);
        add(new JScrollPane(foodList), BorderLayout.CENTER);

        // Initialize food data
        allItems = new ArrayList<>();
        allItems.add(new FoodItem("Apple", 1.20));
        allItems.add(new FoodItem("Banana", 0.80));
        allItems.add(new FoodItem("Bread", 2.50));
        allItems.add(new FoodItem("Chicken", 5.00));
        allItems.add(new FoodItem("Rice", 3.40));
        allItems.add(new FoodItem("Cheese", 4.10));
        allItems.add(new FoodItem("Tomato", 0.90));
        allItems.add(new FoodItem("Potato", 1.10));
        allItems.add(new FoodItem("Eggs", 2.00));
        allItems.add(new FoodItem("Fish", 6.50));

        // Load all items initially
        refreshList(allItems);

        // Add listener for search bar
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterList(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterList(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterList(); }
        });

        setVisible(true);
    }

    private void filterList() {
        String query = searchField.getText().toLowerCase();
        List<FoodItem> filtered = allItems.stream()
                .filter(item -> item.name.toLowerCase().contains(query))
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
        SwingUtilities.invokeLater(FoodSearchApp::new);
    }
}
