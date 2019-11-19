package eu.os2.indberetning.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.amazonaws.util.IOUtils;

import eu.os2.indberetning.config.MunicipalityConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3Service {
	@Autowired
	private ResourcePatternResolver resourcePatternResolver;

	@Autowired
	private MunicipalityConfiguration configuration;

	@SneakyThrows
	public String getNewestFilename(String bucketName, String filePrefix)
	{
		Resource[] xmlFiles = resourcePatternResolver.getResources("s3://" + bucketName + "/" + filePrefix + "*");
		Resource newestResource = null;
		for (Resource resource : xmlFiles) {
			if (newestResource == null || resource.getFilename().compareTo(newestResource.getFilename()) > 0) {
				newestResource = resource;
			}
		}
		return newestResource == null ? null : newestResource.getFilename();
	}

	@SneakyThrows
	public InputStreamReader readFile(String bucketName,String fileName) {
		var resource = resourcePatternResolver.getResource("s3://" + bucketName + "/" + fileName);
		return new InputStreamReader(resource.getInputStream());
	}
}