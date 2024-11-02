package form;
import db.dbConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Booking extends JFrame {
    private JPanel panel1;
    private JPanel Main;
    private JPanel leftpanel;
    private JButton btnmenu;
    private JButton btnorder;
    private JButton btnbooking;
    private JButton btnHis;
    private JButton logoutButton;
    private JPanel foodpanel;
    private JTable tblbooked;
    private JTextField txtname;
    private JTextField txtprice;
    private JTextField txttype;
    private JSpinner spinqty;
    private JTextField txtcode;
    private JButton btnreset;
    private JButton btnadd;
    private JButton btnconfirm;
    private JButton btndelete;
    private JPanel listpanel;
    private JTable tbllist;
    private JTextField txtsub;
    private JTextField txttax;
    private JTextField txttotal;
    private int food_id;
    private String food_code;
    private String food_name;
    private Double food_price;
    private int quantity;

    DefaultTableModel tableModel = new DefaultTableModel();
    DefaultTableModel tableModel2 = new DefaultTableModel();
    private int currentOrderId;  // Variable to keep track of the current order ID

    public Booking() {
        setTitle("Booking");
        initializeTablemenu();
        initializetablelist();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1440, 768));
        setLocationRelativeTo(null);
        setContentPane(Main);
        txtcode.setEditable(false);
        txtname.setEditable(false);
        txtprice.setEditable(false);
        txttype.setEditable(false);
        txtsub.setEditable(false);
        txttax.setEditable(false);
        txttotal.setEditable(false);
        setResizable(false);
        setVisible(true);

        createNewOrder();

        tblbooked.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblbooked.getSelectedRow();
                    if (selectedRow != -1) {
                        Object selectedCode = tblbooked.getValueAt(selectedRow, 0);
                        loadMenuByCode(selectedCode.toString());
                    }
                }
            }
        });

        tbllist.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tbllist.getSelectedRow();
                    if (selectedRow != -1) {
                        Object selectedCode = tbllist.getValueAt(selectedRow, 0);
                        loadlistByCode(selectedCode.toString());
                    }
                }
            }
        });

        btnreset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1000);
            }
        });

        btnadd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtcode.getText().isEmpty() && (int) spinqty.getValue() > 0) {
                    int food_id = getfood_id(txtcode.getText());
                    String code = txtcode.getText();
                    String name = txtname.getText();
                    Double price = Double.valueOf(txtprice.getText());
                    String type = txttype.getText();
                    int quantity = (int) spinqty.getValue();
                    inserttolist(currentOrderId, food_id, quantity);
                }
            }
        });

        btndelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!txtcode.getText().isEmpty()) {
                    String code = txtcode.getText();
                    int item_id = get_order_item_id(code);
                    deleteSelectedRow(item_id);
                }
            }
        });

        btnconfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double totalAmount = Double.parseDouble(txttotal.getText());

                    boolean isSuccess = updateTotalAmountInOrder(totalAmount);

                    if (isSuccess) {
                        new bill(currentOrderId).setVisible(true);

                        // Clear tbllist table
                        tableModel2.setRowCount(0);

                        // Clear text fields
                        resetFields();

                        // Create a new order for the next session
                        createNewOrder();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to confirm order.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid total amount.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnmenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Menu().setVisible(true);
            }
        });

        btnbooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Book().setVisible(true);
            }
        });

        btnHis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new History().setVisible(true);
            }
        });
    }

    private void initializeTablemenu() {
        tableModel.addColumn("Code");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Category");
        loadMenu(tableModel);
        tblbooked.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblbooked.setModel(tableModel);
        tblbooked.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblbooked);
        scrollPane.setPreferredSize(new Dimension(tblbooked.getPreferredScrollableViewportSize().width, 400));
        JPanel panelfood = new JPanel(new BorderLayout());
        panelfood.add(scrollPane, BorderLayout.CENTER);
        foodpanel.add(panelfood);
    }

    void loadMenuByCode(String code) {
        try (Connection con = dbConnection.getConnection()) {
            String query = "SELECT food_id, food_code, food_name, price, category_name " +
                    "FROM food_menu " +
                    "INNER JOIN category ON food_menu.category_id = category.category_id " +
                    "WHERE food_code = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, code);
                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        txtcode.setText(resultSet.getString("food_code"));
                        txtname.setText(resultSet.getString("food_name"));
                        txtprice.setText(String.valueOf(resultSet.getDouble("price")));
                        txttype.setText(resultSet.getString("category_name"));
                        food_id = resultSet.getInt("food_id");
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading data from database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void loadMenu(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try (Connection con = dbConnection.getConnection();
             Statement statement = con.createStatement()) {
            String query = "SELECT food_id, food_code, food_name, price, category_name " +
                    "FROM food_menu " +
                    "JOIN category ON food_menu.category_id = category.category_id";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    String code = resultSet.getString("food_code");
                    String name = resultSet.getString("food_name");
                    double price = resultSet.getDouble("price");
                    String type = resultSet.getString("category_name");
                    tableModel.addRow(new Object[] {code, name, price, type});
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error loading data from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewOrder() {
        String createOrderQuery = "INSERT INTO `order` (total_amount) VALUES (0)";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(createOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        currentOrderId = keys.getInt(1);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create a new order.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating new order: " + e.getMessage());
        }
    }

    private void inserttolist(int orderId, int foodId, int quantity) {
        String insertQuery = "INSERT INTO order_item(order_id, food_id, quantity) VALUES (?, ?, ?)";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {
            ps.setInt(1, orderId);
            ps.setInt(2, foodId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            loadlist();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding item: " + e.getMessage());
        }
    }

    private int getfood_id(String code) {
        int foodId = -1;
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT food_id FROM food_menu WHERE food_code = ?")) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    foodId = rs.getInt("food_id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving food ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return foodId;
    }

    private int get_order_item_id(String code) {
        int orderItemId = -1;
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT order_item_id " +
                     "FROM order_item " +
                     "JOIN food_menu ON food_menu.food_id = order_item.food_id " +
                     "WHERE food_code = ? AND order_id = ?")) {
            ps.setString(1, code);
            ps.setInt(2, currentOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    orderItemId = rs.getInt("order_item_id");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving order item ID: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return orderItemId;
    }

    private void initializetablelist() {
        tableModel2.addColumn("Code");
        tableModel2.addColumn("Name");
        tableModel2.addColumn("Price");
        tableModel2.addColumn("Quantity");
        tableModel2.addColumn("Total");
        tbllist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbllist.setModel(tableModel2);
        tbllist.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tbllist);
        scrollPane.setPreferredSize(new Dimension(tbllist.getPreferredScrollableViewportSize().width, 500));
        JPanel panelfood = new JPanel(new BorderLayout());
        panelfood.add(scrollPane, BorderLayout.CENTER);
        listpanel.add(panelfood);
    }

    private void loadlist() {
        tableModel2.setRowCount(0);
        double subtotal = 0;
        String query = "SELECT food_code, food_name, price, quantity, (quantity * price) AS total " +
                "FROM food_menu " +
                "JOIN order_item ON food_menu.food_id = order_item.food_id " +
                "WHERE order_id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, currentOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("food_code");
                    String name = rs.getString("food_name");
                    double price = rs.getDouble("price");
                    int quantity = rs.getInt("quantity");
                    double total = rs.getDouble("total");
                    subtotal += total;
                    tableModel2.addRow(new Object[]{code, name, price, quantity, total});
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        double tax = subtotal * 0.07; // 7% tax
        double total = subtotal + tax;

        txtsub.setText(String.format("%.2f", subtotal));
        txttax.setText(String.format("%.2f", tax));
        txttotal.setText(String.format("%.2f", total));
    }

    private void loadlistByCode(String code) {
        String query = "SELECT food_code, food_name, price, category_name, quantity " +
                "FROM food_menu " +
                "JOIN category ON category.category_id = food_menu.category_id " +
                "JOIN order_item ON order_item.food_id = food_menu.food_id " +
                "WHERE food_code = ? AND order_id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, code);
            ps.setInt(2, currentOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtcode.setText(rs.getString("food_code"));
                    txtname.setText(rs.getString("food_name"));
                    txtprice.setText(String.valueOf(rs.getDouble("price")));
                    txttype.setText(rs.getString("category_name"));
                    spinqty.setValue(rs.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading item details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedRow(int orderItemId) {
        String deleteSQL = "DELETE FROM order_item WHERE order_item_id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(deleteSQL)) {
            ps.setInt(1, orderItemId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                loadlist();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the record.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting item: " + e.getMessage());
        }
    }

    private boolean updateTotalAmountInOrder(double totalAmount) {
        String updateQuery = "UPDATE `order` SET total_amount = ? WHERE order_id = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setDouble(1, totalAmount);
            ps.setInt(2, currentOrderId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating total amount: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void resetFields() {
        txtcode.setText("");
        txtname.setText("");
        txtprice.setText("");
        txttype.setText("");
        spinqty.setValue(0);
        txtsub.setText("");
        txttax.setText("");
        txttotal.setText("");
    }
}