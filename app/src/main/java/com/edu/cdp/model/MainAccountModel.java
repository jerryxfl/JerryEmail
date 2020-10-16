package com.edu.cdp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.cdp.bean.Account;
import com.edu.cdp.database.bean.LocalUser;

public class MainAccountModel extends ViewModel {
    private MutableLiveData<Account> user;

    public MutableLiveData<Account> getUser() {
        if(user == null)
            user = new MutableLiveData<Account>();
        return user;
    }


    public void setUserOnline(boolean online) {
        Account account = getUser().getValue();
        assert account != null;
        account.setOnline(online);
        getUser().postValue(account);


    }
}
