package com.example.db;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class WriteTable extends MainTable {

    private static ValueRange body = new ValueRange();

    public static void updateTable(String range, String x) throws IOException, GeneralSecurityException {
        start();
        body.setValues(Arrays.asList(Arrays.asList(x)));
        UpdateValuesResponse result = sheetsService.spreadsheets().values()
            .update(tableId, range, body)
            .setValueInputOption("RAW")
            .execute();

        if (result.isEmpty() || result == null) {
            System.out.println("updateTable is failed!");
        }
    }

    public static void appendTable(String range, String[] ls) throws IOException, GeneralSecurityException {
        start();
        body.setValues(Arrays.asList(Arrays.asList(ls)));
        AppendValuesResponse response = sheetsService.spreadsheets().values()
            .append(tableId, range, body)
            .setValueInputOption("USER_ENTERED")
            .setInsertDataOption("INSERT_ROWS")
            .setIncludeValuesInResponse(true)
            .execute();
            
        if (response.isEmpty() || response == null) {
            System.out.println("updateTable is failed!");
        }
    }
}

