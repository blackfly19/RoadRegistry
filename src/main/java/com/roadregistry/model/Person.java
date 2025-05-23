package com.roadregistry.model;

import java.util.Date;
import java.util.HashMap;

public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthDate;
    private HashMap<Date, Integer> demeritPoints;
    private boolean isSuspended;


}