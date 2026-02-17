package ban.services;

import ban.model.Client;
import java.util.*;

public class ClientStorage {

    // Map: phoneNumber -> Client
    private static Map<String, Client> clients = new HashMap<>();

    /**
     * Añade o actualiza un cliente
     */
    public synchronized void addClient(Client client) {
        clients.put(client.getPhoneNumber(), client);
        System.out.println("[ClientStorage] Client added: " + client.getPhoneNumber());
    }

    /**
     * Obtiene un cliente por teléfono
     */
    public synchronized Client getClient(String phoneNumber) {
        return clients.get(phoneNumber);
    }

    /**
     * Obtiene todos los clientes
     */
    public synchronized List<Client> getAllClients() {
        return new ArrayList<>(clients.values());
    }

    /**
     * Verifica si un cliente existe
     */
    public synchronized boolean clientExists(String phoneNumber) {
        return clients.containsKey(phoneNumber);
    }
}
