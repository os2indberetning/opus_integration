package eu.os2.indberetning.model;

import lombok.Data;

@Data
public class APIAddress {
    private String street;
    private String postalCode;
    private String city;
}
