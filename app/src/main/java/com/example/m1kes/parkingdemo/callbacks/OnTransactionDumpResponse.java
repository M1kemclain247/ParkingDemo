package com.example.m1kes.parkingdemo.callbacks;


import com.example.m1kes.parkingdemo.models.Transaction;

import java.util.List;

public interface OnTransactionDumpResponse {
    void onDumpComplete();
    void onDumpFailed(List<Transaction> failedTransactions);
}
