package ban;

import ban.services.StationCache;
import ban.services.ClientStorage;
import ban.model.Client;
import ban.model.Data;
import java.util.Arrays;

public class VerificationMain {
    public static void main(String[] args) {
        System.out.println("Starting Verification (Phase 1 Refactoring)...");

        // 1. Test StationCache (which uses BicingClient)
        System.out.println("\n--- Testing StationCache ---");
        StationCache cache = new StationCache();
        try {
            Data data = cache.getStations();
            if (data != null && data.getData() != null) {
                System.out.println("Stations fetched: " + data.getData().getStations().size());
                System.out.println("First station: " + data.getData().getStations().get(0));
            } else {
                System.err.println("Failed to fetch stations.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Test ClientStorage
        System.out.println("\n--- Testing ClientStorage ---");
        ClientStorage clientStorage = new ClientStorage();
        Client c = new Client("+34600123456", "token123", 99999L, Arrays.asList(1, 2, 3));

        System.out.println("Adding client: " + c.getPhoneNumber());
        clientStorage.addClient(c);

        System.out.println("Clients count: " + clientStorage.getAllClients().size());
        if (clientStorage.clientExists(c.getPhoneNumber())) {
            System.out.println("Client successfully verified in storage.");
        }
    }
}
