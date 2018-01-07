import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class CashSaverBot extends TelegramLongPollingBot {

    static Calendar c = Calendar.getInstance();
    static int year = c.get(c.YEAR);
    static int month = c.get(c.MONTH)+1;
    static int day = c.get(c.DAY_OF_MONTH);
    static int hour = c.get(c.HOUR);
    static int min = c.get(c.MINUTE);
    static int sec = c.get(c.SECOND);

    static String hashAccount = null;

    private static Logger log = Logger.getLogger(CashSaverBot.class.getName());
    private static String logFileName = year+"_"+month+"_"+day+"_cashSaverBot.LOG";
    private static String logFilePath = "LOGs";
    private static String accountBasePath = "accountBase";

    private static Map<String, List<Transaction>> accountBase = new HashMap<>();
    private static Transaction transaction = new Transaction();
    private static List<Transaction> base = new ArrayList<>();


    String reTransaction = "^((\\D))+[ ]*[-|+]{1}[1-9](\\d)*$";
    String tocken = "^[tocken][-]*(\\d)+$";
    String word = "^[(\\D)+]$";
    String digit = "^[(\\d)+]$";

    Pattern tr = Pattern.compile(reTransaction);
    Pattern tck = Pattern.compile(tocken);
    Pattern wd = Pattern.compile(word);
    Pattern dg = Pattern.compile(digit);

    Boolean sortByGoal = false;


    public static void main(String[] args) throws IOException {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        File logFile = new File(logFilePath+ File.separator + logFileName);
        logFile.createNewFile();
        PrintWriter pw = new PrintWriter(logFilePath+ File.separator + logFileName);

        try {
            botapi.registerBot(new CashSaverBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } finally {
            pw.print(log);
            pw.close();
        }
    }

    @Override
    public String getBotUsername() {
        return "@Cash_Saverbot";
    }

    @Override
    public void onUpdateReceived(Update e) {
        Message msg = e.getMessage();
        String txt = msg.getText();

        try {
            System.out.println(createAccount(msg));
        } catch (IOException e1) {
            log.warning(hour+":"+min+":"+sec+"Problem with create accountBase!" +msg.getChatId());
        }
        String name = msg.getChat().getFirstName();

        if ((sortByGoal == true)){
            sortByGoal = false;
            base = accountBase.get(getToken(msg));
            int sum = 0;
            for (int i = 0; i < base.size(); i++) {
                if (base.get(i).getGoal().equals(txt)){
                    sum+=base.get(i).getAmount();
                }
            }
            if (sum<0) {
                sendMsg(msg, "На " + txt + " у тебя потрачено " +sum*(-1));
            } else {
                sendMsg(msg, "С помощью "+ txt+" тобой получено "+sum);
            }
            return;
        }

        if (txt.equals("/start")) {
            sendMsg(msg, "Привет, "+ name + "! Я - Cashsaver Bot, и я помогу тебе отследить куда пропадает твоя зарплата)\n\n " +
                    "Для получения списка команд напиши мне /help");
        }
        if (txt.equals("/help")){
            sendMsg(msg, "/whereMoney - Фиксация бюджета,\n" +
                    "/viewAll - Отобразить записанный бюджет за все время,\n"+
                    "/sortByGoal - Сумма трат на цель,\n"+
                    "/howMany - Сколько денег осталось\n"+
                    "В случае возникновения проблем - пишем мне\n" +
                    "(๑˃ᴗ˂)ﻭ" +
                    "--------------------------> @kak666tus");
        }

        if (txt.equals("/whereMoney")){
            sendMsg(msg, "Чтобы записать расходы и доходы нужно прислать боту " +
                    "сообщение в виде слова, которое обозначает источник доходов или цель" +
                    "расходов, и числа - обозначающее сумму доходов (положительное число) или расходов(отрицательное число).\n" +
                    "Примеры:\n еда-200\n зарплата+15000\n");

        }
        if (txt.equals("/getToken")){
            sendMsg(msg, "tocken"+ getToken(msg));
        }
        if (txt.equals("/howMany")){
            int sum = 0;
            base = accountBase.get(getToken(msg));
            for (int i = 0; i < base.size(); i++) {
                sum+=base.get(i).getAmount();
            }
            sendMsg(msg, "Всего осталось: "+sum);
        }
        if (tr.matcher(txt).find()){
            int i;
            int a = 0;
            int sign = 1;
            Calendar c = Calendar.getInstance();
            if (txt.lastIndexOf("+")> -1){
                i = txt.lastIndexOf("+");
            }
            else {
                i = txt.lastIndexOf("-");
                sign = -1;
            }

            try{
               a = Integer.valueOf(txt.substring(i+1, txt.length()))*sign;
            }
            catch (NumberFormatException nE){
                log.warning("bad string!");
                sendMsg(msg, "Пожалуйста введи строку в правильном формате\n" +
                        "Нажми для справки -----> /whereMoney ");
            }
            System.out.println(txt.substring(0, i));
            transaction.setAll(txt.substring(0, i).trim(), a, c);
            System.out.println(transaction);

            base.add(transaction);
            accountBase.put(getToken(msg), base);

            try {
                FileOutputStream fos = new FileOutputStream(accountBasePath+ File.separator + hashAccount+".csa");
                ObjectOutputStream serial = new ObjectOutputStream(fos);
                serial.writeObject(accountBase);
                serial.flush();
                serial.close();
            } catch (Exception ex) {
                log.warning("Ошибка при сериализации объекта");
            }
        }

        if (txt.equals("/viewAll")){
            try {
                System.out.println(accountBasePath+ File.separator + hashAccount+".csa");
                FileInputStream fis = new FileInputStream(accountBasePath+ File.separator + hashAccount+".csa");
                ObjectInputStream oin = new ObjectInputStream(fis);
                accountBase = (Map<String, List<Transaction>>) oin.readObject();
                base = accountBase.get(getToken(msg));
                for (Transaction aBase : base) {
                    sendMsg(msg, aBase.toString());
                }
            } catch (Exception e1) {
                log.warning("Проблемы с file /viewAll");
                log.warning(e1.toString());
            }
        }

        if (txt.equals("/sortByGoal")){
            sendMsg(msg, "Введи цель чтобы узнать общую потраченную сумму!");
            sortByGoal = true;
        }
    }

    @Override
    public String getBotToken() {
        return "469411687:AAEv2HBwsk8aHZ6zuPWJsi3XpsADmyuX7EA";
    }

    public String createAccount(Message msg) throws IOException {
        try {
            hashAccount = getToken(msg);
        } catch (Exception ex){
            log.warning(hour+":"+min+":"+sec+" - User don't have some field for hashAccount!" + ex);
        }

        File accFile = new File(accountBasePath+ File.separator + hashAccount+".csa");
        accFile.createNewFile();
        return hashAccount;
    }

    /**
     * @param msg
     * @return token of this. cash
     */
    private String getToken(Message msg) {
        return "token"+String.valueOf(String.valueOf(msg.getChatId() + getBotUsername()
                + msg.getChat().getId()).hashCode());
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
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

}
