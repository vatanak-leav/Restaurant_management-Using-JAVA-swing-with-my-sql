package form;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class bill extends JFrame {
    private JPanel Main;
    private JLabel lbldate;
    private JLabel lblinvNum;
    private JTable tbllist;
    private JPanel listpanel;
    private JTextField txtsubtotal;
    private JTextField txttax7;
    private JTextField txttotal_t;
    private JButton confirmButton;
    private JLabel lblback;
    private JLabel backbtn;
    DefaultTableModel tableModel2 = new DefaultTableModel();

    private int bookingId;

    public bill(int bookingId) {
        this.bookingId = bookingId;

        setTitle("Bill");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(500, 700));
        setLocationRelativeTo(null);
        setContentPane(Main);
        txtsubtotal.setEditable(false);
        txttax7.setEditable(false);
        txttotal_t.setEditable(false);
        setUndecorated(true);
        setResizable(false);
        setVisible(true);

        initializetablelist();
        displayCurrentDateTime();
        addRecordToBillTable();

        lblback.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
            }
        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Record added to bill table successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false);
            }
        });
    }

    private void displayCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        lbldate.setText(LocalDateTime.now().format(formatter));
    }

    private void addRecordToBillTable() {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = db.dbConnection.getConnection();

            // Calculate subtotal, tax, total
            double subtotal = calculateSubtotal(this.bookingId, con);
            double tax = subtotal * 0.07; // Assuming 7%
            double total = subtotal + tax;

            // Update total_amount in order
            String updateOrderQuery = "UPDATE `order` SET total_amount = ? WHERE order_id = ?";
            ps = con.prepareStatement(updateOrderQuery);
            ps.setDouble(1, total);
            ps.setInt(2, this.bookingId);
            ps.executeUpdate();

            // Now, insert a record into the `bill` table
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = now.format(formatter);

            String insertBillQuery = "INSERT INTO `bill` (invoice, order_id, payment_date) VALUES (?, ?, ?)";
            ps = con.prepareStatement(insertBillQuery);
            String invoiceNumber = generateInvoiceNumber();

            ps.setString(1, invoiceNumber);
            ps.setInt(2, this.bookingId);
            ps.setString(3, formattedDate);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                //JOptionPane.showMessageDialog(this, "Record added to bill table successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Display the values in the fields
                txtsubtotal.setText(String.format("%.2f", subtotal));
                txttax7.setText(String.format("%.2f", tax));
                txttotal_t.setText(String.format("%.2f", total));

                lblinvNum.setText(invoiceNumber);
                lbldate.setText(formattedDate);

                displayOrderDetails(this.bookingId, con);

            } else {
                JOptionPane.showMessageDialog(this, "Failed to add record to bill table.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating new order or bill: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private double calculateSubtotal(int orderId, Connection con) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        double subtotal = 0;
        try {
            String query = "SELECT SUM(oi.quantity * fm.price) AS subtotal " +
                    "FROM order_item oi " +
                    "JOIN food_menu fm ON oi.food_id = fm.food_id " +
                    "WHERE oi.order_id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                subtotal = rs.getDouble("subtotal");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
        return subtotal;
    }

    private void initializetablelist() {
        tableModel2.addColumn("Code");
        tableModel2.addColumn("Name");
        tableModel2.addColumn("Quantity");
        tableModel2.addColumn("Price");
        tableModel2.addColumn("Total");

        tbllist.setModel(tableModel2);
        tbllist.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tbllist);
        scrollPane.setPreferredSize(new Dimension(tbllist.getPreferredScrollableViewportSize().width, 500));
        JPanel panelfood = new JPanel(new BorderLayout());
        panelfood.add(scrollPane, BorderLayout.CENTER);
        listpanel.setLayout(new BorderLayout());
        listpanel.add(panelfood, BorderLayout.CENTER);
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    private void displayOrderDetails(int orderId, Connection con) throws SQLException {
        tableModel2.setRowCount(0); // Clear existing rows
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query = "SELECT fm.food_code, fm.food_name, oi.quantity, fm.price, (oi.quantity * fm.price) AS total " +
                    "FROM order_item oi " +
                    "JOIN food_menu fm ON oi.food_id = fm.food_id " +
                    "WHERE oi.order_id = ?";
            ps = con.prepareStatement(query);
            ps.setInt(1, orderId);
            rs = ps.executeQuery();

            while (rs.next()) {
                tableModel2.addRow(new Object[]{
                        rs.getString("food_code"),
                        rs.getString("food_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                });
            }

            // Check if rows have been added to the table model
            System.out.println("DEBUG: Number of rows in table model: " + tableModel2.getRowCount());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}