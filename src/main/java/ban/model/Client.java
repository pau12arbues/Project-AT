package ban.model;

import java.util.List;

public class Client {
    private String phoneNumber;
    private String telegramToken;
    private long chatId;
    private List<Integer> stationsIds;

    // Constructor vacío
    public Client() {
    }

    // Constructor completo
    public Client(String phoneNumber, String telegramToken,
            long chatId, List<Integer> stationsIds) {
        this.phoneNumber = phoneNumber;
        this.telegramToken = telegramToken;
        this.chatId = chatId;
        this.stationsIds = stationsIds;
    }

    // Getters y setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelegramToken() {
        return telegramToken;
    }

    public void setTelegramToken(String telegramToken) {
        this.telegramToken = telegramToken;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public List<Integer> getStationsIds() {
        return stationsIds;
    }

    public void setStationsIds(List<Integer> stationsIds) {
        this.stationsIds = stationsIds;
    }

    @Override
    public String toString() {
        return "Client{phone=" + phoneNumber +
                ", stations=" + stationsIds + "}";
    }
}
