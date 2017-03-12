// IDataBase.aidl
package com.hut.reoger.aidltest;
import com.hut.reoger.aidltest.Person;

interface IDataBase {
    List<Person> add(in Person person);
}
