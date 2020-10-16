package com.edu.cdp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.cdp.bean.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountModel extends ViewModel {
    private MutableLiveData<List<Account>> accounts;


    public MutableLiveData<List<Account>> getAccounts() {
        if(accounts==null){
            accounts = new MutableLiveData<>();
        }
        return accounts;
    }


    public void updateMegNum(Account account){
        List<Account> accounts = getAccounts().getValue();
        if(accounts==null)return;
        for (int i = 0; i < accounts.size(); i++) {
            if(accounts.get(i).getLocalUser()!=null){
                if(accounts.get(i).getLocalUser().getId()==account.getLocalUser().getId()){
                    accounts.get(i).setEmailNum(account.getEmailNum());
                }
            }
        }
        getAccounts().postValue(accounts);
    }


}
