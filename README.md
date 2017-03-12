AIDL（Android 接口定义语言）与我们可能使用过的其他 IDL 类似。 我们可以利用它定义客户端与服务使用进程间通信 (IPC) 进行相互通信时都认可的编程接口。 在 Android 上，一个进程通常无法访问另一个进程的内存。 尽管如此，进程需要将其对象分解成操作系统能够识别的原语，并将对象编组成跨越边界的对象。 编写执行这一编组操作的代码是一项繁琐的工作，因此 Android 会使用 AIDL 来处理。

>**注**：只有允许不同应用的客户端用 IPC 方式访问服务，并且想要在服务中处理多线程时，才有必要使用 AIDL。 如果我们不需要执行跨越不同应用的并发 IPC，就应该通过[实现一个 Binder](https://developer.android.google.cn/guide/components/bound-services.html#Binder) 创建接口；或者，如果您想执行 IPC，但根本不**需要处理多线程，则[使用 Messenger 类](https://developer.android.google.cn/guide/components/bound-services.html#Messenger)来实现接口。无论如何，在实现 AIDL 之前，我们需要先理解[绑定服务](https://developer.android.google.cn/guide/components/bound-services.html)。

更多关于AIDL接口的介绍，请参考官方文档<https://developer.android.google.cn/guide/components/aidl.html>
下面直接来介绍我们怎么使用他。

# 定义一个AIDL
  首先我们得明白AIDL究竟是做什么用的，简单的讲，我们可以通过AIDL进行不同进程之间的通信。现在，就以一个进程来实现计算，另一个线程来进行调用，并显示结果来进行演示AIDL的使用。

## 服务端的实现。
新建一个项目，在app的目录下新建一个aidl的文件目录，然后在aidl文件中新建一个aidl的文件。

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-d0bf1c43b7069914.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

新建的aidl文件如下：
```
// IMyAidlInterface.aidl
package com.hut.reoger.aidltest;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

     int add(int num1,int num2);
}
```
我们通过add方法来实现a+b，通过远程调用即可返回a+b的值。
项目结构如图：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-c4a93174ea82bf53.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
在android studio中新建中的adil文件不会自动生成，需要我们手动编译，编译的方法也很简单，只需要点击sync按钮即可。形如：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-10dc2e8d5374b63a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


接下来在java文件下新建一个服务MyService.java，代码如下：
```
package com.hut.reoger.aidltest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by 24540 on 2017/3/12.
 */

public class myService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iBinder;
    }

    IBinder iBinder = new IMyAidlInterface.Stub(){

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
        }

        @Override
        public int add(int num1, int num2) throws RemoteException {
            Log.d("TAG","num1="+num1+" num2="+num2);
            return num1+num2;
        }
    };

}

```

可以发现，我们在服务类里面实现了add的方法，并且将他返回给了IBinder对象。
最后，我们需要在manifests文件中声明：
```
 <service android:name=".myService"
            android:process=":remote"
            android:exported="true"/>
```
process表示当前的服务运行在单独的线程中，exported表示该服务能够被其他组件调用或跟他交互。

##  客户端实现
首先我们需要将其在服务端写好的aidl代码copy到客户端，注意文件目录也需要相同。比较简单的做法:
1. 新建一个aidl文件夹
2.从服务端copy一份aidl的代码到aidl文件夹中
3.新建一个包，报名就是aidl文件中的package内容。如图：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-f37f11467da82d4a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

4.将aidl文件移动到新建的包下，直接拖拽就可以了。

然后我们就需要来编写我们的调用程序了。首先是我们的界面xml文件。
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context="reoger.hut.com.myapplication.MainActivity">

    <EditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="num1"
        android:id="@+id/num1"/>
    <EditText

        android:hint="num2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/num2"/>
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/num3"/>
    <Button
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="测试"
        android:onClick="testTry"/>  
</LinearLayout>

```

接下来就是重点，我们的需要在远程进行调用。代码如下：
```
public class MainActivity extends AppCompatActivity {

    private EditText num1;
    private EditText num2;
    private TextView num3;

    private IMyAidlInterface iMyAidlInterface;//定义的aidl文件


   private ServiceConnection conn = new ServiceConnection() {
       @Override
       public void onServiceConnected(ComponentName name, IBinder service) {
           iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);

       }

       @Override
       public void onServiceDisconnected(ComponentName name) {
           Log.d("TAG","连接断开");
           iMyAidlInterface = null;

       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        iniBindService();//启动服务
    }

    private void iniBindService() {
        Intent intent = new Intent();
//注意，在android5.0之后，启动服务都只能显示的启动，及明确指定包名和类的名字才能启动服务。
        intent.setComponent(new ComponentName("com.hut.reoger.aidltest","com.hut.reoger.aidltest.myService"));
        bindService(intent,conn, Context.BIND_AUTO_CREATE);

    }

    private void initView() {
        num1 = (EditText) findViewById(R.id.num1 );
        num2 = (EditText) findViewById(R.id.num2 );
        num3 = (TextView) findViewById(R.id.num3);
    }

    public void testTry(View view){
         int a = Integer.valueOf(num1.getText().toString());
         int b = Integer.valueOf(num2.getText().toString());
        try {
            int res = iMyAidlInterface.add(a,b);//远程调用
            num3.setText("数据"+res);
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.e("TAG","hello");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iMyAidlInterface = null;
        unbindService(conn);//取消绑定
    }

}
```

效果也比较简单。如图：

![aidl.gif](http://upload-images.jianshu.io/upload_images/2178834-65fbcd449c55c279.gif?imageMogr2/auto-orient/strip)
不过，需要注意的一点就是，需要先启动服务端，然后再启动客户端，负责会抛出异常，无法得到正确的效果。

# 使用AIDL传递复杂的数据对象
我们这里就用一个Person来表示复杂的数据，我们在里面定义了两个数据，并实现了Parcelable接口，关于序列化，可以参考我们以前的文章<http://blog.csdn.net/reoger/article/details/51335135>,这里就不重复啰嗦了，直接上代码：
```
package com.hut.reoger.aidltest;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 24540 on 2017/3/12.
 */

public class Person implements Parcelable {
    private String name;
    private int age;

    public Person(String name,int age){
        this.name = name;
        this.age = age;
    }

    protected Person(Parcel in) {
        name = in.readString();
        age = in.readInt();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
    }

    @Override
    public String toString() {
        return "名字"+this.name+"年龄"+this.age;
    }
}
```

然后在aidl文件中新建两个aidl文件，代码如下：
```
// IDataBase.aidl
package com.hut.reoger.aidltest;

parcelable Person;
```

IDataBase.aidl文件代码内容
```
// IDataBase.aidl
package com.hut.reoger.aidltest;
import com.hut.reoger.aidltest.Person;
interface IDataBase {
    List<Person> add(in Person person);
}
```

然后在代码中实现service，在servicer中实现add方法。代码如下：
```
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
```
在来一张项目结构图：

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-33ed5916cf297ffa.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

最后，别忘了在manifest文件中添加申明：
```
 <service android:name=".myService"
            android:process=":remote"
            android:exported="true"/>
```

## 服务端实现。
首先将aidl文件copy过来，然后直接需要将Person文件也copy过来，。注意，这里我们不能更改Person文件的目录，即我们需要保持如图所示的地方也与服务端的一致。为此，我们需要新建一个包来实现这个效果。

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-a1d65c4542462549.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

准备工作完了，我们就可以直接在java文件中进行调用了。代码如下：
```
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
```
因为只是显示怎么使用，所以并没有进行过多的界面等设计，我们点击一个按钮，就远程服务端就会收到一个数据，并且将之前收到的数据一并返回过来，具体先过可以参见下用途；

![Paste_Image.png](http://upload-images.jianshu.io/upload_images/2178834-aa98aed7901e4a46.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
