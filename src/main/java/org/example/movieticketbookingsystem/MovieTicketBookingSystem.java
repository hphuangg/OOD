package org.example.movieticketbookingsystem;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;
import java.util.*;

/*
* TODO:
*  1. apply dynamic pricing for seats and tickets
*     - peakHour, weekday,weekend / holiday
*  2. apply discount / group discount
*  3. search
* */

public class MovieTicketBookingSystem {

    public static void main(String[] args) {
        List<List<Seat>> seats = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < 10; i++) {
            List<Seat> row = new ArrayList<>();
            for (int j = 0; j < 10 ; j++) {
                row.add(new SliverSeat(id++, 1, i, j));
            }
            seats.add(row);
        }

        Screen screen1 = new Screen(1, 1, "t1", seats);

        ShowTime showTime = new ShowTime(1, 1, screen1);

        // showTime.showAvailableSeat()
        Seat seat = showTime.getSeat(5, 5);
        boolean reserved = showTime.reserve(seat);
        if (!reserved) {
            System.out.println("Seat reserved failed. \n" + seat);
            return;
        }

        System.out.println("Seat reserved successfully. \n" + seat);

        // create a booking
        List<MovieTicket> movieTickets = new ArrayList<>();
        movieTickets.add(new MovieTicket(1, new AdultTicket(100), showTime, seat));
        Booking booking = new Booking(1, showTime.getId(), movieTickets, BookingStatus.PENDING, null);
        System.out.println("create a booking successfully. \n" + booking);
        System.out.println("total price (before payment): " + booking.getTotalPrice());

        // pay
        float totalPrice = booking.getTotalPrice();
        CashPayment cashPayment = new CashPayment(totalPrice);
        booking.completePayment(cashPayment);
        System.out.println("confirm a booking successfully. \n" + booking);
    }

}

@Data
@AllArgsConstructor
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
@AllArgsConstructor
class Screen {
    int id;
    int theaterId;
    String name;
    List<List<Seat>> seats;
}

class Theater {
    int id;
    int screenId;
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

@Data
class ShowTime {
    int id;
    int movieId;
    Screen screen;
    HashMap<Seat, SeatStatus> availableSeats;

    public ShowTime(int id, int movieId, Screen screen) {
        this.id = id;
        this.movieId= movieId;
        this.screen = screen;

        availableSeats = new HashMap<>();
        screen.getSeats().forEach(row ->
                row.forEach(seat -> availableSeats.put(seat, SeatStatus.AVAILABLE)));
    }

    public Seat getSeat(int row, int col) {
        Seat reservedSeat = null;
        for (Seat seat : availableSeats.keySet()) {
            if (seat.getRow() == row && seat.getColumn() == col) {
                return seat;
            }
        }
        return null;
    }

    public boolean reserve(Seat seat) {
        if (isAvailableSeat(seat)) {
            updateSeat(seat, SeatStatus.RESERVED);
            return true;

        }
        return false;
    }

    public boolean book(Seat seat) {
        if (isAvailableSeat(seat)) {
            updateSeat(seat, SeatStatus.BOOKED);
            return true;

        }
        return false;
    }

    private void updateSeat(Seat seat, SeatStatus seatStatus) {
        availableSeats.put(seat, seatStatus);
        seat.setSeatStatus(seatStatus);
    }

    private boolean isAvailableSeat(Seat seat) {
        if (!availableSeats.containsKey(seat)) {
            System.out.println("seat is not found.");
            return false;
        }

        if (!availableSeats.get(seat).equals(SeatStatus.AVAILABLE)) {
            System.out.println("seat is not unavailable.");
            return false;
        }
        return true;
    }

}

@Data
@AllArgsConstructor
class MovieTicket {
    int id;
    TicketType ticketType;
    ShowTime showTime;
    Seat seat;

    public float getPrice() {
        return ticketType.getPrice() + seat.getTotalPrice();
    }
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

@Data
@AllArgsConstructor
class Booking {
    int bookingId;
    int showTimeId;
    List<MovieTicket> tickets;
    BookingStatus bookingStatus;
    Payment payment;

    public float getTotalPrice() {
        float total = 0;
        for (MovieTicket movieTicket : tickets) {

            total += movieTicket.getPrice();
        }
        return total;
    }

    public void completePayment(Payment payment) {
        setPayment(payment);
        setBookingStatus(BookingStatus.PAYMENT_COMPLETED);
        for (MovieTicket movieTicket : tickets) {
            movieTicket.getSeat().setSeatStatus(SeatStatus.BOOKED);
        }
    }
}

enum BookingStatus {
    PENDING, PAYMENT_COMPLETED, CANCELED
}

@Data
@AllArgsConstructor
abstract class Payment {
    float amount;
}


class CashPayment extends Payment {

    public CashPayment(float amount) {
        super(amount);
    }
}

// search
