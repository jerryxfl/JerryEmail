package com.edu.cdp.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.cdp.bean.Email;

import java.util.List;

public class EmailModel extends ViewModel {
    private MutableLiveData<List<Email>> emails;

    public MutableLiveData<List<Email>> getEmails() {
        if(emails==null){
            emails = new MutableLiveData<List<Email>>();
        }
        return emails;
    }
}
