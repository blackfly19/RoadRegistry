package com.roadregistry.model;


import java.io.*;
import java.text.SimpleDateFormat;

import java.util.*;

import java.nio.file.Files;
import java.nio.file.Path;

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

    private final String personFileName = "persons.txt";

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public HashMap<Date, Integer> getDemeritPoints() {
        return demeritPoints;
    }

    public void setDemeritPoints(HashMap<Date, Integer> demeritPoints) {
        this.demeritPoints = demeritPoints;
    }

    public void setSuspended(boolean suspended) {
        this.isSuspended = suspended;
    }

    public boolean isSuspended() {
        return this.isSuspended;
    }


    public String getOffenseDate() {
        return offenseDate;
    }

    public void setOffenseDate(String offenseDate) {
        this.offenseDate = offenseDate;
    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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

        //Validating personID, address and birthdate according to given conditions
        if (Utility.validateID(personID) && Utility.validateAddress(address) && Utility.validateBirthdate(birthDate)) {
            // try-with-resources
            try (FileWriter writer = new FileWriter(personFileName, true)){

                // Write to file if everything is correct
                writer.write(personID + "," + firstName + "," + lastName + "," + address + "," + birthDate+"\n");
                return true;
            } catch (IOException e) {
                System.out.println("Error writing to file");
                return false;
            }
        } else {

            // Return false if any field is incorrect
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
        demeritPoints.put(date, points);

        // Step 4: Check suspension logic
        int age = Utility.dateDiffToday(birthDate);
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

    public boolean updatePersonalDetails(String personID) {

        ArrayList<String> fileContent = new ArrayList<>();
        File file = new File(personFileName);
        int index = -1;
        String []parts = new String[5];

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContent.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading the file"+e.getMessage());
            return false;
        }

        for(int i=0;i < fileContent.size(); i++) {
            String[] temp = fileContent.get(i).split(",");

            // To check the content in the person.txt file
            if (temp.length != 5)
                return false;

            if(temp[0].equals(personID)) {
                index = i;
                parts = temp;
                break;
            }
        }

        String currentID = parts[0];
        String currentFirstName = parts[1];
        String currentLastName = parts[2];
        String currentAddress = parts[3];
        String currentBirthday = parts[4];


        if (!Utility.validateID(this.personID) || !Utility.validateAddress(this.address) || !Utility.validateBirthdate(this.birthDate)) {
            return false;
        }

        // Restriction: Under 18 can't change address
        if (!this.address.equals(currentAddress) && Utility.dateDiffToday(currentBirthday) < 18) {
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
        if (!this.personID.equals(currentID) && Character.isDigit(firstChar) && ((firstChar - '0') % 2 == 0)) {
            return false;
        }

        // Update and write to file
        fileContent.set(index, String.join(",", this.personID, this.firstName, this.lastName, this.address, this.birthDate));
        try(FileWriter writer = new FileWriter(file)) {
            for (String s : fileContent) {
                writer.write(s + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        return true;
    }

    public int getDemeritsWithinTwoYears(Date referenceDate) {
        int total = 0;

        // Calculate date 2 years back the offense date
        Calendar cal = Calendar.getInstance();
        cal.setTime(referenceDate);
        cal.add(Calendar.YEAR, -2);
        Date twoYearsAgo = cal.getTime();

        // Iterating through all the offense dates
        for (Date d : demeritPoints.keySet()) {
            if (!d.before(twoYearsAgo) && !d.after(referenceDate)) { // include only those within 2 years before reference date
                total += demeritPoints.get(d);
            }
        }
        return total;
    }

}