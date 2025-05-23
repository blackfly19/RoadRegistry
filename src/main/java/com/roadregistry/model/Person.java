package com.roadregistry.model;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.io.FileWriter;

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
        if(validateID(personID) && validateAddress(address) && validateBirthdate(birthDate)) {
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
    private boolean validateID(String id){
        return id.matches("^[2-9][0-9][^A-Za-z0-9]{2,}.*[A-Z]{2}$") && id.length() == 10;
    }
    private boolean validateAddress(String address){
        return address.matches("\\d+\\|.+\\|.+\\|Victoria\\|.+");
    }
    private boolean validateBirthdate(String birthDate){
        return birthDate.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}