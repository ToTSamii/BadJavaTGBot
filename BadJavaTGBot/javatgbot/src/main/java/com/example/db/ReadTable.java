package com.example.db;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.services.sheets.v4.model.ValueRange;

public class ReadTable extends MainTable {
    
    public static List<List<Object>> readTable(String range) throws IOException, GeneralSecurityException {
        start();
        ValueRange response = sheetsService.spreadsheets().values().get(tableId, range).execute();
        List<List<Object>> values = response.getValues();

        if (values == null || values.isEmpty()) {
            System.out.println("No data found!");
            return null;
        } else {
            return values;
        }
    }
}
