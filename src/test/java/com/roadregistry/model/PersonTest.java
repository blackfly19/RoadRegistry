package com.roadregistry.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    // testing ID by entering 2 digit in the end
    @Test
    public void testPersonIDCapitalLetters() {
        Person person = new Person("48_$fty^78", "maria", "jonas", "4|Letchworth street|Melbourne|Victoria|Australia", "12-09-2004");
        assertFalse(person.addPerson());
    }

    // Testing id by not entering any special characters
    @Test
    public void testPersonIDSpecialCharacters() {
        Person person = new Person("67tioperAB", "maria", "jonas", "4|Letchworth street|Melbourne|Victoria|Australia", "12-09-2004");
        assertFalse(person.addPerson());
    }

    // testing the address of the person by entering wrong state Brisbane
    @Test
    public void testPersonAddress() {
        Person person = new Person("56s_d%&fAB", "happy", "nick", "34|Bourke street|Melbourne|Brisbane|Australia", "11-03-2002");
        assertFalse(person.addPerson());
    }

    // testing the format of birthdate
    @Test
    public void testBirthDateFormat() {
        Person person = new Person("96s_d%&fAB", "Mia", "sen", "21|Narinaway Street|Melbourne|Victoria|Australia", "23/10/2006");
        assertFalse(person.addPerson());
    }

    // All details are correct. This test will add details to file
    @Test
    public void testPersonDetailInsertion() {
        Person person = new Person("58s_d%&fAB", "Foo", "Bar","32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(person.addPerson());
    }

    // Adding demerit points to the system
    @Test
    public void testValidInputUnder21_NotSuspended() {
        Person p = new Person("23@#abcXY", "Alex", "Young", "45|Oak Avenue|Melbourne|Victoria|Australia", "10-05-2007");
        p.setOffenseDate("15-05-2024");
        p.setPoints(3);
        String result = p.addDemeritPoints();
        assertEquals("Success", result);
    }

    // Testing invalid date format in offense date
    @Test
    public void testInvalidDateFormat() {
        Person p = new Person("56$*kloMN", "Nina", "Lee", "88|River Road|Melbourne|Victoria|Australia", "15-03-2006");
        p.setOffenseDate("2024/05/15"); // Wrong format
        p.setPoints(4);
        String result = p.addDemeritPoints();
        assertEquals("Invalid date format. Use DD-MM-YYYY", result);
    }

    // Testing range of the demerit points
    @Test
    public void testInvalidDemeritPoints() {
        Person p = new Person("39##zxcEP", "Hannah", "Lee", "77|Exhibition Road|Melbourne|Victoria|Australia", "09-09-1993");
        p.setOffenseDate("15-05-2024");
        p.setPoints(10); // Invalid points
        String result = p.addDemeritPoints();
        assertEquals("Invalid demerit points. Must be between 1 and 6", result);
    }

    // Test for valid demerit points
    @Test
    public void testvalidDemeritPoints() {
        Person p = new Person("89@@lmnWR", "Sophia", "Tan", "21|George Street|Melbourne|Victoria|Australia", "13-12-2000");
        p.setOffenseDate("15-05-2024");
        p.setPoints(2); // Valid points
        String result = p.addDemeritPoints();
        assertEquals("Success", result);
    }

    // Testing suspension if age > 21 and points more than 12
    @Test
    public void testSuspensionOver21() {
        Person p = new Person("34$#mnoQT", "Daniel", "Smith", "67|Maple Lane|Melbourne|Victoria|Australia", "08-04-2001"); // Age > 21
        p.setOffenseDate("10-01-2023");
        p.setPoints(5);
        assertEquals("Success",p.addDemeritPoints());

        p.setOffenseDate("12-06-2023");
        p.setPoints(4);
        assertEquals("Success",p.addDemeritPoints());

        p.setOffenseDate("15-03-2024");
        p.setPoints(5);
        assertEquals("Success",p.addDemeritPoints());

        assertTrue(p.isSuspended(), "Person should be suspended when demerit points exceed 12 for age 21 or over.");
    }

    // Testing no suspension if age > 21 but points less than 12
    @Test
    public void testNotSuspensionOver21() {
        Person p = new Person("78!&ghrAZ", "John", "Doe", "12|Hillview Drive|Melbourne|Victoria|Australia", "01-01-1990"); // Age > 21
        p.setOffenseDate("06-02-2023");
        p.setPoints(2);
        assertEquals("Success",p.addDemeritPoints());

        p.setOffenseDate("12-05-2023");
        p.setPoints(3);
        assertEquals("Success",p.addDemeritPoints());

        p.setOffenseDate("15-02-2024");
        p.setPoints(4);
        assertEquals("Success",p.addDemeritPoints());

        assertFalse(p.isSuspended(), "Person should NOT be suspended when total demerit points is below threshold (12) for age 21 or over.");
    }

    // All details are correct. The update will happen
    @Test
    public void testValidUpdate() {
        Person p = new Person("92^%abcXYZ", "Maria", "Lopez", "456|New Street|Melbourne|Victoria|Australia", "01-01-2000");
        p.addPerson();
        String personID = p.getPersonID();

        // Setting address and age > 18 so should be able to do it
        p.setAddress("457|New Street|Melbourne|Victoria|Australia");

        // Should work since first character is odd
        p.setPersonID("82^%abcXYZ");

        assertTrue(p.updatePersonalDetails(personID));
    }

    // Testing person ID validation
    @Test
    public void testPersonIDValidation() {
        Person p = new Person("78$#jkghLO", "Richard","Hendricks","458| Newell Street|Melbourne|Victoria|Australia", "05-12-2003");
        p.addPerson();

        String personID = p.getPersonID();

        p.setPersonID("random45");

        assertFalse(p.updatePersonalDetails(personID));
    }

    // The address won't change here since the person is under 18
    @Test
    public void testInvalidAddressForUnder18() {
        Person p = new Person("64*&irthPO", "Jane", "Doe", "7|siddeley street|Melbourne|Victoria|Australia", "01-01-2010");
        p.addPerson();

        p.setAddress("8|siddeley street|Melbourne|Victoria|Australia");

        assertFalse(p.updatePersonalDetails(p.getPersonID()));
    }

    // We are trying to change birthdate and lastname together which cannot happen
    @Test
    public void testBirthdateChange() {
        Person p = new Person("56()qwe4HJ", "Amy", "Santiago", "601|Little Lonsdale|Melbourne|Victoria|Australia", "01-01-2001");
        p.addPerson();

        p.setBirthDate("01-01-2000");
        p.setLastName("Doe");
        assertFalse(p.updatePersonalDetails(p.getPersonID()));
    }

    // Change of id shouldn't work since first character is an even number
    @Test
    public void testIDChange() {
        Person p = new Person("46#@dfu*KL", "Rosa", "Diaz", "576|Little Collins|Melbourne|Victoria|Australia", "01-01-2002");
        p.addPerson();
        String personID = p.getPersonID();

        p.setPersonID("62^%abcXYZ");
        assertFalse(p.updatePersonalDetails(personID));
    }
}
