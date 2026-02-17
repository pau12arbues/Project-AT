package ban.services;

import ban.model.Data;
import ban.clients.BicingClient;
import java.util.Date;

public class StationCache {

    // Cache estático
    private static Data cachedData = null;
    private static long lastUpdateTime = 0;
    private static final long CACHE_DURATION_MS = 120000; // 120 segundos

    private BicingClient bicingClient;

    public StationCache() {
        this.bicingClient = new BicingClient();
    }

    /**
     * Obtiene estaciones desde caché o actualiza si ha expirado
     */
    public synchronized Data getStations() {
        long currentTime = System.currentTimeMillis();

        // Si no hay datos o el caché ha expirado
        if (cachedData == null || (currentTime - lastUpdateTime) > CACHE_DURATION_MS) {
            System.out.println("[StationCache] Cache expired or empty. Fetching from Bicing API...");
            try {
                Data data = bicingClient.getBicingStations();
                if (data != null && data.getData() != null) {
                    cachedData = data;
                    lastUpdateTime = currentTime;
                    System.out.println("[StationCache] Cache updated at " + new Date(lastUpdateTime));
                }
            } catch (Exception e) {
                System.err.println("[StationCache] Error fetching data: " + e.getMessage());
                // In a real app we might want to throw or return stale data if available
                e.printStackTrace();
            }
        } else {
            System.out.println("[StationCache] Returning cached data");
        }

        return cachedData;
    }
}
