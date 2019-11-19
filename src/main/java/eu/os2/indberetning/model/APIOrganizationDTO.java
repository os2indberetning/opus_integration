package eu.os2.indberetning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class APIOrganizationDTO {
    public List<APIOrgUnit> orgUnits;
    public List<APIPerson> persons;

}
