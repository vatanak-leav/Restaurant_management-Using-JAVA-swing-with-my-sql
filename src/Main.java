import form.*;
import db.dbConnection;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Connection con = dbConnection.getConnection();
        if (con != null) {
            System.out.println("Connection is established successfully.");
        } else {
            System.out.println("Can't connect to database");
        }

         LoginForm login=new LoginForm();
         // Menu home = new Menu();
        //Booking booking = new Booking();
        //bill bill = new bill();
        //Book book = new Book();
        //History history = new History();
    }
}