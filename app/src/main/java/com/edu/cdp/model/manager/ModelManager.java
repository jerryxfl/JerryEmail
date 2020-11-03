package com.edu.cdp.model.manager;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.edu.cdp.application.JApplication;
import com.edu.cdp.bean.Account;
import com.edu.cdp.bean.Contact;
import com.edu.cdp.database.AppDataBase;
import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.LocalUser;
import com.edu.cdp.database.bean.Recent;
import com.edu.cdp.model.AccountModel;
import com.edu.cdp.model.ContactModel;
import com.edu.cdp.model.EmailModel;
import com.edu.cdp.model.MainAccountModel;
import com.edu.cdp.model.RecentModel;

import java.util.ArrayList;
import java.util.List;

public class ModelManager {
    private AppDataBase db;

    private static ModelManager manager;
    private ContactModel contactModel;
    private EmailModel emailModel;
    private AccountModel accountModel;
    private MainAccountModel mainAccountModel;
    private RecentModel recentModel;

    public static synchronized ModelManager getManager() {
        if (manager == null) {
            manager = new ModelManager();
        }
        return manager;
    }


    public void initModel(ViewModelStoreOwner owner, AppDataBase db) {
        this.db = db;
        contactModel = new ViewModelProvider(owner).get(ContactModel.class);
        emailModel = new ViewModelProvider(owner).get(EmailModel.class);
        accountModel = new ViewModelProvider(owner).get(AccountModel.class);
        mainAccountModel = new ViewModelProvider(owner).get(MainAccountModel.class);
        recentModel = new ViewModelProvider(owner).get(RecentModel.class);


        //accountModel
        initAccountModel(db);
        initContactsModel();

    }

    public void initContactsModel() {
        List<Contact> contacts = new ArrayList<Contact>();
        contacts.add(new Contact(null,false));
        contactModel.getContacts().setValue(contacts);
    }


    //初始化账号数据
    private void initAccountModel(AppDataBase db) {
        List<Account> accounts = new ArrayList<Account>();
        List<LocalUser> allLocalUser = db.userDao().getAllUser();
        System.out.println("数据库大小："+ allLocalUser.size());


        for (LocalUser u : allLocalUser) {
            if(u.isMainAccount()){
                getMainAccountModel().getUser().setValue(new Account(u,0,false));
            }
            accounts.add(new Account(u, 0,false));
        }
        accounts.add(new Account(null, 0,false));
        accountModel.getAccounts().setValue(accounts);
    }


    public void refreshAccountModel() {
        List<Account> accounts = new ArrayList<Account>();
        List<LocalUser> allLocalUser = db.userDao().getAllUser();
        for (LocalUser u : allLocalUser) {
            if(u.isMainAccount()){
                getMainAccountModel().getUser().setValue(new Account(u, 0,false));
            }
            accounts.add(new Account(u, 0,false));
        }


        accounts.add(new Account(null, 0,false));
        accountModel.getAccounts().postValue(accounts);
    }


    //更新最近的数据
    public void updateRecent(Recent recent){
        db.RecentDao().insertOneRecent(recent);
        List<Recent> allRecent1 = db.RecentDao().getAllRecent1();
        if(allRecent1.size()>5){
            Email email = db.EmailDao().loadEmailById(allRecent1.get(0).getEmailid(), allRecent1.get(0).getTag());
            recentModel.remove(email);
            db.RecentDao().deleteRecent(allRecent1.get(0));
        }

        Email email = db.EmailDao().loadEmailById(recent.getEmailid(), recent.getTag());
        recentModel.add(email);
    }


    public AccountModel getAccountModel() {
        return accountModel;
    }

    public ContactModel getContactModel() {
        return contactModel;
    }

    public EmailModel getEmailModel() {
        return emailModel;
    }

    public MainAccountModel getMainAccountModel() {
        return mainAccountModel;
    }

    public RecentModel getRecentModel() {
        return recentModel;
    }
}
