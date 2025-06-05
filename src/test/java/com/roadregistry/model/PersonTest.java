package com.roadregistry.model;

import org.junit.jupiter.api.Test;

import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    // testing ID by entering 2 digit in the end
    @Test
    public void testPersonID() {
        Person person = new Person("bv_$%^&#78", "maria", "jonas", "4|Letchworth street|Melbourne|Victoria|Australia", "12-09-2004");
        assertFalse(person.addPerson());
    }

    // testing the address of the person by entering wrong state sydney
    @Test
    public void testPersonAddress() {
        Person person = new Person("56s_d%&fAB", "happy", "nick", "34|Bourke street|Sydney|NSW|Australia", "11-03-2002");
        assertFalse(person.addPerson());
    }

    // testing the format of birthdate
    @Test
    public void testBirthDateFormat() {
        Person person = new Person("56s_d%&fAB", "Mia", "sen", "21|Narinaway Street|Melbourne|Victoria|Australia", "23/10/2006");
        assertFalse(person.addPerson());
    }

    // All details are correct. This test will pass
    @Test
    public void testPersonDetailInsertion() {
        Person person = new Person("56s_d%&fAB", "Foo", "Bar","32|Highland Street|Melbourne|Victoria|Australia", "15-11-1990");
        assertTrue(person.addPerson());
    }

    @Test
    public void testValidInputUnder21_NotSuspended() {
        Person p = new Person("23@#abcXY", "Alex", "Young", "45|Oak Avenue|Melbourne|Victoria|Australia", "10-05-2007");
        p.setOffenseDate("15-05-2024");
        p.setPoints(3);
        String result = p.addDemeritPoints();
        assertEquals("Success", result);
    }

    @Test
    public void testInvalidDateFormat() {
        Person p = new Person("56$*kloMN", "Nina", "Lee", "88|River Road|Melbourne|Victoria|Australia", "15-03-2006");
        p.setOffenseDate("2024/05/15"); // Wrong format
        p.setPoints(4);
        String result = p.addDemeritPoints();
        assertEquals("Invalid date format. Use DD-MM-YYYY", result);
    }

    @Test
    public void testInvalidDemeritPoints() {
        Person p = new Person("39##zxcEP", "Hannah", "Lee", "77|Exhibition Road|Melbourne|Victoria|Australia", "09-09-1993");
        p.setOffenseDate("15-05-2024");
        p.setPoints(10); // Invalid points
        String result = p.addDemeritPoints();
        assertEquals("Invalid demerit points. Must be between 1 and 6", result);
    }

    @Test
    public void testvalidDemeritPoints() {
        Person p = new Person("89@@lmnWR", "Sophia", "Tan", "21|George Street|Melbourne|Victoria|Australia", "13-12-2000");
        p.setOffenseDate("15-05-2024");
        p.setPoints(2); // Valid points
        String result = p.addDemeritPoints();
        assertEquals("Success", result);
    }

    @Test
    public void testSuspensionOver21() {
        Person p = new Person("34$#mnoQT", "Daniel", "Smith", "67|Maple Lane|Melbourne|Victoria|Australia", "08-04-2001"); // Age > 21
        p.setOffenseDate("10-01-2023");
        p.setPoints(5);
        p.addDemeritPoints();

        p.setOffenseDate("12-06-2023");
        p.setPoints(4);
        p.addDemeritPoints();

        p.setOffenseDate("15-03-2024");
        p.setPoints(5);
        p.addDemeritPoints();

        assertTrue(p.isSuspended(), "Person should be suspended when demerit points exceed 12 for age 21 or over.");
    }

    @Test
    public void testNotSuspensionOver21() {
        Person p = new Person("78!&ghrAZ", "John", "Doe", "12|Hillview Drive|Melbourne|Victoria|Australia", "01-01-1990"); // Age > 21
        p.setOffenseDate("06-02-2023");
        p.setPoints(2);
        p.addDemeritPoints();

        p.setOffenseDate("12-05-2023");
        p.setPoints(3);
        p.addDemeritPoints();

        p.setOffenseDate("15-02-2024");
        p.setPoints(4);
        p.addDemeritPoints();

        assertFalse(p.isSuspended(), "Person should NOT be suspended when total demerit points is below threshold (12) for age 21 or over.");
    }

    /*
    @Test
    public void testValidUpdate() throws Exception {

        Files.writeString(tempFile, "91abcXYZ,Maria,Lopez,123|Main Street|Melbourne|Victoria|Australia,01-01-2000");

        Person p = new Person("91abcXYZ", "Maria", "Lopez", "456|New Street|Melbourne|Victoria|Australia", "01-01-2000");
        assertFalse(p.updatePersonalDetails(tempFile));

        String updatedContent = Files.readString(tempFile);
        assertEquals("91abcXYZ,Maria,Lopez,456|New Street|Melbourne|Victoria|Australia,01-01-2000", updatedContent);
    }*/

    @Test
    public void testInvalidAddressForUnder18() throws Exception {
        Path tempFile = Files.createTempFile("person", ".txt");
        Files.writeString(tempFile, "11abcXYZ,Jane,Doe,123|Hill Rd|Melbourne|Victoria|Australia,01-01-2010");

        Person p = new Person("11abcXYZ", "Jane", "Doe", "456|Lake St|Melbourne|Victoria|Australia", "01-01-2010");
        assertFalse(p.updatePersonalDetails(tempFile));
    }

    @Test
    public void testInvalidBirthdateFormat() throws Exception {
        Path tempFile = Files.createTempFile("person", ".txt");
        Files.writeString(tempFile, "77abcXYZ,Tom,Smith,123|Elm St|Melbourne|Victoria|Australia,01-01-2000");

        Person p = new Person("77abcXYZ", "Tom", "Smith", "123|Elm St|Melbourne|Victoria|Australia", "01/01/2000");
        assertFalse(p.updatePersonalDetails(tempFile));
    }


}
