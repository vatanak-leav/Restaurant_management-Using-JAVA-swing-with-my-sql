package form;

import cls.MenuCode;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Timer;
import java.util.TimerTask;

public class Book extends JFrame {
    private JPanel Main;
    private JPanel leftpanel;
    private JButton btnmenu;
    private JButton btnorder;
    private JButton btnbooking;
    private JButton btnHis;
    private JButton logoutButton;
    private JPanel bookpanel;
    private JTable tblbooked;
    private JTextField txtname;
    private JTextField txtphone;
    private JTextField txtroom;
    private JButton btnreset;
    private JButton btndelete;
    private JButton btnadd;
    private JButton btnconfirm;
    private JPanel roompanel;
    private JTable tblroom;
    private JComboBox<String> cbostatue;
    private JPanel datepicker;
    private DatePicker dobPicker;
    DefaultTableModel tableModel = new DefaultTableModel();
    DefaultTableModel tableModel2 = new DefaultTableModel();

    private void initializeDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd-MMM-yyyy");
        dobPicker = new DatePicker(dateSettings);
        datepicker.add(dobPicker);
        dobPicker.setDate(LocalDate.now());
    }

    private void initializeTablebook() {
        tableModel.addColumn("Name");
        tableModel.addColumn("Contact");
        tableModel.addColumn("Room");
        tableModel.addColumn("Date");
        tableModel.addColumn("Status");
        tblbooked.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblbooked.setModel(tableModel);
        tblbooked.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblbooked);
        scrollPane.setPreferredSize(new Dimension(tblbooked.getPreferredScrollableViewportSize().width, 400));
        JPanel book = new JPanel(new BorderLayout());
        book.add(scrollPane, BorderLayout.CENTER);
        roompanel.add(book);
    }

    private void initializeTableroom() {
        tableModel2.addColumn("Room Number");
        tableModel2.addColumn("Capacity");
        tableModel2.addColumn("Status");
        tblroom.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblroom.setModel(tableModel2);
        tblroom.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(tblroom);
        scrollPane.setPreferredSize(new Dimension(tblroom.getPreferredScrollableViewportSize().width, 400));
        JPanel room = new JPanel(new BorderLayout());
        room.add(scrollPane, BorderLayout.CENTER);
        bookpanel.add(room);
    }

    private void loadFreeRooms() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();
            String query = "SELECT room_number, capacity, statue FROM room";
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            tableModel2.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                int roomId = rs.getInt("room_number");
                int capacity = rs.getInt("capacity");
                String status = rs.getString("statue");
                tableModel2.addRow(new Object[]{roomId, capacity, status});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading available rooms: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadBookings() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();
            String query = "SELECT costumer_name, contact_numberr, room_number, booking_date, booking_statue FROM booking JOIN room ON room.room_id=booking.table_id ORDER BY booking_date DESC";
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                String name = rs.getString("costumer_name");
                String contact = rs.getString("contact_numberr");
                int roomId = rs.getInt("room_number");
                LocalDate date = rs.getDate("booking_date").toLocalDate();
                String status = rs.getString("booking_statue");
                tableModel.addRow(new Object[]{name, contact, roomId, date, status});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    public Book() {
        setTitle("Booking");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initializeTablebook();
        initializeTableroom();
        initializeDatePicker();
        setMinimumSize(new Dimension(1440, 768));
        setLocationRelativeTo(null);
        setContentPane(Main);
        setResizable(false);
        setVisible(true);

        // Load available rooms and bookings into the tables
        loadFreeRooms();
        loadBookings();

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

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Initialize cbostatue JComboBox with "Pending" and "Done"
        cbostatue.addItem("Pending");
        cbostatue.addItem("Done");

        btnadd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBooking();
            }
        });

        btndelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBooking();
            }
        });

        btnconfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBooking();
            }
        });

        // Add a selection listener to the room table
        tblroom.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedRoomData();
                }
            }
        });

        // Add a selection listener to the booked table
        tblbooked.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedBookingData();
                }
            }
        });

        // Schedule a task to update room status daily
        scheduleRoomStatusUpdateTask();
        btnreset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtname.setText("");
                txtphone.setText("");
                txtroom.setText("");
            }
        });
    }

    private void loadSelectedRoomData() {
        // Get the selected row
        int selectedRow = tblroom.getSelectedRow();
        if (selectedRow == -1) {
            return; // No row is selected
        }

        // Get the room number from the selected row
        int roomNumber = (int) tableModel2.getValueAt(selectedRow, 0);

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();

            // Retrieve booking details for the selected room number
            String query = "SELECT * ,room_number FROM booking JOIN room ON room.room_id=booking.table_id WHERE table_id = (SELECT room_id FROM room WHERE room_number = ?) ORDER BY booking_date DESC";
            ps = con.prepareStatement(query);
            ps.setInt(1, roomNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("costumer_name");
                String phone = rs.getString("contact_numberr");
                LocalDate date = rs.getDate("booking_date").toLocalDate();
                String status = rs.getString("booking_statue");

                // Set the values in the input fields
                txtname.setText(name);
                txtphone.setText(phone);
                txtroom.setText(String.valueOf(roomNumber));
                cbostatue.setSelectedItem(status);
                dobPicker.setDate(date);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

    private void loadSelectedBookingData() {
        // Get the selected row
        int selectedRow = tblbooked.getSelectedRow();
        if (selectedRow == -1) {
            return; // No row is selected
        }

        // Get the data from the selected row
        String name = tableModel.getValueAt(selectedRow, 0).toString();
        String phone = tableModel.getValueAt(selectedRow, 1).toString();
        int roomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        LocalDate date = LocalDate.parse(tableModel.getValueAt(selectedRow, 3).toString());
        String status = tableModel.getValueAt(selectedRow, 4).toString();

        // Set the values in the input fields
        txtname.setText(name);
        txtphone.setText(phone);
        txtroom.setText(String.valueOf(roomNumber));
        cbostatue.setSelectedItem(status);
        dobPicker.setDate(date);
    }

    private void addBooking() {
        String name = txtname.getText();
        String phone = txtphone.getText();
        int roomNumber;
        try {
            roomNumber = Integer.parseInt(txtroom.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the Room.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return; // Exit on invalid room number input
        }
        LocalDate date = dobPicker.getDate();
        String status = cbostatue.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || date == null || status == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();

            // Confirm the room exists and is available
            String checkRoomQuery = "SELECT room_id FROM room WHERE room_number = ? AND statue = 'available'";
            ps = con.prepareStatement(checkRoomQuery);
            ps.setInt(1, roomNumber);
            rs = ps.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Invalid or unavailable room number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int roomId = rs.getInt("room_id");
            rs.close();
            ps.close();

            // Add the booking details into the booking table
            String insertBookingQuery = "INSERT INTO booking (costumer_name, contact_numberr, table_id, booking_date, booking_statue) VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(insertBookingQuery);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, roomId);
            ps.setDate(4, Date.valueOf(date));
            ps.setString(5, status);
            ps.executeUpdate();
            ps.close();

            // Update the room status based on booking status
            String updateRoomQuery = status.equals("Done") ? "UPDATE room SET statue = 'available' WHERE room_id = ?" : "UPDATE room SET statue = 'occupied' WHERE room_id = ?";
            ps = con.prepareStatement(updateRoomQuery);
            ps.setInt(1, roomId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Room successfully booked!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Reload available rooms and bookings to update the tables
            loadFreeRooms();
            loadBookings();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error booking room: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void deleteBooking() {
        int selectedRow = tblbooked.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = tableModel.getValueAt(selectedRow, 0).toString();
        String phone = tableModel.getValueAt(selectedRow, 1).toString();
        int roomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        LocalDate date = LocalDate.parse(tableModel.getValueAt(selectedRow, 3).toString());

        Connection con = null;
        PreparedStatement ps = null;
        PreparedStatement updatePs = null;
        ResultSet rs = null;

        try {
            con = db.dbConnection.getConnection();

            // Begin transaction
            con.setAutoCommit(false);

            // Delete the booking from the booking table
            String deleteBookingQuery = "DELETE FROM booking WHERE costumer_name = ? AND contact_numberr = ? AND table_id = (SELECT room_id FROM room WHERE room_number = ?) AND booking_date = ?";
            ps = con.prepareStatement(deleteBookingQuery);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, roomNumber);
            ps.setDate(4, Date.valueOf(date));
            ps.executeUpdate();
            ps.close();

            // First, retrieve the room_id by executing a separate query
            String selectRoomIdQuery = "SELECT room_id FROM room WHERE room_number = ?";
            ps = con.prepareStatement(selectRoomIdQuery);
            ps.setInt(1, roomNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                int roomId = rs.getInt("room_id");

                // Update the room status to 'available'
                String updateRoomQuery = "UPDATE room SET statue = 'available' WHERE room_id = ?";
                updatePs = con.prepareStatement(updateRoomQuery);
                updatePs.setInt(1, roomId);
                updatePs.executeUpdate();
            }

            // Commit transaction
            con.commit();

            JOptionPane.showMessageDialog(this, "Booking successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Reload available rooms and bookings to update the tables
            loadFreeRooms();
            loadBookings();
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    // Rollback transaction in case of error
                    con.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Error deleting booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (updatePs != null) updatePs.close();
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

//    private void updateBooking() {
//        int selectedRow = tblbooked.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        String oldName = tableModel.getValueAt(selectedRow, 0).toString();
//        String oldPhone = tableModel.getValueAt(selectedRow, 1).toString();
//        int oldRoomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
//        LocalDate oldDate = LocalDate.parse(tableModel.getValueAt(selectedRow, 3).toString());
//
//        String newName = txtname.getText();
//        String newPhone = txtphone.getText();
//        int newRoomNumber;
//        try {
//            newRoomNumber = Integer.parseInt(txtroom.getText());
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(
//                    null,
//                    "Please enter a valid number for the Room.",
//                    "Invalid Input",
//                    JOptionPane.ERROR_MESSAGE
//            );
//            return; // Exit the method if the input is invalid
//        }
//        LocalDate newDate = dobPicker.getDate();
//        String newStatus = cbostatue.getSelectedItem().toString();
//
//        if (newName.isEmpty() || newPhone.isEmpty() || newDate == null || newStatus == null) {
//            JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        Connection con = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//
//        try {
//            con = db.dbConnection.getConnection();
//
//            // Update the booking details in the booking table
//            String updateBookingQuery = "UPDATE booking SET costumer_name = ?, contact_numberr = ?, table_id = (SELECT room_id FROM room WHERE room_number = ?), booking_date = ?, booking_statue = ? WHERE costumer_name = ? AND contact_numberr = ? AND table_id = (SELECT room_id FROM room WHERE room_number = ?) AND booking_date = ?";
//            ps = con.prepareStatement(updateBookingQuery);
//            ps.setString(1, newName);
//            ps.setString(2, newPhone);
//            ps.setInt(3, newRoomNumber);
//            ps.setDate(4, Date.valueOf(newDate));
//            ps.setString(5, newStatus);
//            ps.setString(6, oldName);
//            ps.setString(7, oldPhone);
//            ps.setInt(8, oldRoomNumber);
//            ps.setDate(9, Date.valueOf(oldDate));
//            ps.executeUpdate();
//            ps.close();
//
//            // If room number has changed, update the status of old and new rooms
//            if (oldRoomNumber != newRoomNumber) {
//                // Mark old room as available if it is no longer booked
//                String updateOldRoomStatusQuery = "UPDATE room SET statue = 'available' WHERE room_id = (SELECT room_id FROM room WHERE room_number = ?) AND NOT EXISTS (SELECT 1 FROM booking WHERE table_id = room_id AND booking_statue = 'Pending')";
//                ps = con.prepareStatement(updateOldRoomStatusQuery);
//                ps.setInt(1, oldRoomNumber);
//                ps.executeUpdate();
//                ps.close();
//
//                // Mark new room as occupied
//                String updateNewRoomStatusQuery = "UPDATE room SET statue = 'occupied' WHERE room_id = (SELECT room_id FROM room WHERE room_number = ?)";
//                ps = con.prepareStatement(updateNewRoomStatusQuery);
//                ps.setInt(1, newRoomNumber);
//                ps.executeUpdate();
//            }
//
//            JOptionPane.showMessageDialog(this, "Booking successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
//
//            // Reload available rooms and bookings to update the tables
//            loadFreeRooms();
//            loadBookings();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Error updating booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
//
//        } finally {
//            try {
//                if (ps != null) ps.close();
//                if (con != null) con.close();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
private void updateBooking() {
    int selectedRow = tblbooked.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a booking to update.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    String oldName = tableModel.getValueAt(selectedRow, 0).toString();
    String oldPhone = tableModel.getValueAt(selectedRow, 1).toString();
    int oldRoomNumber = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
    LocalDate oldDate = LocalDate.parse(tableModel.getValueAt(selectedRow, 3).toString());

    String newName = txtname.getText();
    String newPhone = txtphone.getText();
    int newRoomNumber;
    try {
        newRoomNumber = Integer.parseInt(txtroom.getText());
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter a valid number for the Room.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        return;
    }
    LocalDate newDate = dobPicker.getDate();
    String newStatus = cbostatue.getSelectedItem().toString();

    if (newName.isEmpty() || newPhone.isEmpty() || newDate == null || newStatus == null) {
        JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = db.dbConnection.getConnection();

        // Begin transaction
        con.setAutoCommit(false);

        // Retrieve IDs for old and new rooms
        int oldRoomId = getRoomIdByNumber(con, oldRoomNumber);
        int newRoomId = getRoomIdByNumber(con, newRoomNumber);

        // Update the booking details
        String updateBookingQuery = "UPDATE booking SET costumer_name = ?, contact_numberr = ?, table_id = ?, booking_date = ?, booking_statue = ? WHERE costumer_name = ? AND contact_numberr = ? AND table_id = ? AND booking_date = ?";
        ps = con.prepareStatement(updateBookingQuery);
        ps.setString(1, newName);
        ps.setString(2, newPhone);
        ps.setInt(3, newRoomId);
        ps.setDate(4, Date.valueOf(newDate));
        ps.setString(5, newStatus);
        ps.setString(6, oldName);
        ps.setString(7, oldPhone);
        ps.setInt(8, oldRoomId);
        ps.setDate(9, Date.valueOf(oldDate));
        ps.executeUpdate();
        ps.close();

        // Update the room status based on the booking status
        if (oldRoomNumber != newRoomNumber || newStatus.equals("Done")) {
            // Mark old room as available if it is no longer booked
            String updateOldRoomStatusQuery = "UPDATE room SET statue = 'available' WHERE room_id = ?";
            ps = con.prepareStatement(updateOldRoomStatusQuery);
            ps.setInt(1, oldRoomId);
            ps.executeUpdate();
            ps.close();

            // Mark new room as occupied unless its status is 'Done'
            String updateNewRoomStatusQuery = newStatus.equals("Done") ?
                    "UPDATE room SET statue = 'available' WHERE room_id = ?" :
                    "UPDATE room SET statue = 'occupied' WHERE room_id = ?";
            ps = con.prepareStatement(updateNewRoomStatusQuery);
            ps.setInt(1, newRoomId);
            ps.executeUpdate();
        }

        // Commit transaction
        con.commit();

        JOptionPane.showMessageDialog(this, "Booking successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);

        // Reload available rooms and bookings to update the tables
        loadFreeRooms();
        loadBookings();

    } catch (SQLException e) {
        e.printStackTrace();
        if (con != null) {
            try {
                // Rollback transaction in case of error
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
        JOptionPane.showMessageDialog(this, "Error updating booking: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    private int getRoomIdByNumber(Connection con, int roomNumber) throws SQLException {
        String query = "SELECT room_id FROM room WHERE room_number = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, roomNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("room_id");
                } else {
                    throw new SQLException("Room not found for room number: " + roomNumber);
                }
            }
        }
    }

    private void scheduleRoomStatusUpdateTask() {
        TimerTask updateRoomStatusTask = new TimerTask() {
            @Override
            public void run() {
                Connection con = null;
                PreparedStatement ps = null;

                try {
                    con = db.dbConnection.getConnection();

                    // Update status of rooms whose bookings are not 'Pending'
                    String updateRoomStatusQuery = "UPDATE room SET statue = 'available' WHERE room_id NOT IN (SELECT table_id FROM booking WHERE booking_statue = 'Pending')";
                    ps = con.prepareStatement(updateRoomStatusQuery);
                    ps.executeUpdate();

                    // Reload the available rooms table to reflect changes
                    loadFreeRooms();

                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ps != null) ps.close();
                        if (con != null) con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        Timer timer = new Timer(true); // Run the timer as a daemon
        long delay = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        timer.schedule(updateRoomStatusTask, delay, delay); // Schedule task to run daily
    }
}