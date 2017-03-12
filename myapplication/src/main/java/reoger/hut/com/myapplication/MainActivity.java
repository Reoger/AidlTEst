package reoger.hut.com.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.hut.reoger.aidltest.IDataBase;
import com.hut.reoger.aidltest.Person;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {



    private IDataBase iDataBase;


   private ServiceConnection conn = new ServiceConnection() {
       @Override
       public void onServiceConnected(ComponentName name, IBinder service) {
           iDataBase = IDataBase.Stub.asInterface(service);

       }

       @Override
       public void onServiceDisconnected(ComponentName name) {
           iDataBase = null;

       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        iniBindService();
    }

    private void iniBindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.hut.reoger.aidltest","com.hut.reoger.aidltest.myService"));
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }





    public void testTry(View view){
        Person person = new Person("张三",1213);
        try {
            ArrayList<Person> list = (ArrayList<Person>) iDataBase.add(person);
            for (Person per:
                 list) {
                Log.d("TAG",per.toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        iDataBase = null;
    }


}
