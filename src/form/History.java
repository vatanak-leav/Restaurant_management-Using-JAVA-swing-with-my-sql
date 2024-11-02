package form;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class History extends JFrame {
    private JPanel Main;
    private JPanel leftpanel;
    private JButton btnmenu;
    private JButton btnorder;
    private JButton btnbooking;
    private JButton btnHis;
    private JButton logoutButton;
    private JPanel Rightpanel;
    private JPanel billpanel;
    private JTable tblbill;
    private JPanel paneldetail;
    private JTable tblallbill;
    private JTextField txtsub;
    private JTextField txttax;
    private JTextField txttotal;
    DefaultTableModel billTableModel;
    DefaultTableModel tableModel2 = new DefaultTableModel();

    public History() {
        setTitle("History");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1440, 768));
        setLocationRelativeTo(null);
        setContentPane(Main);
        setResizable(false);
        setVisible(true);
        txtsub.setEditable(false);
        txttax.setEditable(false);
        txttotal.setEditable(false);
        initializetablelist();
        initializeTableBill();
        loadBillHistory();

        btnorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Booking().setVisible(true);
            }
        });
        btnmenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Menu().setVisible(true);
            }
        });
        btnHis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new History().setVisible(true);
            }
        });
        tblbill.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = tblbill.getSelectedRow();
                if (selectedRow != -1) {
                    String invoice = (String) billTableModel.getValueAt(selectedRow, 0);
                    loadBillDetails(invoice);
                }
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnbooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Book().setVisible(true);
            }
        });
    }

    private void initializeTableBill() {
        billTableModel = new DefaultTableModel();
        billTableModel.addColumn("Invoice No");
        billTableModel.addColumn("Order ID");
        billTableModel.addColumn("Payment Date");
        billTableModel.addColumn("Total Amount");

        tblbill.setModel(billTableModel);
        tblbill.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblbill);
        scrollPane.setPreferredSize(new Dimension(tblbill.getPreferredScrollableViewportSize().width, 500));
        JPanel panelBill = new JPanel(new BorderLayout());
        panelBill.add(scrollPane, BorderLayout.CENTER);
        billpanel.setLayout(new BorderLayout());
        billpanel.add(panelBill, BorderLayout.CENTER);
    }

    private void initializetablelist() {
        tableModel2.addColumn("Code");
        tableModel2.addColumn("Name");
        tableModel2.addColumn("Quantity");
        tableModel2.addColumn("Price");
        tableModel2.addColumn("Total");

        tblallbill.setModel(tableModel2);
        tblallbill.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblallbill);
        scrollPane.setPreferredSize(new Dimension(tblallbill.getPreferredScrollableViewportSize().width, 400));
        JPanel panelfood = new JPanel(new BorderLayout());
        panelfood.add(scrollPane, BorderLayout.CENTER);
        paneldetail.setLayout(new BorderLayout());
        paneldetail.add(panelfood, BorderLayout.CENTER);
    }

    private void loadBillHistory() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();

            String query = "SELECT b.invoice, b.order_id, b.payment_date, o.total_amount " +
                    "FROM bill b " +
                    "JOIN `order` o ON b.order_id = o.order_id";
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                String invoice = rs.getString("invoice");
                int orderId = rs.getInt("order_id");
                String paymentDate = rs.getString("payment_date");
                double totalAmount = rs.getDouble("total_amount");

                billTableModel.addRow(new Object[]{invoice, orderId, paymentDate, totalAmount});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bill history: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loadBillDetails(String invoice) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double subTotal = 0.0;
        double tax = 0.0;
        double total = 0.0;

        try {
            con = db.dbConnection.getConnection();

            String query = "SELECT fm.food_code, fm.food_name, oi.quantity, fm.price, (oi.quantity * fm.price) AS itemTotal " +
                    "FROM bill b " +
                    "JOIN `order` o ON b.order_id = o.order_id " +
                    "JOIN order_item oi ON o.order_id = oi.order_id " +
                    "JOIN food_menu fm ON oi.food_id = fm.food_id " +
                    "WHERE b.invoice = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, invoice);
            rs = ps.executeQuery();

            tableModel2.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String foodCode = rs.getString("food_code");
                String foodName = rs.getString("food_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double itemTotal = rs.getDouble("itemTotal");
                subTotal += itemTotal;

                tableModel2.addRow(new Object[]{foodCode, foodName, quantity, price, itemTotal});
            }

            tax = subTotal * 0.10; // Assuming tax is 10% of subTotal
            total = subTotal + tax;

            txtsub.setText(String.format("%.2f", subTotal));
            txttax.setText(String.format("%.2f", tax));
            txttotal.setText(String.format("%.2f", total));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bill details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}