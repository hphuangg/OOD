package org.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class SnowflakeIdGenerator {
    private final long epoch = 1609459200000L; // Custom epoch: Jan 1, 2021
    private final long machineId;
    private final int machineIdBits = 10;
    private final int sequenceBits = 12;
    private final long maxSequence = ~(-1L << sequenceBits);
    private final AtomicInteger sequence = new AtomicInteger(0);
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId >= (1 << machineIdBits)) {
            throw new IllegalArgumentException("Invalid Machine ID");
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (currentTimestamp == lastTimestamp) {
            int seq = sequence.incrementAndGet();
            if (seq > maxSequence) {
                // Wait for the next millisecond
                while ((currentTimestamp = System.currentTimeMillis()) <= lastTimestamp) {}
                sequence.set(0);
            }
        } else {
            sequence.set(0);
        }

        lastTimestamp = currentTimestamp;
        return ((currentTimestamp - epoch) << (machineIdBits + sequenceBits))
                | (machineId << sequenceBits)
                | sequence.get();
    }

    public static void main(String[] args) {
        int dataCenterId = 1; // Example data center ID
        int serverId = 1; // Example server ID

        SnowflakeIdGenerator snowflakeIdGenerator = new SnowflakeIdGenerator(serverId);


        Set<Long> set = new HashSet();
        long start = System.currentTimeMillis();

        // Generate 10,000 unique IDs per second
        for (int i = 0; i < 10000; i++) {
            Runnable runnable = () -> {
                long uniqueId = snowflakeIdGenerator.nextId();
                if (set.contains(uniqueId)) {
                    System.out.println("duplicate found");
                } else {
                    set.add(uniqueId);
                }
                System.out.println("Generated Unique ID: " + uniqueId);
            };
            new Thread(runnable).start();

        }
        System.out.println("time take in milliseconds: " + (System.currentTimeMillis() - start));
    }
}
