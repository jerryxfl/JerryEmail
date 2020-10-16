package com.edu.cdp.globe;

import com.edu.cdp.bean.Account;

public class MainAccount {
    private Account account;

    private static MainAccount instance;
    public static synchronized MainAccount getInstance() {
        if(instance == null){
            instance = new MainAccount();
        }
        return instance;
    }



    public void setMainAccount(Account account){
        this.account = account;
    }


    public Account getMainAccount() {
        return account;
    }

}
