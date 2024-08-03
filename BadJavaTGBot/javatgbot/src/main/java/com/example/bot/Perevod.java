package com.example.bot;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.db.ReadTable;
import com.example.db.WriteTable;

public class Perevod {
    public Map<String, Double> transferMoney = new HashMap<>();
    private String chatId;
    private Map<String, List<Object>> transfer_profile = new HashMap<>();
    private int flag_money;
    private int count = 2;

    Perevod(String chatId) {
        this.chatId = chatId;
        this.transferMoney.put(chatId, null);
    }

    public boolean provKip(Map<String, String> kip_transfer) throws IOException, GeneralSecurityException {
        List<List<Object>> k = ReadTable.readTable("B3:J2000");
        for (List<Object> i:k) {
            if(i.isEmpty() || i == null) {
                this.count++;
                continue;
            }
            if (i.get(3).equals(kip_transfer.get(this.chatId))) {
                this.transfer_profile.put(this.chatId, i);
                this.count++;
                return true;
            } 
            this.count++;
        }
        return false;
    }

    public boolean provMoney(Map<String, String> valute_out_transfer, Map<String, String> money_transfer, Map<String, List<Object>> Profile) throws IOException, GeneralSecurityException {
        List<Object> k = Profile.get(this.chatId);
        if (valute_out_transfer.get(this.chatId).equals("Рубль")) {
            System.out.println(Double.valueOf(money_transfer.get(this.chatId)));
            System.out.println(Double.valueOf(k.get(6).toString()));
            if (Double.valueOf(money_transfer.get(this.chatId)) <= Double.valueOf(k.get(6).toString())) {
                this.flag_money = 1;
                return true;
            } else {
                return false;
            }
        }
        if (valute_out_transfer.get(this.chatId).equals("Доллар")) {
            if (Long.valueOf(money_transfer.get(this.chatId)) <= Long.valueOf(k.get(7).toString())) {
                this.flag_money = 2;
                return true;
            } else {
                return false;
            }
        }
        if (valute_out_transfer.get(this.chatId).equals("Евро")) {
            if (Long.valueOf(money_transfer.get(this.chatId)) <= Long.valueOf(k.get(8).toString())) {
                this.flag_money = 3;
                return true;
            } else {
                return false;
            }
        }
        if (valute_out_transfer.get(this.chatId).equals("Юань")) {
            if (Long.valueOf(money_transfer.get(this.chatId)) <= Long.valueOf(k.get(9).toString())) {
                this.flag_money = 4;
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public int perevod(Map<String, String> kip_transfer, Map<String, String> valute_out_transfer, Map<String, String> valute_in_transfer, Map<String, String> money_transfer, Map<String, List<Object>> Profile, Map<String, Integer> count) throws NumberFormatException, IOException, GeneralSecurityException {
        String range1, range2;
        Double x;
        Double y;
        if (provKip(kip_transfer)) {
            if (provMoney(valute_out_transfer, money_transfer, Profile)) {
                System.out.println(this.flag_money);
                String j = "";
                int i = 0;
                if (valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
                    j = "G";
                    i = 5;
                } else if (valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
                    j = "H";
                    i = 6;
                } else if (valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
                    j = "I";
                    i = 7;
                } else if (valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
                    j = "J";
                    i = 8;
                }
                switch (this.flag_money) {
                case 1:
                    range1 = j + Integer.toString(this.count);
                    range2 = "G" + count.get(this.chatId);
                    y = Double.valueOf(Profile.get(this.chatId).get(6).toString()) - Double.valueOf(money_transfer.get(this.chatId).toString());
                    x = Double.valueOf(this.transfer_profile.get(this.chatId).get(i).toString()) + getPerValute(valute_in_transfer, Double.valueOf(money_transfer.get(this.chatId)), 1);
                    if (this.transfer_profile.get(this.chatId).get(0).toString().equals(this.chatId) && valute_in_transfer.get(this.chatId).equals(valute_out_transfer.get(this.chatId))) {
                        break;
                    } else {
                        WriteTable.updateTable(range1, Double.toString(Math.round(x * 100.0) / 100.0));
                        WriteTable.updateTable(range2, Double.toString(Math.round(y * 100.0) / 100.0));
                    }
                    break;
                case 2:
                    range1 = j + Integer.toString(this.count);
                    range2 = "H" + count.get(this.chatId);
                    y = Double.valueOf(Profile.get(this.chatId).get(7).toString()) - Double.valueOf(money_transfer.get(this.chatId).toString());
                    x = Double.valueOf(this.transfer_profile.get(this.chatId).get(i).toString()) + getPerValute(valute_in_transfer, Double.valueOf(money_transfer.get(this.chatId)), 2);
                    System.out.println(x);
                    if (this.transfer_profile.get(this.chatId).get(0).toString().equals(this.chatId) && valute_in_transfer.get(this.chatId).equals(valute_out_transfer.get(this.chatId))) {
                        break;
                    } else {
                        WriteTable.updateTable(range1, Double.toString(Math.round(x * 100.0) / 100.0));
                        WriteTable.updateTable(range2, Double.toString(Math.round(y * 100.0) / 100.0));
                    }
                    break;
                case 3:
                    range1 = j + Integer.toString(this.count);
                    range2 = "I" + count.get(this.chatId);
                    y = Double.valueOf(Profile.get(this.chatId).get(8).toString()) - Double.valueOf(money_transfer.get(this.chatId).toString());
                    x = Double.valueOf(this.transfer_profile.get(this.chatId).get(i).toString()) + getPerValute(valute_in_transfer, Double.valueOf(money_transfer.get(this.chatId)), 3);
                    if (this.transfer_profile.get(this.chatId).get(0).toString().equals(this.chatId) && valute_in_transfer.get(this.chatId).equals(valute_out_transfer.get(this.chatId))) {
                        break;
                    } else {
                        WriteTable.updateTable(range1, Double.toString(Math.round(x * 100.0) / 100.0));
                        WriteTable.updateTable(range2, Double.toString(Math.round(y * 100.0) / 100.0));
                    }
                    break;
                case 4:
                    range1 = j + Integer.toString(this.count);
                    range2 = "J" + count.get(this.chatId);
                    y = Double.valueOf(Profile.get(this.chatId).get(9).toString()) - Double.valueOf(money_transfer.get(this.chatId).toString());
                    x = Double.valueOf(this.transfer_profile.get(this.chatId).get(i).toString()) + getPerValute(valute_in_transfer, Double.valueOf(money_transfer.get(this.chatId)), 4);
                    if (this.transfer_profile.get(this.chatId).get(0).toString().equals(this.chatId) && valute_in_transfer.get(this.chatId).equals(valute_out_transfer.get(this.chatId))) {
                        break;
                    } else {
                        WriteTable.updateTable(range1, Double.toString(Math.round(x * 100.0) / 100.0));
                        WriteTable.updateTable(range2, Double.toString(Math.round(y * 100.0) / 100.0));
                    }
                    break;
                }
                return 0;
            } else {
                return 2;
            } 
        } else {
            return 1;
        }
        
    }
    public String getChatIdTransferProfile() {
        return this.transfer_profile.get(this.chatId).get(0).toString();
    }

    private Double getPerValute(Map<String, String> valute_in_transfer, Double x, int k) throws IOException, GeneralSecurityException {
        System.out.println(x);
        List<List<Object>> ls1 = new ArrayList<>();
        List<Object> ls2;
        Double i = 0D;
        switch (k) {
        case 1:
            ls1 = ReadTable.readTable("M3:P3");
            System.out.println(ls1);
            break;
        case 2:
            ls1 = ReadTable.readTable("M4:P4");
            System.out.println(ls1);
            break;
        case 3:
            ls1 = ReadTable.readTable("M5:P5");
            System.out.println(ls1);
            break;
        case 4:
            ls1 = ReadTable.readTable("M6:P6");
            System.out.println(ls1);
            break;
        }

        if (valute_in_transfer.get(this.chatId).toString().equals("Рубль")) {
           ls2 = ls1.get(0);
            System.out.println(ls2);
            System.out.println(ls2.get(0));
            i = Math.round(x * Double.parseDouble(ls2.get(0).toString()) * 100.0) / 100.0;
            System.out.println(i);
        } else if (valute_in_transfer.get(this.chatId).toString().equals("Доллар")) {
            ls2 = ls1.get(0);
            System.out.println(ls2);
            System.out.println(ls2.get(1));
            i = Math.round(x * Double.parseDouble(ls2.get(1).toString()) * 100.0) / 100.0;
            System.out.println(i);
        } else if (valute_in_transfer.get(this.chatId).toString().equals("Евро")) {
            ls2 = ls1.get(0);
            System.out.println(ls2);
            System.out.println(ls2.get(2));
            i = Math.round(x * Double.parseDouble(ls2.get(2).toString()) * 100.0) / 100.0;
            System.out.println(i);
        } else if (valute_in_transfer.get(this.chatId).toString().equals("Юань")) {
            ls2 = ls1.get(0);
            System.out.println(ls2);
            System.out.println(ls2.get(3));
            i = Math.round(x * Double.parseDouble(ls2.get(3).toString()) * 100.0) / 100.0;
            System.out.println(i);
        }
        this.transferMoney.replace(this.chatId, i);         
        return i;
    }
    public String GetTransferName() {
        return this.transfer_profile.get(this.chatId).get(1).toString();
    }
}