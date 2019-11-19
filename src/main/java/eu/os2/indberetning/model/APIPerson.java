package eu.os2.indberetning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIPerson {
    private String cpr;
    private String firstName;
    private String lastName;
    private APIAddress address;
    private String email;
    private List<APIEmployment> employments;
    private String initials;

}
