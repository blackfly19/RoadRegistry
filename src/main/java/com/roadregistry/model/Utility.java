package com.roadregistry.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    // validate id of person
    public static boolean validateID(String id){
        return id.matches("^[2-9]{2}(?=(?:.*[^A-Za-z0-9]){2,}).{6}[A-Z]{2}$") && id.length() == 10;
    }

    // validate address formate of person
    public static boolean validateAddress(String address){
        return address.matches("\\d+\\|.+\\|.+\\|Victoria\\|.+");
    }

    // validate person's birthdate format
    public static boolean validateBirthdate(String birthDate){
        return birthDate.matches("\\d{2}-\\d{2}-\\d{4}");
    }

    public static int dateDiffToday(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(dateString);
            Date today = new Date();
            long ageInMillis = today.getTime() - date.getTime();
            return (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
        } catch (Exception e) {
            return -1;
        }
    }
}
