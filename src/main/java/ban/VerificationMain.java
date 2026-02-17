package ban;

import ban.clients.BicingClient;
import ban.model.Client;
import ban.model.Data;
import java.util.Arrays;

public class VerificationMain {
    public static void main(String[] args) {
        System.out.println("Starting Verification (Simplified Architecture)...");

        // 1. Test BicingClient directly (Connectivity Check)
        System.out.println("\n--- Testing BicingClient (Connectivity) ---");
        BicingClient client = new BicingClient();
        try {
            Data data = client.getBicingStations();
            if (data != null && data.getData() != null) {
                System.out.println("Stations fetched from API: " + data.getData().getStations().size());
                if (!data.getData().getStations().isEmpty()) {
                    System.out.println("First station sample: " + data.getData().getStations().get(0));
                }
            } else {
                System.err.println("Failed to fetch stations from API.");
            }
        } catch (Exception e) {
            System.err.println("Error connecting to Bicing: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Test Client Model (Sanity Check)
        System.out.println("\n--- Testing Client Model (Sanity) ---");
        Client c = new Client("+34600123456", "token123", 99999L, Arrays.asList(1, 2, 3));
        System.out.println("Client created: " + c);
    }
}
