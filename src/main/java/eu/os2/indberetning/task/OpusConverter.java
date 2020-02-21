package eu.os2.indberetning.task;

import eu.kmd.opus.Kmd;
import eu.os2.indberetning.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static eu.os2.indberetning.utility.NullChecker.getValue;

@Slf4j
@Component
public class OpusConverter {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public APIOrganizationDTO OpusToApiOrganization(Kmd kmd, List<VacationBalanceLine> ferieLines) {
        var apiOrganization = new APIOrganizationDTO();
        AddOrgUnitsRecursive(apiOrganization, kmd, "");
        AddPersons(apiOrganization, kmd, ferieLines);
        return apiOrganization;
    }

    private void AddOrgUnitsRecursive(APIOrganizationDTO apiOrganization, Kmd kmd, String parent) {
        if (apiOrganization.orgUnits == null) {
            apiOrganization.orgUnits = new ArrayList<>();
        }
        var children = kmd.getOrgUnit().stream().filter(o -> o.getParentOrgUnit().equals(parent)).collect(Collectors.toList());
        for (var kmdOrgUnit : children) {
            var apiOrgUnit = new APIOrgUnit();
            apiOrgUnit.setId(kmdOrgUnit.getId());
            apiOrgUnit.setParentId(kmdOrgUnit.getParentOrgUnit());
            apiOrgUnit.setName(kmdOrgUnit.getLongName());
            apiOrgUnit.setCostCenter(getValue(() -> kmdOrgUnit.getCostCenter().toString()));
            var apiAddress = new APIAddress();
            apiAddress.setStreet(kmdOrgUnit.getStreet());
            apiAddress.setPostalCode(getValue(() -> kmdOrgUnit.getZipCode(),(short)0));
            apiAddress.setCity(kmdOrgUnit.getCity());
            apiOrgUnit.setAddress(apiAddress);
            apiOrganization.orgUnits.add(apiOrgUnit);
            AddOrgUnitsRecursive(apiOrganization, kmd, kmdOrgUnit.getId());
        }
    }

    @SneakyThrows
    private void AddPersons(APIOrganizationDTO apiOrganizationDTO, Kmd kmd, List<VacationBalanceLine> ferieLines) {
        apiOrganizationDTO.persons = new ArrayList<>();
        var kmdEmployees = kmd.getEmployee().stream().filter(e -> e.getAction() == null).collect(Collectors.toList());
        var uniqueCprs = new HashSet<String>();
        for (var kmdEmployee : kmdEmployees) {
            uniqueCprs.add(kmdEmployee.getCpr().getValue());
        }

        for (var cpr : uniqueCprs) {
            var kmdPerson = kmdEmployees.stream().filter(e -> e.getCpr().getValue().equals(cpr)).findFirst().get();
            var apiPerson = new APIPerson();
            apiPerson.setCpr(kmdPerson.getCpr().getValue());
            apiPerson.setFirstName(kmdPerson.getFirstName());
            apiPerson.setLastName(kmdPerson.getLastName());
            var apiAddress = new APIAddress();
            apiAddress.setStreet(getValue(() -> kmdPerson.getAddress().getValue()));
            apiAddress.setPostalCode(kmdPerson.getPostalCode().isBlank() ? 0 : Integer.parseInt(kmdPerson.getPostalCode()));
            apiAddress.setCity(kmdPerson.getCity());
            apiPerson.setAddress(apiAddress);

            var kmdPersonEmployments = kmdEmployees.stream().filter(e -> e.getCpr().getValue().equals(apiPerson.getCpr())).collect(Collectors.toList());
            apiPerson.setEmployments(new ArrayList<>());
            for (var kmdPersonEmployment : kmdPersonEmployments) {
                var apiEmployment = new APIEmployment();
                apiEmployment.setEmployeeNumber(String.valueOf(kmdPersonEmployment.getId()));
                apiEmployment.setEmploymentType(Integer.parseInt(kmdPersonEmployment.getWorkContract()));
                apiEmployment.setExtraNumber(kmdPersonEmployment.getCpr().getSuppId());
                if (!kmdPersonEmployment.getEntryDate().isBlank()) {
                    apiEmployment.setFromDate(simpleDateFormat.parse(kmdPersonEmployment.getEntryDate()));
                }
                if (!kmdPersonEmployment.getLeaveDate().isBlank()) {
                    apiEmployment.setToDate(simpleDateFormat.parse(kmdPersonEmployment.getLeaveDate()));
                }
                apiEmployment.setManager(kmdPersonEmployment.isIsManager());
                apiEmployment.setOrgUnitId(kmdPersonEmployment.getOrgUnit().toString());
                apiEmployment.setPosition(kmdPersonEmployment.getPosition());
                // handle vacation balance if present
                if( ferieLines != null)
                {
                    var ferieLine = ferieLines.stream().filter(fl -> fl.getCpr().equals(apiPerson.getCpr()) && fl.getExtraNumber() == apiEmployment.getExtraNumber()).findFirst();
                    if( ferieLine.isPresent())
                    {
                        var vacationBalance = new APIVacationBalance();
                        vacationBalance.setFreeVacationHoursTotal(ferieLine.get().getFreeVacationHoursTotal());
                        vacationBalance.setTransferredVacationHours(ferieLine.get().getTransferredVacationHours());
                        vacationBalance.setVacationHoursWithPay(ferieLine.get().getVacationHoursWithPay());
                        vacationBalance.setVacationEarnedYear(ferieLine.get().getVacationEarnedYear());
                        vacationBalance.setUpdatedDate(ferieLine.get().getUpdatedDate());
                        apiEmployment.setVacationBalance(vacationBalance);
                    }
                }
                apiPerson.getEmployments().add(apiEmployment);
            }
            apiOrganizationDTO.persons.add(apiPerson);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        var seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
