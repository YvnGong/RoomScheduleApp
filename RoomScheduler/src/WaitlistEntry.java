import java.sql.Timestamp;
import java.util.Date;

public class WaitlistEntry {

    private final String faculty;
    private final Date date;
    private final Integer seats;
    private final Timestamp timestamp;
    
    WaitlistEntry(String faculty, Date date, Integer seats, Timestamp timestamp)
    {
        this.faculty = faculty;
        this.date = date;
        this.seats = seats;
        this.timestamp = timestamp;
    }

    public String getFaculty()
    {
        return faculty;
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
