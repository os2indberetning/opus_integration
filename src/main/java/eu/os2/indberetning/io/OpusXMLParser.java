package eu.os2.indberetning.io;

import eu.kmd.opus.Kmd;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import java.io.Reader;

@Component
public class OpusXMLParser {

    public Kmd parseXML(Reader reader) throws Exception {
        JAXBContext context = JAXBContext.newInstance(Kmd.class);

        return (Kmd) context.createUnmarshaller().unmarshal(reader);
    }
}
