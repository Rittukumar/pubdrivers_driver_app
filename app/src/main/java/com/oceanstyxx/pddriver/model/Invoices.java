package com.oceanstyxx.pddriver.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by amit on 1/5/16.
 */
public class Invoices {

    /*public InvoiceData[] getInvoices() {
        InvoiceData[] data = new InvoiceData[20];

        for(int i = 0; i < 20; i ++) {
            InvoiceData row = new InvoiceData();
            row.id = (i+1);
            row.invoiceNumber = row.id;
            row.amountDue = BigDecimal.valueOf(20.00 * i);
            row.invoiceAmount = BigDecimal.valueOf(120.00 * (i+1));
            row.invoiceDate = new Date();
            row.customerName =  "Thomas John Beckett";
            row.customerAddress = "1112, Hash Avenue, NYC";

            data[i] = row;
        }
        return data;

    }*/

    public InvoiceData[] getInvoices() {
        InvoiceData[] data = new InvoiceData[2];

        InvoiceData row1 = new InvoiceData();
        row1.chargeDetails = "First hour";
        row1.qty = 1;
        row1.unitRate = 250;
        row1.total = BigDecimal.valueOf(250);
        data[0] = row1;

        InvoiceData row2 = new InvoiceData();
        row2.chargeDetails = "Number of minutes travelled after first hour";
        row2.qty = 60;
        row2.unitRate = 4;
        row2.total = BigDecimal.valueOf(240);
        data[1] = row2;

        return data;

    }
}