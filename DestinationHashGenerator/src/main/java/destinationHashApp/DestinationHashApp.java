package destinationHashApp;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class DestinationHashApp {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <PRN Number> <Path to JSON File>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replaceAll("\\s+", "");
        String jsonFilePath = args[1];

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JsonElement jsonElement = JsonParser.parseString(jsonContent);

            String destinationValue = findDestinationValue(jsonElement);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String concatenatedValue = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedValue);

            System.out.println(md5Hash + ";" + randomString);
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestinationValue(JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().getAsString();
                } else {
                    String result = findDestinationValue(entry.getValue());
                    if (result != null) {
                        return result;
                    }
                }
            }
        } else if (jsonElement.isJsonArray()) {
            for (JsonElement element : jsonElement.getAsJsonArray()) {
                String result = findDestinationValue(element);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for(int i=0;i<length;i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        
        for(byte b : digest) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
