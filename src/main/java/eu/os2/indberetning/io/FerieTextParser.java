package eu.os2.indberetning.io;

import eu.os2.indberetning.model.APIVacationBalance;
import eu.os2.indberetning.model.VacationBalanceLine;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class FerieTextParser {

    private static final int DATA_LINE_LENGTH = 77;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<VacationBalanceLine> parseText(Reader reader) throws Exception {
        var bufferedReader = new BufferedReader(reader);
        var result = new ArrayList<VacationBalanceLine>();
        // read first line
        String line = bufferedReader.readLine();
        var dateString = line.substring(5,15);
        var fileDate = simpleDateFormat.parse(dateString);
        // read the rest of the file
        line = bufferedReader.readLine();
        while (line != null) {
            if (line.length() == DATA_LINE_LENGTH) {
                var vacationBalanceLine = new VacationBalanceLine();
                vacationBalanceLine.setCpr( line.substring(9,19) );
                vacationBalanceLine.setExtraNumber(Integer.parseInt(line.substring(19,20)));
                vacationBalanceLine.setVacationEarnedYear(Integer.parseInt(line.substring(21,25)));
                vacationBalanceLine.setVacationHoursWithPay(Double.parseDouble(line.substring(35,41))/100);
                vacationBalanceLine.setTransferredVacationHours(Double.parseDouble(line.substring(59,65))/100);
                vacationBalanceLine.setFreeVacationHoursTotal(Double.parseDouble(line.substring(71,77))/100);
                vacationBalanceLine.setUpdatedDate(fileDate);
                result.add(vacationBalanceLine);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        return result;
    }
}
