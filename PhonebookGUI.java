import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}

public class PhonebookGUI extends JFrame {
    private ArrayList<Contact> contacts;
    private DefaultTableModel tableModel;
    private JTable contactTable;
    private JTextField nameField, phoneField, searchField;
    private JButton addButton, updateButton, deleteButton, searchButton;

    // Color scheme
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // Alice Blue
    private final Color BUTTON_COLOR = new Color(70, 130, 180);      // Steel Blue
    private final Color TEXT_COLOR = new Color(25, 25, 112);         // Midnight Blue

    public PhonebookGUI() {
        contacts = new ArrayList<>();
        setTitle("Phonebook Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create components
        createTable();
        createInputPanel();
        createButtonPanel();

        // Add components to frame
        add(new JScrollPane(contactTable), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private void createTable() {
        String[] columns = {"Name", "Phone Number"};
        tableModel = new DefaultTableModel(columns, 0);
        contactTable = new JTable(tableModel);
        contactTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contactTable.setBackground(BACKGROUND_COLOR);
        contactTable.setForeground(TEXT_COLOR);
        contactTable.setGridColor(BUTTON_COLOR);
        contactTable.setSelectionBackground(BUTTON_COLOR);
        contactTable.setSelectionForeground(Color.WHITE);
        contactTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JTableHeader header = contactTable.getTableHeader();
        header.setBackground(BUTTON_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = createStyledTextField();
        phoneField = createStyledTextField();
        searchField = createStyledTextField();

        inputPanel.add(createStyledLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(createStyledLabel("Phone:"));
        inputPanel.add(phoneField);
        inputPanel.add(createStyledLabel("Search:"));
        inputPanel.add(searchField);

        add(inputPanel, BorderLayout.NORTH);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT_COLOR);
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        addButton = createStyledButton("Add", new ImageIcon("path/to/add_icon.png"));
        updateButton = createStyledButton("Update", new ImageIcon("path/to/update_icon.png"));
        deleteButton = createStyledButton("Delete", new ImageIcon("path/to/delete_icon.png"));
        searchButton = createStyledButton("Search", new ImageIcon("path/to/search_icon.png"));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> addContact());
        updateButton.addActionListener(e -> updateContact());
        deleteButton.addActionListener(e -> deleteContact());
        searchButton.addActionListener(e -> searchContacts());
    }

    private JButton createStyledButton(String text, ImageIcon icon) {
        JButton button = new JButton(text, icon);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        if (!name.isEmpty() && !phone.isEmpty()) {
            Contact contact = new Contact(name, phone);
            contacts.add(contact);
            tableModel.addRow(new Object[]{name, phone});
            clearInputFields();
            sortContacts();
        } else {
            showErrorMessage("Please enter both name and phone number.");
        }
    }

    private void updateContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (!name.isEmpty() && !phone.isEmpty()) {
                contacts.get(selectedRow).setName(name);
                contacts.get(selectedRow).setPhoneNumber(phone);
                tableModel.setValueAt(name, selectedRow, 0);
                tableModel.setValueAt(phone, selectedRow, 1);
                clearInputFields();
                sortContacts();
            } else {
                showErrorMessage("Please enter both name and phone number.");
            }
        } else {
            showErrorMessage("Please select a contact to update.");
        }
    }

    private void deleteContact() {
        int selectedRow = contactTable.getSelectedRow();
        if (selectedRow != -1) {
            contacts.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            showErrorMessage("Please select a contact to delete.");
        }
    }

    private void searchContacts() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        if (!searchTerm.isEmpty()) {
            tableModel.setRowCount(0);
            for (Contact contact : contacts) {
                if (contact.getName().toLowerCase().contains(searchTerm) ||
                        contact.getPhoneNumber().contains(searchTerm)) {
                    tableModel.addRow(new Object[]{contact.getName(), contact.getPhoneNumber()});
                }
            }
        } else {
            refreshTable();
        }
    }

    private void sortContacts() {
        Collections.sort(contacts, Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER));
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Contact contact : contacts) {
            tableModel.addRow(new Object[]{contact.getName(), contact.getPhoneNumber()});
        }
    }

    private void clearInputFields() {
        nameField.setText("");
        phoneField.setText("");
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new PhonebookGUI().setVisible(true));
    }
}