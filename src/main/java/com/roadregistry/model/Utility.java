package com.roadregistry.model;

public class Utility {

    // validate id of person
    public static boolean validateID(String id){
        return id.matches("^[2-9][0-9][^A-Za-z0-9]{2,}.*[A-Za-z]{2}$") && id.length() == 10;
    }

    // validate address formate of person
    public static boolean validateAddress(String address){
        return address.matches("\\d+\\|.+\\|.+\\|Victoria\\|.+");
    }

    // validate person's birthdate format
    public static boolean validateBirthdate(String birthDate){
        return birthDate.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
