package com.example.bot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.db.ReadTable;
import com.example.db.WriteTable;


public class Bot extends TelegramLongPollingBot {
    private String chatId;
    private Map<String, String> name = new HashMap<>();
    private Map<String, String> cvv = new HashMap<>();
    private Map<String, String> kip = new HashMap<>();
    private Map<String, String> transferChatId = new HashMap<>();
    private Map<String, String> kip_transfer = new HashMap<>();
    private Map<String, String> valute_out_transfer = new HashMap<>();
    private Map<String, String> valute_in_transfer = new HashMap<>();
    private Map<String, String> money_transfer = new HashMap<>();
    private Map<String, Integer> flagUpdate = new HashMap<>();
    private Map<String, String> r = new HashMap<>();
    private Map<String, String> d = new HashMap<>();
    private Map<String, String> e = new HashMap<>();
    private Map<String, String> u = new HashMap<>();
    private List<String> buttons = new ArrayList<>();
    private Map<String, List<Object>> Profile = new HashMap<>();
    private Map<String, List<String>> msgPerevod = new HashMap<>();
    private Map<String, String> log = new HashMap<>();
    private Map<String, Integer> count = new HashMap<>();
    private Map<String, Integer> k = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handler(update);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        
    }

    public void handler(Update update) throws IOException, GeneralSecurityException {
        SendMessage msg = new SendMessage();
        Perevod p = new Perevod(this.chatId);
        this.chatId = update.getMessage().getChatId().toString();
        msg.setChatId(this.chatId);
        if (!(this.log.containsKey(this.chatId)) || this.log.get(this.chatId).toString().equals("start"))
            this.msgPerevod.put(this.chatId, null);
        String text = update.getMessage().getText();
        this.flagUpdate.put(this.chatId, 0);
        this.r.put(this.chatId, "");
        this.d.put(this.chatId, "");
        this.e.put(this.chatId, "");
        this.u.put(this.chatId, "");
        this.transferChatId.put(this.chatId, null);
        this.k.put(this.chatId, null);
        buttons.clear();
        if (this.log.get(this.chatId) == "not_auth") {
            err(msg);
        } else {
            switch(text) {
            case "/start":
                this.log.put(this.chatId, "start");
                msg.setText("Приветствую, друг! Введи своё имя tg.");
                break;
            case "/kip":
                if (this.log.get(this.chatId) == "not_auth" || this.log.get(this.chatId) == "reauth" || this.log.get(this.chatId) == "start") {
                    err(msg);
                } else {
                    prov(msg);
                    this.kip.put(this.chatId, this.Profile.get(this.chatId).get(4).toString());
                    msg.setText(this.kip.get(this.chatId));
                }
                break;
            case "/transfer":
                if (this.log.get(this.chatId) == "not_auth" || this.log.get(this.chatId) == "reauth" || this.log.get(this.chatId) == "start") {
                    err(msg);
                } else {
                    prov(msg);
                    this.kip_transfer.put(this.chatId, null);
                    this.valute_out_transfer.put(this.chatId, null);
                    this.valute_in_transfer.put(this.chatId, null);
                    this.money_transfer.put(this.chatId, null);
                    msg.setText("Введите kip");
                    this.log.replace(this.chatId, "transfer_kip");
                }
                break;
            case "/check":
                if (this.log.get(this.chatId) == "not_auth" || this.log.get(this.chatId) == "reauth" || this.log.get(this.chatId) == "start") {
                    err(msg);
                } else {
                    prov(msg);
                    checkProfile();
                    this.r.replace(this.chatId, this.Profile.get(this.chatId).get(6).toString());
                    this.d.replace(this.chatId, this.Profile.get(this.chatId).get(7).toString());
                    this.e.replace(this.chatId, this.Profile.get(this.chatId).get(8).toString());
                    this.u.replace(this.chatId, this.Profile.get(this.chatId).get(9).toString());
                    String balanse = "Ваш баланс:\nВ рублях: " + this.r.get(this.chatId) + "\nВ долларах: " + this.d.get(this.chatId) + "\nВ евро: " + this.e.get(this.chatId) + "\nВ юанях: " + this.u.get(this.chatId);
                    msg.setText(balanse);
                }
                break;
            default:
                if (this.log.get(this.chatId) == "start") {
                    this.log.replace(this.chatId, "name");
                    this.name.put(this.chatId, text);
                    String a = "Рад знакомству, " + this.name.get(this.chatId) + "!\nВведи свой cvv.";
                    msg.setText(a);
                } else if (this.log.get(this.chatId) == "name") {
                    this.log.replace(this.chatId, "cvv");
                    this.cvv.put(this.chatId, text);
                    if (checkProfile()) {
                        msg.setText("Аунтефикация прошла успешно! Вот что я могу:\n /kip - получить кип\n /transfer - сделать перевод на другой счёт\n/check - баланс");
                        buttons.add("/kip");
                        buttons.add("/transfer");
                        buttons.add("/check");
                        msg.setReplyMarkup(Keyboard.CreateKeyboard(buttons));
                        this.log.replace(this.chatId, "auth");
                    } else {
                        this.log.replace(this.chatId, "not_auth");
                        err(msg);
                    } 
                } else if (this.log.get(this.chatId) == "transfer_kip") {
                    this.kip_transfer.replace(this.chatId, text);
                    this.log.replace(this.chatId, "get_kip");
                    msg.setText("Введите валюту, из которой переводить (Рубль, Доллар, Евро, Юань)");
                } else if (this.log.get(this.chatId) == "get_kip") {
                    this.valute_out_transfer.replace(this.chatId, text);
                    this.log.replace(this.chatId, "get_valute_transfer");
                    msg.setText("Введите валюту, в которую переводить (Рубль, Доллар, Евро, Юань)");
                } else if (this.log.get(this.chatId) == "get_valute_transfer") {
                    this.valute_in_transfer.replace(this.chatId, text);
                    this.log.replace(this.chatId, "get_valute");
                    msg.setText("Введите сумму");
                } else if (this.log.get(this.chatId) == "get_valute") {
                    this.money_transfer.replace(this.chatId, text);
                    this.log.replace(this.chatId, "get_money");
                    this.k.replace(this.chatId, 3);
                    try {
                        this.k.replace(this.chatId,p.perevod(this.kip_transfer, this.valute_out_transfer, this.valute_in_transfer, this.money_transfer, this.Profile, this.count));
                    } catch (Exception e) {
                       this.k.replace(this.chatId, 3);
                    }
                        if (this.k.get(this.chatId).toString().equals("0")) {
                        msg.setText("Перевод успешно выполнен! Введите команду /check, чтобы узнать баланс.");
                        try {
                            this.execute(msg);
                        } catch (TelegramApiException e1) {
                            e1.printStackTrace();
                        }
                        String i = valuteMessage();
                        String c = "Перевод от " + this.name.get(this.chatId) + " в размере " + this.money_transfer.get(this.chatId).toString() + i + p.transferMoney.get(this.chatId).toString() + ")";
                        logger(p.GetTransferName(), this.valute_out_transfer.get(this.chatId).toString(), this.money_transfer.get(this.chatId).toString(), this.valute_in_transfer.get(this.chatId).toString(), p.transferMoney.get(this.chatId).toString());
                        this.transferChatId.replace(this.chatId, p.getChatIdTransferProfile());
                        if (this.transferChatId.get(this.chatId).toString().equals("0") || this.transferChatId.get(this.chatId).toString().isEmpty()) {
                            msg.setChatId(this.chatId);
                            msg.setText("Сообщение не отправлено, у пользователя не верный id!");
                        } else {
                            msg.setChatId(this.transferChatId.get(this.chatId).toString());
                            msg.setText(c);
                        }
                        this.flagUpdate.replace(this.chatId, 1);
                        checkProfile();
                        this.log.replace(this.chatId, "perevod");
                    } else if (this.k.get(this.chatId).toString().equals("1")) {
                        msg.setText("Перевод не выполнен, данного kip не найдено!");
                        this.log.replace(this.chatId, "err_perevod");
                    } else if (this.k.get(this.chatId).toString().equals("2")) {
                        msg.setText("Перевод не выполнен, недостаточно средств!");
                        this.log.replace(this.chatId, "err_perevod");
                    } else if (this.k.get(this.chatId).toString().equals("3")) {
                        msg.setText("Перевод не выполнен!");
                        this.log.replace(this.chatId, "err_perevod");
                    }
                } else { 
                    if (this.log.get(this.chatId) == "not_auth" || this.log.get(this.chatId) == "reauth" || this.log.get(this.chatId) == "start") {
                        err(msg);
                    } else {
                        msg.setText("Вот что я могу:\n /kip - получить кип\n /transfer - сделать перевод на другой счёт\n/check - баланс");
                        buttons.add("/kip");
                        buttons.add("/transfer");
                        buttons.add("/check");
                        msg.setReplyMarkup(Keyboard.CreateKeyboard(buttons));
                    }
                }
            }
        }

        try {
            this.execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotToken() {
        return "";
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    public boolean checkProfile() throws IOException, GeneralSecurityException {
        List<List<Object>> lsProf = ReadTable.readTable("A3:J2000");
        this.count.put(this.chatId, 2);
        for (List<Object> i:lsProf) {
            if(i.isEmpty() || i == null) {
                this.count.replace(this.chatId, Integer.valueOf(this.count.get(this.chatId).toString()) + 1);
                continue;
            }
            if (i.get(0).equals(this.cvv.get(this.chatId)) & i.get(2).equals(this.name.get(this.chatId))) {
                if (Integer.valueOf(this.flagUpdate.get(this.chatId).toString()).equals(0)) {
                    this.Profile.put(this.chatId, i);
                } else {
                    this.Profile.replace(this.chatId, i);
                }
                this.count.replace(this.chatId, Integer.valueOf(this.count.get(this.chatId).toString()) + 1);
                WriteTable.updateTable("B" + this.count.get(this.chatId), this.chatId);
                return true;
            }
            this.count.replace(this.chatId, Integer.valueOf(this.count.get(this.chatId).toString()) + 1);
        }
        return false; 
    }

    public void prov(SendMessage msg) {
        if (this.name.get(this.chatId) == null & this.cvv.get(this.chatId) == null) {
            msg.setText("Неверный cvv или имя!");
        }
    }

    public void err(SendMessage msg) {
        msg.setText("Аунтефикация не прошла! Попробуй ещё раз, введя команду /start");
        this.log.replace(this.chatId, "reauth");
    }

    public void logger(String name, String valute_out, String money_out, String valute_in, String money_in) throws IOException, GeneralSecurityException {
        String[] ls = new String[6];
        ls[0] = this.name.get(this.chatId).toString();
        ls[1] = name;
        ls[2] = valute_out;
        ls[3] = money_out;
        ls[4] = valute_in;
        ls[5] = money_in;
        WriteTable.appendTable("Logi!A2", ls);
    }

    private String valuteMessage() {
        String i = "";
        if (this.valute_out_transfer.get(this.chatId).toString().equals("Рубль")) {
            if (this.valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
                i = " рублей в рубли (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
                i = " рублей в доллары (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
                i = " рублей в евро (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
                i = " рублей в юани (";
            } 
        } else if (this.valute_out_transfer.get(this.chatId).toString().equals("Доллар")) {
           if (this.valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
                i = " долларов в рубли (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
                i = " долларов в доллары (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
                i = " долларов в евро (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
                i = " долларов в юани (";
            } 
        } else if ((this.valute_out_transfer.get(this.chatId).toString().equals("Евро"))) {
           if (this.valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
                i = " евро в рубли (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
                i = " евро в доллары (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
                i = " евро в евро (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
                i = " евро в юани (";
            } 
        } else if ((this.valute_out_transfer.get(this.chatId).toString().equals("Юань"))) {
            if (this.valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
                i = " юань в рубли (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
                i = " юань в доллары (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
                i = " юань в евро (";
            } else if (this.valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
                i = " юань в юани (";
            } 
        }
        return i;
    }
}
