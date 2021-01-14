import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class RoomQueries {
    public static ArrayList<String> getAllRooms()
    {
        ArrayList<String> allRoom = new ArrayList<>();
        
        try
        {
           ResultSet resultSet = DBConnection.getConnection().prepareStatement("SELECT * FROM JAVA.ROOMS").executeQuery();
           
           while(resultSet.next())
           {
               allRoom.add(resultSet.getString("NAME"));
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return allRoom;
    }

    public static ArrayList getAllPossibleRooms(Date date)
    {
        ArrayList<RoomEntry> possibleRooms = new ArrayList<>();
        
        try
        {
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(
                    "SELECT NAME, SEATS FROM JAVA.ROOMS WHERE name NOT IN (SELECT ROOM FROM RESERVATIONS WHERE DATE = ?)"); 
            statement.setDate(1, date);
            ResultSet resultSet = statement.executeQuery();
           
           while(resultSet.next())
           {
               RoomEntry next = new RoomEntry(resultSet.getString("NAME"), resultSet.getInt("SEATS"));
               possibleRooms.add(next);
           }         
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        
        return possibleRooms;
    }
    
    public static void addRoom(String roomName, Integer seats)
    {
        String status;
        String waitlistStatus = "";
        String newstatus;
        try
        {
            PreparedStatement setRoom = DBConnection.getConnection().prepareStatement("INSERT INTO JAVA.ROOMS (NAME, SEATS) VALUES (?, ?)");
            setRoom.setString(1, roomName);
            setRoom.setInt(2, seats);
            setRoom.executeUpdate();
            
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

                            newstatus = String.format("Faculty: %s, room: %s, Date: %s \n", w.getFaculty(), bestRoom, w.getDate());  
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
            
        status = String.format("ROOM %s with %s seats added to ROOMS table", roomName, seats);
        MainFrame.addNewRoomStatusLabel(status, waitlistStatus); 
        } 
        catch (SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    public static void dropRoom(String roomName)
    {
        String status;
        String rstatus = "";
        String MovetoWL = "";
        String newstatus = "";
        String newaddedStatus = "";
        ArrayList<ReservationEntry> WLReservation = new ArrayList<>();
        
        try
        {
            PreparedStatement movetoWL = DBConnection.getConnection().prepareStatement("SELECT * FROM RESERVATIONS WHERE (ROOM) = ? ORDER BY Timestamp");
            movetoWL.setString(1, roomName);
            ResultSet resultSet = movetoWL.executeQuery();
            
            while(resultSet.next())
            {
                ReservationEntry next = new ReservationEntry(resultSet.getString("FACULTY"), resultSet.getString("ROOM"), resultSet.getDate("DATE"), resultSet.getInt("SEATS"), resultSet.getTimestamp("Timestamp"));
                WLReservation.add(next);
            }
            
            //deletes from the room table
            PreparedStatement dropRoom = DBConnection.getConnection().prepareStatement("DELETE FROM ROOMS WHERE NAME = ?");
            dropRoom.setString(1, roomName);
            dropRoom.executeUpdate();
            
            //deletes from the reservation table
            dropRoom = DBConnection.getConnection().prepareStatement("DELETE FROM RESERVATIONS WHERE ROOM = ?");
            dropRoom.setString(1, roomName);
            dropRoom.executeUpdate();
            
            
            for (ReservationEntry r : WLReservation){
                WaitlistQueries.addWaitlistEntry(r.getFaculty(), (Date) r.getDate(), r.getSeats(), r.getTimestamp());           
                rstatus = String.format("Faculty %s on Date %s is moved to Waitlist %n", r.getFaculty(), r.getDate());
                MovetoWL = MovetoWL+rstatus;
            }
            
            //reReserved the waitlisted Room
            for (ReservationEntry r : WLReservation)
            {      
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());      
                ArrayList<RoomEntry> possibleRooms = RoomQueries.getAllPossibleRooms((Date) r.getDate());
                if (possibleRooms.isEmpty() == false)                                                                           //if there is no magician available for the selected holiday add to the wait list
                {
                    String bestRoom = "";
                    Integer min = 100;

                    for (int i=0; i<possibleRooms.size(); i++){
                        if(possibleRooms.get(i).getSeats()>=r.getSeats()){
                            Integer num = possibleRooms.get(i).getSeats()-r.getSeats();
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
                            setReservation.setString(1, r.getFaculty());
                            setReservation.setString(2, bestRoom);
                            setReservation.setDate(3, (Date) r.getDate());
                            setReservation.setInt(4, r.getSeats());
                            setReservation.setTimestamp(5, currentTimestamp);

                            setReservation.executeUpdate(); 
                            WaitlistQueries.deleteWaitlistEntry(r.getFaculty(), (Date) r.getDate());

                            newstatus = String.format("Reserved: Faculty: %s, room: %s, Date: %s %n", r.getFaculty(), bestRoom, r.getDate());  
                            newaddedStatus = newaddedStatus+newstatus;
                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                            System.exit(0);
                        }
                    }
                }
            }   


            status = String.format("%s has been dropped from ROOMS", roomName);
            MainFrame.setDropRoomStatus(status, MovetoWL, newaddedStatus);
  
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
  
}
