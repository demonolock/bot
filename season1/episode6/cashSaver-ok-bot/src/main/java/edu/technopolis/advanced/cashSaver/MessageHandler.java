package edu.technopolis.advanced.cashSaver;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

final class MessageHandler {

    private String token;

    private static String hashAccount = null;
    private static final String botName = "@Cash_Saverbot";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private Map<String, List<Transaction>> accountBase = new HashMap<>();
    private Transaction transaction = new Transaction();
    private static List<Transaction> base = new ArrayList<>();


    private String reTransaction = "^((\\D))+[ ]*[-|+]{1}[1-9](\\d)*$";
    private Pattern tr = Pattern.compile(reTransaction);

    private String getBotUsername() {
        return botName;
    }

    private void getData() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(hashAccount);
        ObjectInputStream oin = new ObjectInputStream(fis);
        accountBase = (Map<String, List<Transaction>>) oin.readObject();
        base = accountBase.get(token);
    }

    void setToken(String chatId, String userId) {
        try {
            token = "token" + String.valueOf(String.valueOf(chatId + getBotUsername()
                    + userId).hashCode());
        } catch (Exception ex){
            log.warn("User don't have some field for hashAccount!" + ex);
        }
    }

    private void getAccount() throws IOException {
        String accountBasePath = "accountBase";
        hashAccount = accountBasePath + File.separator + token +".csa";
        File accFile = new File(hashAccount);
        accFile.createNewFile();
    }

    String onUpdateReceived(String txt, String chatId, String name) {
        try {
            getAccount();
        } catch (IOException e1) {
            log.warn("Problem with create accountBase!" + chatId);
        }
        switch (txt) {

            case "/start": {
                return "Привет, " + name + "! Я - Cashsaver Bot, и я помогу тебе отследить куда пропадает твоя зарплата)\n\n " +
                        "Для получения списка команд напиши мне /help";
            }

            case "/help": {
                return "/whereMoney - Фиксация бюджета,\n" +
                        "/viewAll - Отобразить записанный бюджет за все время,\n" +
                        "/sortByGoal - Сумма трат на цель,\n" +
                        "/howMany - Сколько денег осталось\n" +
                        "В случае возникновения проблем - пишем мне\n" +
                        "(๑˃ᴗ˂)ﻭ" +
                        "-------------> https://ok.ru/profile/571004979798";
            }

            case "/whereMoney": {
                return "Чтобы записать расходы и доходы нужно прислать боту " +
                        "сообщение в виде слова, которое обозначает источник доходов или цель" +
                        " расходов, и числа - обозначающее сумму доходов (положительное число) или расходов(отрицательное число).\n" +
                        " Примеры:\n еда-200\n зарплата+15000\n";
            }

            case "/getToken": {
                return "tocken" + token;
            }

            case "/howMany": {
                log.info("howMany started");
                int sum = 0;
                try {
                    getData();
                } catch (Exception e) {
                    log.error("Невозможно извлечь данные");
                    e.printStackTrace();
                }
                for (Transaction aBase : base) {
                    sum += aBase.getAmount();
                    log.info(aBase.getAmount().toString());
                }
                log.info("howMany finished");
                return "Всего осталось: " + sum;
            }

            case "/viewAll": {
                log.info("viewAll started");
                try {
                    getData();
                } catch (Exception e) {
                    log.error("Невозможно извлечь данные");
                    e.printStackTrace();
                }
                StringBuilder out = new StringBuilder();
                for (Transaction aBase : base) {
                    out.append(aBase.toString()).append("\n");
                }
                log.info("viewAll finished");
                return out.toString();
            }

            case "/delete": {
                log.info("delete started");
                try {
                    log.debug(hashAccount);
                    File accFile = new File(hashAccount);
                    log.debug(String.valueOf(accFile.delete()));
                } catch (Exception e) {
                    log.error("Проблема с удалением файла");
                }
                log.info("delete finished");
                return "Вся информация удалена";
            }

            default: {
                if (txt.matches("/sortByGoal.*")) {
                    log.info("sortByGoal started");
                    if (txt.equals("/sortByGoal"))
                        return "Введи цель чтобы узнать общую потраченную сумму!\n" +
                                "Например /sortByGoal еда";
                    try {
                        getData();
                    } catch (Exception e) {
                        log.error("Невозможно извлечь данные");
                        e.printStackTrace();
                    }
                    int sum = 0;
                    txt = txt.replaceAll("/sortByGoal ", "");
                    for (Transaction aBase : base) {
                        if (aBase.getGoal().equals(txt)) {
                            sum += aBase.getAmount();
                        }
                    }
                    log.info("sortByGoal finished");
                    if (sum < 0) {
                        return "На '" + txt + "' у тебя потрачено " + sum * (-1);
                    } else {
                        return "С помощью '" + txt + "' тобой получено " + sum;
                    }
                }

                else if (tr.matcher(txt).find()) {
                    int posSign,
                        sum,
                        sign = 1;
                    Calendar date = Calendar.getInstance();
                    if (txt.lastIndexOf("+") > -1) {
                        posSign = txt.lastIndexOf("+");
                    } else {
                        posSign = txt.lastIndexOf("-");
                        sign = -1;
                    }

                    try {
                        sum = Integer.valueOf(txt.substring(posSign + 1, txt.length())) * sign;
                    } catch (NumberFormatException nE) {
                        log.warn("bad string!");
                        return "Пожалуйста введи строку в правильном формате\n" +
                                "Нажми для справки -----> /start ";
                    }
                    try {
                        getData();
                    } catch (Exception e) {
                        log.error("Невозможно извлечь данные");
                        e.printStackTrace();
                    }
                    log.info(txt.substring(0, posSign));
                    transaction.setAll(txt.substring(0, posSign).trim(), sum, date);
                    log.info(transaction.toString());

                    base.add(transaction);
                    accountBase.put(token, base);

                    try (FileOutputStream fos = new FileOutputStream(hashAccount)) {
                            ObjectOutputStream serial = new ObjectOutputStream(fos);
                            serial.writeObject(accountBase);
                            serial.flush();
                    } catch (Exception ex) {
                        log.warn("Ошибка при сериализации объекта");
                        return "Ошибка при сериализации объекта";
                    }
                    return "Учтено";
                }
            }
        }

        return "";
    }
}
