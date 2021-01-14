import java.sql.Timestamp;
import java.util.Date;

public class ReservationEntry{

    private final String faculty;
    private final String room;
    private final Date date;
    private final Integer seats;
    private final Timestamp timestamp;
    
    ReservationEntry(String faculty, String room, Date date, Integer seats, Timestamp timestamp)
    {
        this.faculty = faculty;
        this.room = room;
        this.date = date;
        this.seats = seats;
        this.timestamp = timestamp;
    }

    public String getFaculty()
    {
        return faculty;
    }

    public String getRoom()
    {
        return room;
    }

    public Date getDate()
    {
        return date;
    }

    public Integer getSeats(){
        return seats;
    }
    
    public Timestamp getTimestamp()
    {
        return timestamp;
    }  

}
