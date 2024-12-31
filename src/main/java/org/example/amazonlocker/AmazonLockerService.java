package org.example.amazonlocker;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AmazonLockerService {

    List<Locker> lockers;

    LockerAssignmentService assignmentService;

    PassCodeService passCodeService;

    HashMap<NotificationType, NotificationService> notificationServices;

    public AmazonLockerService(HashMap<NotificationType, NotificationService> notificationServices) {
        lockers = new ArrayList<>();
    }

    // call this api when users choose the locker as delivery location.
    public List<Locker> searchLockerByLocation(float longitude, float latitude) {
        // TODO: filter by longitude and latitude.
        return lockers.stream()
                .filter(l -> l.getStatus() == LockerStatus.ACTIVE)
                .collect(Collectors.toList());
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

        NotificationPayload payload = new NotificationPayload(pkg.getCustomerId(), "a message....", LocalDateTime.now());
        notify(payload, NotificationType.SMS);
        notify(payload, NotificationType.EMAIL);

    }

    public void notify(NotificationPayload payload, NotificationType type) {
        NotificationService notificationService  = notificationServices.get(type);
        notificationService.send(payload);
    }

}

class LockerAssignmentService {

    public Compartment assignCompartment(Package pkg, Locker locker) {
        return locker.getCompartments().stream()
                .filter(comp -> comp.getStatus() == CompartmentStatus.AVAILABLE
                        && comp.getSize() == pkg.getCompartmentSize())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available compartments of the lock for this package"));
    }

    /*
    public Compartment assignCompartment(Package pkg, Locker locker) {
        for (Compartment comp : locker.getCompartments()) {
            if (comp.getStatus() == CompartmentStatus.AVAILABLE
                    && comp.getSize().equals(pkg.getCompartmentSize())) {
                return comp;
            }
        }
        throw new RuntimeException("No available compartments of the lock for this package");
    }*/

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
        String code = UUID.randomUUID().toString().substring(0, 6);
        return new PassCode(code, LocalDateTime.now().plusDays(3));
    }
}

interface NotificationService {

    void send(NotificationPayload notificationPayload);
}

class SmsNotificationService implements NotificationService {

    @Override
    public void send(NotificationPayload notificationPayload) {
        // via sms....
    }
}

class EmailNotificationService implements NotificationService {

    @Override
    public void send(NotificationPayload notificationPayload) {
        // via email....
    }
}

class NotificationFactory {

    public static Map<NotificationType ,NotificationService> createNotificationService() {
        Map<NotificationType ,NotificationService> services = new HashMap<>();
        services.put(NotificationType.SMS, new SmsNotificationService());
        services.put(NotificationType.EMAIL, new EmailNotificationService());
        return services;
    }
}

@AllArgsConstructor
@Data
class NotificationPayload {
    int recipientId;
    String message;
    LocalDateTime timestamp;
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

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }
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
