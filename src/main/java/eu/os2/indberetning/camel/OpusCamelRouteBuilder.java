package eu.os2.indberetning.camel;

import eu.os2.indberetning.config.MunicipalityConfiguration;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class OpusCamelRouteBuilder extends RouteBuilder {

    public static final String ROUTE_ID = "OpusSyncRoute";

    @Autowired
    private MunicipalityConfiguration municipalityConfiguration;

    @Override
    public void configure() throws Exception {

        // seda queue ensures only 1 concurrent consumer (default)
        from("seda:main")
        .log("Opus integration update starting")
        .split(constant(municipalityConfiguration.getMunicipalities()))
        .setHeader("municipalityName",simple("${body.name}"))
        .to("bean:opusFileParserTask?method=execute")
        .end()
        .log("Opus integration update complete");

        // run at specific intervals
        from("quartz2:scheduler?cron={{scheduler.cron}}")
        .routeId("SchedulerRoute")
        .to("seda:main");

        // run at application start up
        from("timer:StartupRoute?repeatCount=1")
        .routeId("StartupRoute")
        .to("seda:main");
    }
}
