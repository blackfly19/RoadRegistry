package com.roadregistry.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    @Test
    public void testPersonID() {
        Person person = new Person("bv_$%^&#78", "maria","jonas","4|Letchworth street|Melbourn|Victoria|Australia","12-09-2004");
        assertFalse(person.addPerson());
    }

    @Test
    public void testPersonAddress() {
        Person person = new Person("23_%&&**%^","happy","nick","34|Bourke street|Melbourn|Sydney|Australia","11-03-2002");
        assertFalse(person.addPerson());
    }

    @Test
    public void testPersonBirthDate() {
        Person person = new Person("56*&%%$%gh","peter","jackson","22|swanston street|Melbourne|Victoria|Australia","23-10-20");
        assertFalse(person.addPerson());
    }

    @Test
    public void testBirthDateFormat() {
        Person person = new Person("54%$^#$@df","Mia","sen","21|Narinaway Street|Melbourne|Victoria|Australia","23/10/2006");
        assertFalse(person.addPerson());
    }
}
