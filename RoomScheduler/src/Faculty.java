import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Faculty {
   
    
    //gets an ArrayList of all the customers
    public static ArrayList getFaculty()
    {
        ArrayList<String> faculty = new ArrayList<>();
        
        try
        {
           ResultSet resultSet = DBConnection.getConnection().prepareStatement("SELECT * FROM JAVA.FACULTY").executeQuery();
           
           while(resultSet.next())
           {
               faculty.add(resultSet.getString("NAME"));
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return faculty;
    }
    
    public static void addFaculty(String name)
    {
        String status;
        try
        {
            PreparedStatement setCustomer = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.FACULTY (NAME) VALUES (?)");
            setCustomer.setString(1, name);
            setCustomer.executeUpdate();
            status = String.format("Faculty member %s has been added to Faculty database", name);
            MainFrame.addFacultyStatusLabel(status);
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            status = String.format("Unable to add Faculty member %s to Faculty database.", name);
            MainFrame.addFacultyStatusLabel(status);
            //System.exit(0);
        }
    }

}
