package form;
import cls.New;
import db.dbConnection;
import form.WelcomeLoading;
import form.Menu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginForm extends JFrame {
    private JPanel Mainpanel;
    private JTextField txtUser;
    private JTextField txtPw;
    private JButton btncancel;
    private JButton btnLogin;
    private JPanel left;
    private JPanel loginPanel;
    private JPanel ButtonPanel;

    New New=new New();
    private void Login(String username, String password){

        Connection con=dbConnection.getConnection();
        StringBuilder query=new StringBuilder();
        query.append("SELECT username, password").append(" FROM user").append(" WHERE username='").append(username).append("'").append(" AND password='").append(password).append("'");
        try{
            Statement stm=con.createStatement();
            ResultSet rs=stm.executeQuery(query.toString());
                if(rs.next()){
                    Mainpanel.setVisible(false);
                    dispose();
                    Menu menu=new Menu();

                }else{
                    JOptionPane.showMessageDialog(null,"Invalid Username or Password");
                    txtUser.setText("");
                    txtPw.setText("");
                }


            con.close();
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,"Error loading data from database.","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    public LoginForm() {
        setTitle("Login");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1024,768));
        setLocationRelativeTo(null);
        setContentPane(Mainpanel);
        setResizable(true);
        setVisible(true);


        btnLogin.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                    String username = txtUser.getText();
                    String password = txtPw.getText();
                    Login(username, password);
            }


        });

        btncancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUser.setText("");
                txtPw.setText("");
            }
        });
    }
}


