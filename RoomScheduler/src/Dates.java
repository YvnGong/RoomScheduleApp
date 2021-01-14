import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Dates {

    public static ArrayList<String> getDates()
    {
        ArrayList<String> dates = new ArrayList<>();
        
        try
        {
           ResultSet resultSet = DBConnection.getConnection().prepareStatement("SELECT * FROM JAVA.DATES").executeQuery();
           
           while(resultSet.next())
           {
               dates.add(resultSet.getString("Dates"));
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return dates;
    }
    
    public static void addDate(Date date)
    {
        String status;
        try
        {
            PreparedStatement setDate = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.DATES (DATES) VALUES (?)");
            setDate.setDate(1, date);
            setDate.executeUpdate();
            
        status = String.format("%s has been added to the DATES database", date);
        MainFrame.addNewDateStatus(status);
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
