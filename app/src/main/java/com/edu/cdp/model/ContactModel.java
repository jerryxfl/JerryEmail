package com.edu.cdp.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.cdp.R;
import com.edu.cdp.bean.Contact;

import java.io.Serializable;
import java.util.List;

public class ContactModel extends ViewModel implements Serializable {
    private MutableLiveData<List<Contact>> contacts;

    public MutableLiveData<List<Contact>> getContacts() {
        if(contacts == null){
            contacts = new MutableLiveData<>();
        }
        return contacts;
    }


    public void addContacts(List<Contact> contact){
        List<Contact> value = getContacts().getValue();
        assert value != null;
        for (int i = 0; i < contact.size(); i++) {
            boolean add = true;
            for (int j = 0; j < value.size(); j++) {
                if(value.get(j).getLocalUser()!=null){
                    if(contact.get(i).getLocalUser().getId()==value.get(j).getLocalUser().getId())add=false;
                }
            }
            if(add)value.add(contact.get(i));
        }
        getContacts().setValue(value);
    }

    public void addContact(Contact contact){
        List<Contact> value = getContacts().getValue();
        assert value != null;
        boolean add = true;
        for (int i = 0; i < value.size(); i++) {
            if(value.get(i).getLocalUser()!=null){
                if(contact.getLocalUser().getId()==value.get(i).getLocalUser().getId())add =false;
            }
        }
        if(add)value.add(contact);
        getContacts().setValue(value);
    }



    public void setOnline(int id,boolean online){
        List<Contact> contacts = getContacts().getValue();
        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getLocalUser()!=null){
                if(contacts.get(i).getLocalUser().getId()==id){
                    contacts.get(i).setOnline(online);
                }
            }
        }
        getContacts().postValue(contacts);
    }


    public void setOnline(boolean online){
        List<Contact> contacts = getContacts().getValue();
        for (int i = 0; i < contacts.size(); i++) {
            if(contacts.get(i).getLocalUser()!=null){
                    contacts.get(i).setOnline(online);
            }
        }
        getContacts().postValue(contacts);
    }
}

