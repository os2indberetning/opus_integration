package eu.os2.indberetning.task;

import eu.os2.indberetning.config.MunicipalityConfiguration;
import eu.os2.indberetning.io.FerieTextParser;
import eu.os2.indberetning.io.OpusXMLParser;
import eu.os2.indberetning.model.VacationBalanceLine;
import eu.os2.indberetning.service.OS2indberetningStub;
import eu.os2.indberetning.service.PersistentMapService;
import eu.os2.indberetning.service.S3Service;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Component
public class OpusFileParserTask {
    private static final SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OpusXMLParser opusXMLParser;

    @Autowired
    private FerieTextParser ferieTextParser;

    @Autowired
    private OpusConverter opusConverter;

    @Autowired
    private OS2indberetningStub os2indberetningStub;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private MunicipalityConfiguration configuration;

    @Autowired
    private PersistentMapService persistentMap;

    @Value("${lastFilesPath}")
    private String lastFilesPath;

    @SneakyThrows
    public void execute(@Header("municipalityName") String municipalityName) {
        var municipalityConfiguration = configuration.getMunicipalities().stream().filter(m -> m.getName().equals(municipalityName)).findFirst();
        if (municipalityConfiguration.isPresent()) {
            var municipality = municipalityConfiguration.get();

            var newestOpusFile = s3Service.getNewestFilename(municipality.getBucket(),"opus");
            var newestFerieFile = municipality.getType().equals("ferie") ? s3Service.getNewestFilename(municipality.getBucket(),"ferie") : null;

            var lastFiles = persistentMap.get(lastFilesPath);
            var lastOpusFileName = lastFiles.get(municipality.getTypeName() + ".opus");
            var lastFerieFileName = lastFiles.get(municipality.getTypeName() + ".ferie");

            // only update if there is an opus file. Only update if either of the files have changed since last run
            if( newestOpusFile != null && (!newestOpusFile.equals(lastOpusFileName) || (newestFerieFile != null && !newestFerieFile.equals(lastFerieFileName))))
            {
                log.info("Parsing OPUS file for " + municipality.getName());
                var opusReader = s3Service.readFile(municipality.getBucket(),newestOpusFile);
                var kmd = opusXMLParser.parseXML(opusReader);

                List<VacationBalanceLine> ferieLines = null;
                if( newestFerieFile != null)
                {
                   var ferieReader = s3Service.readFile(municipality.getBucket(),newestFerieFile);
                   ferieLines = ferieTextParser.parseText(ferieReader);
                }

                var apiOrganization = opusConverter.OpusToApiOrganization(kmd, ferieLines);

                log.info("Updating " + municipality.getName() + ". Orgunits: " + apiOrganization.orgUnits.size() + ". Persons: " + apiOrganization.persons.size());
                os2indberetningStub.updateOrganization(municipality.getUrl(), municipality.getApiKey(), apiOrganization);

                lastFiles.put(municipality.getTypeName() + ".opus", newestOpusFile);
                if( newestFerieFile != null )
                {
                    lastFiles.put(municipality.getTypeName() + ".ferie", newestFerieFile);
                }
                persistentMap.save(lastFilesPath,lastFiles);
            }
        }
    }
}
