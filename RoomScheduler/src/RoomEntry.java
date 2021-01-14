import java.security.Timestamp;
import java.util.Date;

public class RoomEntry {
    private final String roomName;
    private final Integer seats;
    
    RoomEntry(String roomName, Integer seats)
    {
        this.roomName = roomName;
        this.seats = seats;
    }

    //getters
    public String getroomName()
    {
        return roomName;
    }

    public Integer getSeats(){
        return seats;
    }

}
