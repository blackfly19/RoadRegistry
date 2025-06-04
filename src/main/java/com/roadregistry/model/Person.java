package com.roadregistry.model;


import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.io.FileWriter;

import java.nio.file.Files;
import java.nio.file.Path;

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

    private boolean isSuspended;
    private String offenseDate;
    private int points;

    public void setOffenseDate(String offenseDate) {
        this.offenseDate = offenseDate;
    }

    public String getOffenseDate() {
        return this.offenseDate;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return this.points;
    }

    public void setSuspended(boolean suspended) {
        this.isSuspended = suspended;
    }

    public boolean isSuspended() {
        return this.isSuspended;
    }


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
        this.isSuspended = false;
    }

    //add person to the txt file after validating person's detail
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
    public boolean updatePersonalDetails(Path filePath) throws IOException {
        String content = Files.readString(filePath);
        String[] parts = content.split(",");

        if (parts.length != 5) return false;

        String currentID = parts[0];
        String currentFirstName = parts[1];
        String currentLastName = parts[2];
        String currentAddress = parts[3];
        String currentBirthday = parts[4];


        if (!Utility.validateID(this.personID) || !Utility.validateAddress(this.address) || !Utility.validateBirthdate(this.birthDate)) {
            return false;
        }

        int age;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDateParsed = sdf.parse(currentBirthday);
            Date today = new Date();
            long ageInMillis = today.getTime() - birthDateParsed.getTime();
            age = (int) (ageInMillis / (1000L * 60 * 60 * 24 * 365));
        } catch (Exception e) {
            return false;
        }

        // Restriction: Under 18 can't change address
        if (age < 18 && !this.address.equals(currentAddress)) {
            return false;
        }

        // Restriction: Birthdate change must keep other fields the same
        if (!this.birthDate.equals(currentBirthday)) {
            if (!this.personID.equals(currentID) || !this.firstName.equals(currentFirstName)
                    || !this.lastName.equals(currentLastName) || !this.address.equals(currentAddress)) {
                return false;
            }
        }

        // Restriction: Cannot change ID if it starts with even digit
        char firstChar = currentID.charAt(0);
        if (Character.isDigit(firstChar) && ((firstChar - '0') % 2 == 0) && !this.personID.equals(currentID)) {
            return false;
        }

        // Update and write to file
        String updatedContent = String.join(",", this.personID, this.firstName, this.lastName, this.address, this.birthDate);
        Files.writeString(filePath, updatedContent);

        return true;
    }
}