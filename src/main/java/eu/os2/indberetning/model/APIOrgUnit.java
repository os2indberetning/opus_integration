package eu.os2.indberetning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIOrgUnit {
    private String id;
    private String parentId;
    private String name;
    private APIAddress address;
    private String costCenter;
}