package org.example.amazonlocker;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class AmazonLockerService {

    List<Locker> lockers;

    LockerAssignmentService assignmentService;

    PassCodeService passCodeService;

    SmsNotificationService smsNotificationService;

    EmailNotificationService emailNotificationService;

    HashMap<NotificationType, NotificationService> notificationServices;

    // call this api when users choose the locker as delivery location.
    public List<Locker> searchLockerByLocation(float log, float lat) {
        return lockers;
        // return lockers; filter by long, lat and status.
    }

    // call this api after users select a locker or after checkout?
    public Compartment assignCompartment(Package pkg, Locker locker) {
        Compartment compartment = assignmentService.assignCompartment(pkg, locker);
        compartment.reserve();
        return compartment;
    }

    // call this api when the deliverer ship the package to the locker.
    public void storePackageToCompartment(Package pkg, Compartment compartment) {
        compartment.storePackage(pkg);

        pkg.setPickupCode(passCodeService.generateCode(pkg));

        notify(compartment, NotificationType.SMS);
        notify(compartment, NotificationType.EMAIL);

    }

    public void notify(Compartment compartment, NotificationType type) {
        NotificationService notificationService  = notificationServices.get(type);
        notificationService.send(compartment);
    }

}

class LockerAssignmentService {

    public Compartment assignCompartment(Package pkg, Locker locker) {
        for (Compartment comp : locker.getCompartments()) {
            if (comp.getStatus() == CompartmentStatus.AVAILABLE
                    && comp.getSize().equals(pkg.getCompartmentSize())) {
                return comp;
            }
        }
        throw new RuntimeException("No available compartments of the lock for this package");
    }

}

class LockerAccessService {
    public boolean validatePickupCode(String pickupCode, Package pkg) {
        return pkg.getPickupCode().getCode().equals(pickupCode);
    }

    public void unlockCompartment(Compartment compartment) {
        // unlock
    }
}

class PassCodeService {

    public PassCode generateCode(Package pkg) {
        PassCode code = new PassCode(String.valueOf(pkg.getCustomerId()), LocalDateTime.now().plusDays(3));
        return code;
    }
}

interface NotificationService {

    void send(Compartment compartment);
}

class SmsNotificationService implements NotificationService {

    @Override
    public void send(Compartment compartment) {
        // via sms....
    }
}

class EmailNotificationService implements NotificationService {

    @Override
    public void send(Compartment compartment) {
        // via email....
    }
}

@Data
class Notification {
    Compartment compartment;

}

enum NotificationType {
    SMS, EMAIL
}

enum CompartmentSize {
    XS, S, M, L, XL, XXL
}

enum CompartmentStatus {
    AVAILABLE, RESERVED, OCCUPIED, CLEANING
}

@Data
class Compartment {
    int id;
    CompartmentSize size;
    CompartmentStatus status;
    Package storedPkg;

    public void reserve() {
        setStatus(CompartmentStatus.RESERVED);
    }

    public void storePackage(Package pkg) {
        setStoredPkg(pkg);
        setStatus(CompartmentStatus.OCCUPIED);
    }
}


enum LockerStatus {
    ACTIVE, INACTIVE, MAINTENANCE
}

@Data
class Locker {
    int id;
    String name;
    List<Compartment> compartments;
    LockerStatus status;
    Address address;
    // long, lat, open hours....
}

@AllArgsConstructor
@Data
class PassCode { // TODO: to reuse when refund.
    String code;
    LocalDateTime expiredAt;
}

enum PackageStatus {
    IN_TRANSIT, DELIVERED, PICKED_UP, EXPIRED
}

@Data
class Package {
    int id;
    int customerId;
    CompartmentSize compartmentSize;
    PackageStatus status;
    PackageSize dimension;

//    CompartmentCode storeCode; // 寄件
    PassCode pickupCode;
    float weight;

    // ... metadata
}

class PackageSize {
    float width;
    float length;
    float height;
}

class Customer {
    int id;
    int name;
    String phoneNumber;
    String email;
    String address;
}

class Address {
    String zipcode;
    String fullAddress;
}
