package form;

import cls.KeyValue;
import cls.MenuCode;
import db.dbConnection;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import cls.MenuCode;

public class Menu extends JFrame {
    public Object initailizeTable;
    private JPanel Main;
    private JButton btnmenu;
    private JPanel Rightpanel;
    private JTable tblfood;
    private JPanel foodpanel;
    private JPanel leftpanel;
    private JButton btnorder;
    private JButton btnbooking;
    private JButton btnHis;
    private JButton logoutButton;
    private JTextField txtcode;
    private JTextField txtname;
    private JComboBox cbotype;
    private JTextField txtprice;
    private JButton btndelete;
    private JButton btnupdate;
    private JButton btnreset;
    private JButton btnadd;
    private JPanel Menupanel;
    DefaultTableModel tableModel = new DefaultTableModel();
    public final KeyValue[] items = {
            new KeyValue(0, ""),
            new KeyValue(1, "Drink"),
            new KeyValue(2, "Khmer"),
            new KeyValue(3, "Chinese"),
            new KeyValue(4, "Fastfood"),
            new KeyValue(5, "Seafood"),
    };
    private void initializedComboBox() {
        for (KeyValue kw : items)
            cbotype.addItem(kw);
    }
    private void initializedTable() {
        // Add columns to the table model

        tableModel.addColumn("Code");
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Category");
        // Load data from the database
        loadMenu(tableModel);
        tblfood.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // set data to JTable
        tblfood.setModel(tableModel);
        tblfood.setRowHeight(30);
        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(tblfood);
        scrollPane.setPreferredSize(
                new Dimension(tblfood.getPreferredScrollableViewportSize().width, 700)
        );
        JPanel panelfood = new JPanel(new BorderLayout());
        panelfood.add(scrollPane, BorderLayout.CENTER);
        foodpanel.add(panelfood);
    }

    static void setSelectedItemByKey(JComboBox<KeyValue> comboBox, int key) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            KeyValue item = comboBox.getItemAt(i);
            if (item.getKey() == key) {
                comboBox.setSelectedItem(item);
                break;
            }
        }
    }

    void clearControls(Container container) {
        txtcode.setText("");
        txtname.setText("");
        txtprice.setText("");
        for (Component component : container.getComponents()) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JComboBox) {
                setSelectedItemByKey((JComboBox) component, 0);
            }
        }
    }

    private int getKeyFromSelectedItem(JComboBox<KeyValue> comboBox) {
        KeyValue selectedItem = (KeyValue) comboBox.getSelectedItem();
        int selectedKey = 0;
        if (selectedItem != null) {
            selectedKey = selectedItem.getKey();
        }
        return selectedKey;
    }


    void loadMenuByCode(String Code) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            con = dbConnection.getConnection();
            String query = "SELECT food_code, food_name, price, category_id FROM food_menu WHERE food_code = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, Code);

            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                txtcode.setText(resultSet.getString("food_code"));
                txtname.setText(resultSet.getString("food_name"));
                txtprice.setText(String.valueOf(resultSet.getDouble("price")));
                setSelectedItemByKey(cbotype, resultSet.getInt("category_id"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error loading data from database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                // Log this exception (e.g., using a logger)
            }
        }
    }

    void loadMenu(DefaultTableModel tableModel) {
        // Clear the existing data
        tableModel.setRowCount(0);
        try {
            Connection con = dbConnection.getConnection();
            StringBuilder query = new StringBuilder();
            query.append("SELECT food_code, food_name, price,category_name");
            query.append(" FROM food_menu JOIN category");
            query.append(" ON food_menu.category_id=category.category_id");
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query.toString());
            while (resultSet.next()) {
                String code = resultSet.getString("food_code");
                String name = resultSet.getString("food_name");
                double Price = resultSet.getBigDecimal("price").doubleValue();
                String Type = resultSet.getString("category_name");
                tableModel.addRow(new Object[] { code, name, Price, Type });
            }
            con.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error loading data from database",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int insertMenu(String Code, String name, Double Price, int type) {
        StringBuilder insertSQL = new StringBuilder();
        insertSQL
                .append("INSERT INTO food_menu(food_code,food_name,price,category_id)")
                .append(" values('")
                .append(Code)
                .append("','")
                .append(name)
                .append("',")
                .append(Price)
                .append(",")
                .append(type)
                .append(")");
        int recordID = 0;
        Connection con = dbConnection.getConnection();
        try {
            Statement statement = con.createStatement();
            int rowsAffected = statement.executeUpdate(
                    insertSQL.toString(),
                    Statement.RETURN_GENERATED_KEYS
            );
            if (rowsAffected > 0) {
                ResultSet genKeys = statement.getGeneratedKeys();
                if (genKeys.next()) {
                    recordID = genKeys.getInt(1);
                }
                JOptionPane.showMessageDialog(null, "Record inserted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to insert the record.");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error connecting to the database: " + e.getMessage()
            );
        } finally {
            return recordID;
        }
    }
    private void updateMenu(String code, String name, Double Price, int type) {
        StringBuilder updateSQL = new StringBuilder();

        updateSQL.append("UPDATE food_menu SET")
                .append(" food_name='").append(name).append("',")
                .append(" price='").append(Price).append("',")
                .append(" category_id=").append(type)
                .append(" WHERE food_code='").append(code).append("'");
        Connection con = dbConnection.getConnection();
        try{
            Statement statement = con.createStatement();
            int rowsAffected = statement.executeUpdate(updateSQL.toString());
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Record updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update the record.");
            }
            con.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error " + e.getMessage());
        }
    }
    private void deleteMenu(String code) {
        StringBuilder deleteSQL = new StringBuilder();
        deleteSQL.append("DELETE FROM food_menu")
                .append(" WHERE food_menu.food_code='").append(code).append("'");
        Connection con = dbConnection.getConnection();
        try{
            Statement statement = con.createStatement();
            int rowsAffected = statement.executeUpdate(deleteSQL.toString());
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Record deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete the record.");
            }
            con.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error " + e.getMessage());
        }
    }
    public Menu() {
        setTitle("Login");
        initializedTable();
        initializedComboBox();
        txtcode.setEditable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1440, 768));
        setLocationRelativeTo(null);
        setContentPane(Main);
        setResizable(false);
        setVisible(true);

        tblfood.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()){
                    int selectedRow = tblfood.getSelectedRow();
                    if(selectedRow != -1){
                        Object selectedCode = tblfood.getValueAt(selectedRow,0);
                        loadMenuByCode(selectedCode.toString());
                    }
                }
            }
        });
        btnreset.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        clearControls(Menupanel);
                        loadMenu(tableModel);
                    }
                }
        );
        btnupdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!txtcode.getText().equals("")){
                    String Code = txtcode.getText();
                    String name = txtname.getText();
                    Double price;
                    try {
                        price = Double.valueOf(txtprice.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Please enter a valid number for the price.",
                                "Invalid Input",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return; // Exit the method if the input is invalid
                    }
                    int type = getKeyFromSelectedItem(cbotype);
                    updateMenu(Code,name,price,type);
                    clearControls(Menupanel);
                    loadMenu(tableModel);
                }
            }
        });
        btndelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!txtcode.getText().equals("")){
                    String code = txtcode.getText();
                    deleteMenu(code);
                    clearControls(Menupanel);
                    loadMenu(tableModel);
                }
            }
        });
        btnadd.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int recordID = 0;
                        if (txtcode.getText().equals("")) {
                            String code = MenuCode.generateMenuCode();
                            String name = txtname.getText();
                            Double price;
                            try {
                                price = Double.valueOf(txtprice.getText());
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Please enter a valid number for the price.",
                                        "Invalid Input",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return; // Exit the method if the input is invalid
                            }
                            int type = getKeyFromSelectedItem(cbotype);

                            recordID = insertMenu(code, name, price,type);
                            clearControls(Menupanel);
                            loadMenu(tableModel);
                        }
                        loadMenuByCode(String.valueOf(recordID));
                    }
                }
        );
        btnorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new Booking().setVisible(true);
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
        btnHis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                new History().setVisible(true);
            }
        });
    }
}

