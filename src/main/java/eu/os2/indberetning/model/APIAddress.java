package eu.os2.indberetning.model;

import lombok.Data;

@Data
public class APIAddress {
    private String street;
    private int postalCode;
    private String city;
}
