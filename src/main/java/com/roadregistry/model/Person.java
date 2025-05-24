package com.roadregistry.model;


import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.io.FileWriter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<Date, Integer> demeritPoints;

    private boolean suspended;
    private String offenseDate;
    private int points;


    public void addDemerit(int points, Date date) {
        demeritPoints.put(date, points);
    }




    public int getAge() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date birth = sdf.parse(birthDate);
            Date today = new Date();
            long ageInMillis = today.getTime() - birth.getTime();
            return (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
        } catch (Exception e) {
            return -1;
        }
    }

    public int getDemeritsWithinTwoYears(Date referenceDate) {
        int total = 0;
        for (Date d : demeritPoints.keySet()) {
            long diff = referenceDate.getTime() - d.getTime(); // compare each offense to the current one
            long days = diff / (1000 * 60 * 60 * 24);
            if (days >= 0 && days <= 730) { // include only those within 2 years before reference date
                total += demeritPoints.get(d);
            }
        }
        return total;
    }

    public Person(String personID, String firstName, String lastName, String address, String birthDate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDate = birthDate;
        this.demeritPoints = new HashMap<>();
        this.suspended = false;
    }

    public boolean addPerson() {
        if (Utility.validateID(personID) && Utility.validateAddress(address) && Utility.validateBirthdate(birthDate)) {
            try {
                FileWriter writer = new FileWriter("person.txt");
                writer.write(personID + "," + firstName + "," + lastName + "," + address + "," + birthDate);
                return true;
            } catch (IOException e) {
                System.out.println("Error writing to file");
                return false;
            }
        } else {
            return false;
        }
    }


    public String addDemeritPoints() {
        Date date;

        // Step 1: Validate and parse date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            date = sdf.parse(this.offenseDate);
        } catch (Exception e) {
            return "Invalid date format. Use DD-MM-YYYY";
        }

        // Step 2: Validate points range
        if (this.points < 1 || this.points > 6) {
            return "Invalid demerit points. Must be between 1 and 6";
        }

        // Step 3: Add demerit entry
        this.addDemerit(points, date);

        // Step 4: Check suspension logic
        int age = this.getAge();
        int totalPoints = this.getDemeritsWithinTwoYears(date);

        if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
            this.setSuspended(true);
        }

        // Step 5: Log to file
        try (FileWriter writer = new FileWriter("demerit_log.txt", true)) {
            writer.write(this.personID + "," + this.firstName + " " + this.lastName + "," + this.offenseDate + "," + this.points + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            return "File writing error";
        }

        return "Success";

    }





}