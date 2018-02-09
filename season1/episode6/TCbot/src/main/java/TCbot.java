import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;


public class TCbot extends TelegramLongPollingBot {


    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TCbot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "@GAT_tcbot";
    }

    @Override
    public String getBotToken() {
        return "474226860:AAFttptu1KdIPxUKyycjLhrM2CdCwQoC-dI";
    }


    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();

        String name = msg.getChat().getFirstName();


        if (txt.equals("/start")) {
            sendMsg(msg, "Привет, "+ name + "! Я бот для получения оповещания с TeamCity. Для получения списка команд введи /help");
        }
        if (txt.equals("/help")){
            sendMsg(msg, "/getChatId - Получить chat_id");
        }
        if (txt.equals("/getChatId")){
            sendMsg(msg, "chatId = "+ msg.getChatId());
        }

    }


    /**
     * Send message to user (msg.getChatId)
     * @param msg
     * @param text
     */
    @SuppressWarnings("deprecation")
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            sendMessage(s);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}