package com.roadregistry.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.io.FileWriter;

import com.roadregistry.model.Utility;

public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<Date, Integer> demeritPoints;
    private boolean isSuspended;

    public Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;
    }

    public boolean addPerson(){
        if(Utility.validateID(personID) && Utility.validateAddress(address) && Utility.validateBirthdate(birthDate)) {
            try {
                FileWriter writer = new FileWriter("person.txt");
                writer.write(personID + "|" + firstName + "|" + lastName + "|" + address + "|" + birthDate);
                return true;
            } catch (IOException e) {
                System.out.println("Error writing to file");
                return false;
            }
        }
        else {
            return false;
        }
    }
}