package com.hut.reoger.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 24540 on 2017/3/12.
 */

public class myService extends Service {

    private ArrayList<Person> persons;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        persons = new ArrayList<>();
        return iBinder1;
    }

    IBinder iBinder1 = new IDataBase.Stub(){

        @Override
        public List<Person> add(Person person) throws RemoteException {
            persons.add(person);
            return persons;
        }
    };


}
