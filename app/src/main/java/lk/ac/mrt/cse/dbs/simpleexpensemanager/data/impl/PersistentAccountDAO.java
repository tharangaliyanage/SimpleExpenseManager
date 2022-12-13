package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.*;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.DBHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DBHelper dbHelper;

    private static final String TABLE_ACCOUNT = "account";
    private static final String ACCOUNT_NO = "accountno";
    private static final String ACCOUNT_BANKNAME = "bankname";
    private static final String ACCOUNT_HOLDERNAME = "accountHolderName";
    private static final String ACCOUNT_BALANCE = "balance";



    public PersistentAccountDAO(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    @Override
    public List<String> getAccountNumbersList() {
        Cursor result = this.dbHelper.getData(TABLE_ACCOUNT,new String[] {"accountno"}, new String[][] {});
        List<String> accountNumbers = new ArrayList<String>();
        if (result.getCount() != 0) {
            while (result.moveToNext()) {
                accountNumbers.add(result.getString(0));
            }
        }
        result.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor result = this.dbHelper.getData(TABLE_ACCOUNT,new String[] {"*"}, new String[][] {});
        List<Account> accounts = new ArrayList<Account>();
        if (result.getCount() != 0) {
            while (result.moveToNext()) {
                String accountNo = result.getString(0);
                String bankName = result.getString(1);
                String accountHolderName = result.getString(2);
                double balance = result.getDouble(3);
                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accounts.add(account);
            }
        }
        result.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String[] condition = {"accountNo", "=",accountNo};
        Cursor result = this.dbHelper.getData(TABLE_ACCOUNT,new String[] {"*"}, new String[][] {condition});
        if (result.getCount() == 0){
            throw new InvalidAccountException("Invalid account number");
        }
        String accNO = "";
        String bankName = "";
        String accountHolderName = "";
        double balance = 0;
        while (result.moveToNext()) {
            accNO = result.getString(result.getColumnIndex(ACCOUNT_NO));
            bankName = result.getString(result.getColumnIndex(ACCOUNT_BANKNAME));
            accountHolderName = result.getString(result.getColumnIndex(ACCOUNT_HOLDERNAME));
            balance = result.getDouble(result.getColumnIndex(ACCOUNT_BALANCE));
        }

        result.close();
        Account account = new Account(accNO,bankName,accountHolderName,balance);
        return account;
    }

    @Override
    public void addAccount(Account account) {
        ContentValues accountContent = new ContentValues();

        accountContent.put(ACCOUNT_NO, account.getAccountNo());
        accountContent.put(ACCOUNT_BANKNAME, account.getBankName());
        accountContent.put(ACCOUNT_HOLDERNAME, account.getAccountHolderName());
        accountContent.put(ACCOUNT_BALANCE, account.getBalance());

        this.dbHelper.insertData(TABLE_ACCOUNT, accountContent);

    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        int result = this.dbHelper.deleteData("account","accountno",accountNo);
        if (result == 0) {
            throw new InvalidAccountException("Invalid account number");
        }


    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        double balance = 0;
        double total = 0;
        try {
            Account acc = getAccount(accountNo);
            balance = acc.getBalance();
        }
        catch (Exception e) {
            throw new InvalidAccountException("Invalid account number");
        }

        if (expenseType == ExpenseType.EXPENSE) {
            if(balance < amount) {
                throw new InvalidAccountException("Insufficient account balance");
            }
            total = balance-amount;
        }
        else {
            total = amount +balance;
        }
        String[] condition = {"accountno","=",accountNo};
        ContentValues accountContent = new ContentValues();
        accountContent.put(ACCOUNT_BALANCE, total);
        boolean result = this.dbHelper.updateData("account",accountContent,condition);
        if(!result) {
            throw new InvalidAccountException("Invalid account number");
        }
    }


}