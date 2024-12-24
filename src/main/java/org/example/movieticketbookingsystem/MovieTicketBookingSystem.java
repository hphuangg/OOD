package org.example.movieticketbookingsystem;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MovieTicketBookingSystem {


}

@Data
abstract class Seat {
    int id;
    int screenId;
    int row;
    int column;
    int additionalPrice;


    SeatStatus seatStatus;

    public Seat(int id, int screenId, int row, int column, int additionalPrice) {
        this.id = id;
        this.screenId = screenId;
        this.row = row;
        this.column = column;
        this.additionalPrice = additionalPrice;
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public int getTotalPrice() {
        return this.additionalPrice;
    }

    public boolean isAvailable() {
        return seatStatus.equals(SeatStatus.AVAILABLE);
    }
}


enum SeatStatus {
    UNAVAILABLE, AVAILABLE, RESERVED, BOOKED, MAINTENANCE
}

class SliverSeat extends Seat {

    public SliverSeat(int id, int screenId, int row, int column) {
        super(id, screenId, row, column, 0);
    }
}

// and others

class DiamondrSeat extends Seat {

    public DiamondrSeat(int id, int screenId, int row, int column, int additionalPrice) {
        super(id, screenId, row, column, 100);
    }

    public int getExtraPerksCost() {
        return 10;
    }

    @Override
    public int getTotalPrice() {
        return this.additionalPrice + getExtraPerksCost();
    }
}

@Data
class Screen {
    int id;
    String theaterId;
    String name;
    List<List<Seat>> seats;
}

class Theater {
    int id;
    String screenId;
    String name;
    // Location location;
}

class Movie {
    BigInteger id;
    String name;
    MovieGenre genre;
    Date releaseDate;

    List<ShowTime> showTimes;
}

enum MovieGenre {
    Comedy, Drama, ScienceFiction
}

class ShowTime {
    int id;
    int movieId;
    Screen screen;
    HashMap<Seat, Boolean> availableSeats;

    public ShowTime(int id, int movieId, Screen screen) {
        this.id = id;
        this.movieId= movieId;
        this.screen = screen;

        screen.getSeats().forEach(row ->
                row.forEach(seat -> availableSeats.put(seat, true)));
    }

    public Booking reserve(Seat seat, TicketType ticketType) {
        if (!availableSeats.containsKey(seat)) {
            System.out.println("seat is not found.");
            return null;
        }

        if (!availableSeats.get(seat)) {
           System.out.println("seat is not unavailable.");
           return null;
       }

        availableSeats.put(seat, false);
        seat.setSeatStatus(SeatStatus.RESERVED);

       return new Booking();
    }

}

class MovieTicket {
    int id;
    TicketType ticketType;
    ShowTime showTime;
    Seat seat;
}

@Data
abstract class TicketType {
    int basePrice;
    float multiplier;

    TicketType(int basePrice, float multiplier) {
        this.basePrice = basePrice;
        this.multiplier = multiplier;
    }

    float getPrice() {
        return basePrice * multiplier;
    }

}

class AdultTicket extends TicketType {
    AdultTicket(int basePrice) {
        super(basePrice, 1);
    }
}

class ChildrenTicket extends TicketType {
    ChildrenTicket(int basePrice) {
        super(basePrice, 0.5f);
    }
}

class Booking {
    int bookingId;
    int showTimeId;
    List<MovieTicket> tickets;
    BookingStatus bookingStatus;
    Payment payment;
}

enum BookingStatus {
    PENDING, PAYMENT_COMPLETED, CANCELED
}

abstract class Payment {

}

class CashPayment extends Payment {

}

// search
