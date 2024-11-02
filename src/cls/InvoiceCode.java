package cls;

import db.dbConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InvoiceCode {
    private static final String prefix = "INV";
    private static final int nbDigit =4;

    private static String formatNumber(int nbDigits, int number){
        String formatString = "%0"+nbDigits+"d";
        return String.format(formatString,number);
    }
    private static String getMenuCode(){
        Connection con = dbConnection.getConnection();
        String code = null;
        //get latest code from db
        try{
            StringBuilder query = new StringBuilder();
            query.append("SELECT invoice")
                    .append(" FROM ")
                    .append(" bill")
                    .append(" ORDER BY invoice DESC LIMIT 1");
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query.toString());
            if (resultSet.next()){
                code = resultSet.getString("invoice");
            }
            con.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }
        finally {
            return code;
        }
    }
    public static String generateInvoiceCode(){
        String newNumberPart="";
        try{
            String oldCode = getMenuCode();
            if (oldCode!=null)
            {
                String numberPart = oldCode.substring(prefix.length());
                int numberValue = Integer.parseInt(numberPart);
                numberValue ++;
                newNumberPart = formatNumber(nbDigit,numberValue);
            }
            else{
                newNumberPart = formatNumber(nbDigit,1);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            return prefix.concat(newNumberPart);
        }
    }
}
