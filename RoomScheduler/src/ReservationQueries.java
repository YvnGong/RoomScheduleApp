import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ReservationQueries {
    
    public static void addReservationEntry(String faculty, Date date, Integer seats)
    {
        Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());      
        String status;
        ArrayList<RoomEntry> possibleRooms = RoomQueries.getAllPossibleRooms(date);
        ArrayList<WaitlistEntry> waitList = WaitlistQueries.getAllWaitList();
        ArrayList<String> ReservatedFacultybyDate = new ArrayList<>();
        try
        {
            PreparedStatement ReservatedFaculty = DBConnection.getConnection().prepareStatement("SELECT FACULTY FROM JAVA.RESERVATIONS WHERE DATE = ?");
            ReservatedFaculty.setDate(1, date);                                   
            ResultSet resultSet = ReservatedFaculty.executeQuery();
           
           while(resultSet.next())
           {
               String next = new String(resultSet.getString("FACULTY"));
               ReservatedFacultybyDate.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        if(ReservatedFacultybyDate.contains(faculty)){
            status = String.format("Unable to process. Faculy %s had a reservation on this date already.", faculty);
            MainFrame.addReservationStatusLabel(status); 
        }
                
        else if (possibleRooms.isEmpty())                                                                           //if there is no magician available for the selected holiday add to the wait list
        {
            WaitlistQueries.addWaitlistEntry(faculty, date, seats, currentTimestamp);
            status = String.format("No room available right now. The reservation is placed in the waitlist.");
            MainFrame.addReservationStatusLabel(status);
        }
        else
        {
            String bestRoom = "";
            Integer min = 100;
            
            for (int i=0; i<possibleRooms.size(); i++){
                if(possibleRooms.get(i).getSeats()>=seats){
                    Integer num = possibleRooms.get(i).getSeats()-seats;
                    if (num < min){
                        min = num;
                        bestRoom = possibleRooms.get(i).getroomName();
                    }
                }
            }
            if(bestRoom == ""){
                for (WaitlistEntry w : waitList){
                    if (faculty == w.getFaculty() && date == (Date) w.getDate()){
                        return;
                    }
                }
                WaitlistQueries.addWaitlistEntry(faculty, date, seats, currentTimestamp);
                status = String.format("No room available right now. The reservation is placed in the waitlist.");
                MainFrame.addReservationStatusLabel(status); 
            }
            else{
                try
                {
                    PreparedStatement setReservation = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.RESERVATIONS (FACULTY, ROOM, DATE, SEATS, Timestamp) VALUES (?, ?, ?, ?, ?)");
                    setReservation.setString(1, faculty);
                    setReservation.setString(2, bestRoom);
                    setReservation.setDate(3, date);
                    setReservation.setInt(4, seats);
                    setReservation.setTimestamp(5, currentTimestamp);

                    setReservation.executeUpdate();                                       

                    status = String.format("Faculty: %s, room: %s, seats: %s, Date: %s , Reserved at: %s", faculty, bestRoom, seats, date, currentTimestamp);
                    
                    MainFrame.addReservationStatusLabel(status);        
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        }
    }
    
    public static void cancelReservation(String faculty, Date date)
    {
        String status;
        String newstatus;
        String waitlistStatus = "";
        try
        {
            //check if it is in the reservation table
            Integer count = 0;
            ArrayList<ReservationEntry> allReservations = getAllReservations();
            
            ArrayList<String> ReservatedFaculty = new ArrayList<>();
            ArrayList<Date> ReservatedDate = new ArrayList<>();
            try
            {
                PreparedStatement Reservated = DBConnection.getConnection().prepareStatement("SELECT count(*) FROM JAVA.RESERVATIONS WHERE FACULTY = ? AND DATE = ?");                                  
                Reservated.setString(1, faculty);
                Reservated.setDate(2, date);                                   
                ResultSet resultSet = Reservated.executeQuery();

                if(resultSet.next())
                {
                    count = resultSet.getInt(1);
                }         
            }
            catch(SQLException e)
            {
                e.printStackTrace();
                System.exit(0);
            }
            
            //System.out.println(count);

            if(count > 0){
                PreparedStatement statement = DBConnection.getConnection().prepareStatement("DELETE FROM RESERVATIONS WHERE FACULTY = ? AND DATE = ?");
                statement.setString(1, faculty);
                statement.setDate(2, date);
                statement.executeUpdate();
                
                //check waitlist and perform new reservations
                ArrayList<WaitlistEntry> waitList;
                waitList = WaitlistQueries.getAllWaitList();
            
                //deal with waitlist with new room
                for (WaitlistEntry w : waitList)
                {      
                    Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());      
                    ArrayList<RoomEntry> possibleRooms = RoomQueries.getAllPossibleRooms((Date) w.getDate());
                    if (possibleRooms.isEmpty() == false)                                                                           //if there is no magician available for the selected holiday add to the wait list
                    {
                        String bestRoom = "";
                        Integer min = 100;

                        for (int i=0; i<possibleRooms.size(); i++){
                            if(possibleRooms.get(i).getSeats()>=w.getSeats()){
                                Integer num = possibleRooms.get(i).getSeats()-w.getSeats();
                                if (num < min){
                                    min = num;
                                    bestRoom = possibleRooms.get(i).getroomName();
                                }
                            }
                        }

                        if(bestRoom != ""){
                            try
                            {
                                PreparedStatement setReservation = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.RESERVATIONS (FACULTY, ROOM, DATE, SEATS, Timestamp) VALUES (?, ?, ?, ?, ?)");
                                setReservation.setString(1, w.getFaculty());
                                setReservation.setString(2, bestRoom);
                                setReservation.setDate(3, (Date) w.getDate());
                                setReservation.setInt(4, w.getSeats());
                                setReservation.setTimestamp(5, currentTimestamp);

                                setReservation.executeUpdate(); 
                                WaitlistQueries.deleteWaitlistEntry(w.getFaculty(), (Date) w.getDate());

                                newstatus = String.format("Reserved Faculty: %s, room: %s, Date: %s %n", w.getFaculty(), bestRoom, w.getDate());  
                                waitlistStatus = waitlistStatus+newstatus;
                            }
                            catch (SQLException e)
                            {
                                e.printStackTrace();
                                System.exit(0);
                            }
                        }
                    }
                }
            }
            
            else
            {
                PreparedStatement WLstatement = DBConnection.getConnection().prepareStatement("DELETE FROM WAITLIST WHERE FACULTY = ? AND DATE = ?");
                WLstatement.setString(1, faculty);
                WLstatement.setDate(2, date);
                WLstatement.executeUpdate();
            }

            status = String.format("Reservation of Faculty: %s on Date: %s is canceled", faculty, date);
            
            MainFrame.cancelReservationStatusLabel(status, waitlistStatus); 
            }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static ArrayList<ReservationEntry> getAllReservations()
    {
        ArrayList<ReservationEntry> allReservation = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM RESERVATIONS");                                
            ResultSet resultSet = statement.executeQuery();
           
           while(resultSet.next())
           {
               ReservationEntry next = new ReservationEntry(resultSet.getString("FACULTY"), resultSet.getString("ROOM"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
               allReservation.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return allReservation;
    } 
    
    
    public static ArrayList<ReservationEntry> getReservationsByFaculty(String faculty)
    {
        ArrayList<ReservationEntry> ReservationsByFaculty = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM RESERVATIONS WHERE FACULTY = ?");
            statement.setString(1, faculty);                                   
            ResultSet resultSet = statement.executeQuery();
           
           while(resultSet.next())
           {
               ReservationEntry next = new ReservationEntry(resultSet.getString("FACULTY"), resultSet.getString("ROOM"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
               ReservationsByFaculty.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return ReservationsByFaculty;
    }

    public static ArrayList<ReservationEntry> getReservationsByDate(Date date)
    {
        ArrayList<ReservationEntry> ReservationsByDate = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT * FROM RESERVATIONS WHERE DATE = ?");
            statement.setDate(1, date);                                   
            ResultSet resultSet = statement.executeQuery();
           
           while(resultSet.next())
           {
               ReservationEntry next = new ReservationEntry(resultSet.getString("FACULTY"), resultSet.getString("ROOM"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
               ReservationsByDate.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return ReservationsByDate;
    }
    

    public static ArrayList<ReservationEntry> getRoomsReservedByDate(Date date)
    {
        ArrayList<ReservationEntry> RoomsReservedByDate = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement("SELECT ROOM FROM RESERVATIONS WHERE DATE = ?");
            statement.setDate(1, date);                                   
            ResultSet resultSet = statement.executeQuery();
           
           while(resultSet.next())
           {
               ReservationEntry next = new ReservationEntry(resultSet.getString("FACULTY"), resultSet.getString("ROOM"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
               RoomsReservedByDate.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return RoomsReservedByDate;
    }   
}
