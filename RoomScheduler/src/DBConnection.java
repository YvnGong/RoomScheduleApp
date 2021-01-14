import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {

    private static final String DATABASE_URL = "jdbc:derby://localhost:1527/RoomSchedulerDBYiyunGongypg5063";       
    private static final String user = "java";                                                    
    private static final String password = "java";
    private static Connection connection;

    public static Connection getConnection()
    { 
        try
        {  
            connection = DriverManager.getConnection(DATABASE_URL, user, password);         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.out.println("Could not open database");
            System.exit(0);
        }
        
        return connection;
    }

}
