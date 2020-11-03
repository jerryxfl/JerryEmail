package com.edu.cdp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.cdp.application.JApplication;
import com.edu.cdp.database.bean.Email;
import com.edu.cdp.database.bean.Recent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class RecentModel extends ViewModel {
    private MutableLiveData<List<Email>>  recent;

    public MutableLiveData<List<Email>> getRecent() {
        if(recent==null){
            recent = new MutableLiveData<>();
            List<Recent> allRecent = JApplication.getInstance().getDb().RecentDao().getAllRecent2();
            List<Email> emails = new ArrayList<Email>();
            for (Recent r:allRecent) {
                Email email = JApplication.getInstance().getDb().EmailDao().loadEmailById(r.getEmailid(),r.getTag());
                emails.add(email);
            }
            recent.postValue(emails);
        }
        return recent;
    }

    public void add(Email email) {
        List<Email> emailList = getRecent().getValue();
        emailList.add(email);
        getRecent().postValue(emailList);
    }

    public void remove(Email email) {
        List<Email> emailList = getRecent().getValue();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            emailList.removeIf(email1 -> {
                if(email1.getId()==email.getId())return true;
                else return false;
            });
        }else{
            assert emailList != null;
            ListIterator<Email> emailListIterator = emailList.listIterator();
            while(emailListIterator.hasNext()){
                Email e = emailListIterator.next();
                if(e.getId()==email.getId()){
                    emailListIterator.remove();
                }
            }
        }
        getRecent().postValue(emailList);
    }

}
