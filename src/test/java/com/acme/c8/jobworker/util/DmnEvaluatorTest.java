package com.acme.c8.jobworker.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DmnEvaluatorTest {

    public static Map<String, Object> createSamplePatientHighRisk() {
        Map<String, Object> patient = new HashMap<>();

        patient.put("id", 1L);
        patient.put("memberId", "M-1108257d01d14a11946f1a102ef22a91");
        patient.put("firstName", "Charlotte");
        patient.put("lastName", "Brown");
        patient.put("dateOfBirth", LocalDate.parse("1976-07-10"));
        patient.put("gender", "Non-binary");
        patient.put("address", "9552 Oak St");
        patient.put("city", "Boston");
        patient.put("state", "MA");
        patient.put("zipCode", "87785");

        patient.put("bmi", 37.9);
        patient.put("glucoseLevel", 146.7);
        patient.put("cholesterolLevel", 202.5);

        patient.put("hasDiabetes", true);
        patient.put("hasHypertension", true);
        patient.put("hasCopd", false);

        patient.put("erVisitsLast12Months", 6);
        patient.put("medicationAdherent", true);

        patient.put("metabolicSyndromeRisk", true);
        patient.put("highReadmissionRisk", true);
        patient.put("medicationNonAdherenceRisk", false);
        patient.put("riskLevel", "High");

        return patient;
    }

    public static Map<String, Object> createSamplePatientLowRisk() {
        Map<String, Object> patient = new HashMap<>();

        patient.put("id", 1L);
        patient.put("memberId", "M-1108257d01d14a11946f1a102ef22a91");
        patient.put("firstName", "Charlotte");
        patient.put("lastName", "Brown");
        patient.put("dateOfBirth", LocalDate.parse("1976-07-10"));
        patient.put("gender", "Non-binary");
        patient.put("address", "9552 Oak St");
        patient.put("city", "Boston");
        patient.put("state", "MA");
        patient.put("zipCode", "87785");

        patient.put("bmi", 20);
        patient.put("glucoseLevel", 100);
        patient.put("cholesterolLevel", 202.5);

        patient.put("hasDiabetes", false);
        patient.put("hasHypertension", false);
        patient.put("hasCopd", false);

        patient.put("erVisitsLast12Months", 0);
        patient.put("medicationAdherent", true);

        patient.put("metabolicSyndromeRisk", true);
        patient.put("highReadmissionRisk", true);

        return patient;
    }
}
