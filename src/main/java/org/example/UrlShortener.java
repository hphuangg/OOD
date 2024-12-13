package org.example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UrlShortener {
    private final String domain = "https://tinyurl.com/";
    /*
    * 1,000,000,000
    * */
    //private final AtomicInteger counter = new AtomicInteger(1000000000); // Auto-increment ID
    private long globalCounter = 1000000000L;
    private final HashMap<String, String> shortToLong = new HashMap<>();
    private final HashMap<String, String> longToShort = new HashMap<>();
    private final String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static int FIXED_LENGTH = 7;

    // Convert an integer to Base62
    private String encode(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(characters.charAt((int) (num % 62)));
            num /= 62;
        }

        // Reverse the string to get the proper encoding order
        String encoded = sb.reverse().toString();

        // Pad with leading zeros if the length is less than 7
        if (encoded.length() < FIXED_LENGTH) {
            return "0".repeat(FIXED_LENGTH - encoded.length()) + encoded;
        }

        // Truncate to 7 characters if it's longer
        return encoded.substring(0, FIXED_LENGTH);
    }

//    private String encode(int num) {
//        Base62
//    }

    // Shorten the URL
    public String shortenUrl(String originalUrl, int i) {
        if (longToShort.containsKey(originalUrl)) {
            return domain + longToShort.get(originalUrl); // Return existing short URL
        }
        globalCounter = globalCounter + i;
        long id = globalCounter;
        String shortCode = encode(id);
        longToShort.put(String.valueOf(i), shortCode);
        shortToLong.put(shortCode, String.valueOf(i));
        return domain + shortCode;
    }

    public String shortenUrlWithSalt(String originalUrl, int i) throws NoSuchAlgorithmException {
        if (longToShort.containsKey(originalUrl)) {
            return domain + longToShort.get(originalUrl); // Return existing short URL
        }
        globalCounter = globalCounter + i;
        String salt = "random"; // 固定 Salt
        String originalId = String.valueOf(globalCounter);
        String s = originalId + salt;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));

//        MessageDigest md;
//        md = MessageDigest.getInstance("SHA-1");
//        md.update(text.getBytes());
//        return new BigInteger(1, md.digest()); // use this 1 to tell it is positive.
        BigInteger num = new BigInteger(1, hash);
                //Arrays.hashCode(hash);
        System.out.println("globalCounter: " + globalCounter + ", num: " + num);

        String shortCode = encode(num.longValue());
        longToShort.put(String.valueOf(i), shortCode);
        shortToLong.put(shortCode, String.valueOf(i));
        return domain + shortCode;
    }

    public String shortenUrlWithTimestamp(String originalUrl, int i) {
        if (longToShort.containsKey(originalUrl)) {
            return domain + longToShort.get(originalUrl); // Return existing short URL
        }
        globalCounter = globalCounter + i;
        long timestamp = System.currentTimeMillis();
        String code = timestamp + String.format("%04d", globalCounter);

        long id = Long.parseLong(code);
        String shortCode = encode(id);
        longToShort.put(String.valueOf(i), shortCode);
        shortToLong.put(shortCode, String.valueOf(i));
        return domain + shortCode;
    }

    // Shorten the URL
    public String shortenUrl(String originalUrl, long uuid) {
        if (longToShort.containsKey(originalUrl)) {
            return domain + longToShort.get(originalUrl); // Return existing short URL
        }
        long id = uuid;

        String shortCode = encode(id);
        longToShort.put(String.valueOf(uuid), shortCode);
        shortToLong.put(shortCode, String.valueOf(uuid));
        return domain + shortCode;
    }

    // Retrieve the original URL
    public String getOriginalUrl(String shortUrl) {
        String shortCode = shortUrl.replace(domain, "");
        return shortToLong.getOrDefault(shortCode, "URL not found");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        UrlShortener urlShortener = new UrlShortener();
        String originalUrl = "https://example.com/some-long-path";
        for (int i = 0; i < 100; i++) {
            String shortUrl = urlShortener.shortenUrl(originalUrl, i);
            System.out.println("Shortened URL " + (i) + " : " + shortUrl);
            //System.out.println("Original URL: " + urlShortener.getOriginalUrl(shortUrl));
        }

        System.out.println("-----------------Salt-------------------");
        for (int i = 0; i < 100; i++) {
            String shortUrl = urlShortener.shortenUrlWithSalt(originalUrl, i);
            System.out.println("Shortened URL (Salt) " + (i) + " : " + shortUrl);
            //System.out.println("Original URL: " + urlShortener.getOriginalUrl(shortUrl));
        }

//        System.out.println("-----------------Timestamp-------------------");
//        for (int i = 0; i < 100; i++) {
//            String shortUrl = urlShortener.shortenUrlWithTimestamp(originalUrl, i);
//            System.out.println("Shortened URL (Timestamp) " + (i) + " : " + shortUrl);
//            //System.out.println("Original URL: " + urlShortener.getOriginalUrl(shortUrl));
//        }

        System.out.println("-----------------UUID (ByteByteGo)-------------------");
        long uuid = 2009215674938L;
        String shortUrl = urlShortener.shortenUrl(originalUrl, uuid);
        System.out.println("Shortened URL " + uuid + " : " + shortUrl);

        System.out.println("-----------------UUID (SnowflakeId)-------------------");
        long snowflakeId = 517266442903621639L;
        shortUrl = urlShortener.shortenUrl(originalUrl, snowflakeId);
        System.out.println("Shortened URL " + snowflakeId + " : " + shortUrl);
    }
}

/*
* https://tinyurl.com/yrzh87dv
* https://tinyurl.com/pzpu3bfs
* https://tinyurl.com/yrzh87dv
* */
