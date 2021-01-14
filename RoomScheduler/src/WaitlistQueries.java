import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;

public class WaitlistQueries {
    public static void addWaitlistEntry(String faculty, Date date, Integer seats, Timestamp timestamp)
    {
        //String status;
        
        try
        {
            PreparedStatement setList = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.WAITLIST (faculty, date, seats, timestamp) VALUES (?, ?, ?, ?)");
            setList.setString(1, faculty);
            setList.setDate(2, date);
            setList.setInt(3, seats);
            setList.setTimestamp(4, timestamp);

            setList.executeUpdate();

            //status = String.format("%s has been added to the waitlist for %s %s", faculty, date, seats);
            //MagicianAgentUI.setScheduleTextArea(status);     
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static void deleteWaitlistEntry(String faculty, Date date)
    {
        //String status;
        
        try
        {
            //deletes from the WAITLIST table
            PreparedStatement cancelCustomer = DBConnection.getConnection().prepareStatement("DELETE FROM WAITLIST WHERE FACULTY = ? AND DATE = ? ");
            //String strdate = date.toString();
            cancelCustomer.setString(1, faculty);
            cancelCustomer.setDate(2, date);
            cancelCustomer.executeUpdate();
            
            //status = String.format("%s %s has been removed from the Waiting List", faculty, date);
            //MagicianAgentUI.setScheduleTextArea(status);
  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }


    public static ArrayList<WaitlistEntry> getAllWaitList()
    {
        ArrayList<WaitlistEntry> waitList = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM WAITLIST ORDER BY DATE, TIMESTAMP");
            ResultSet resultSet = statement.executeQuery();
           
            while(resultSet.next())
            {
                WaitlistEntry next = new WaitlistEntry(resultSet.getString("FACULTY"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
                waitList.add(next);
            }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return waitList;
    }
    
    public static ArrayList<WaitlistEntry> getWaitListByDate(Date date)
    {
        ArrayList<WaitlistEntry> waitListbydate = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM WaitingList WHERE Date = ? ORDER BY Timestamp");
            //String strdate = date.toString();
            statement.setDate(1, date);
            ResultSet resultSet = statement.executeQuery();
           
            while(resultSet.next())
            {
                WaitlistEntry next = new WaitlistEntry(resultSet.getString("FACULTY"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
                waitListbydate.add(next);
            }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return waitListbydate;
    }
    
    
    public static ArrayList<WaitlistEntry> getWaitListByFaculty(String faculty)
    {
        ArrayList<WaitlistEntry> waitListbyfaculty = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM WAITLIST WHERE FACULTY = ? ORDER BY DATE, Timestamp");
            statement.setString(1, faculty);
            ResultSet resultSet = statement.executeQuery();
           
            while(resultSet.next())
            {
                WaitlistEntry next = new WaitlistEntry(resultSet.getString("FACULTY"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
                waitListbyfaculty.add(next);
            }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return waitListbyfaculty;
    }
}
