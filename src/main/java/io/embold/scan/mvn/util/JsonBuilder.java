package io.embold.scan.mvn.util;

import org.apache.maven.plugin.MojoExecutionException;

import io.embold.scan.mvn.Repo;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonBuilder {
	private static ObjectMapper mapper = null;
	static {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}
	
	public static String repoData(Repo repo) throws MojoExecutionException {
		try {
			return mapper.writeValueAsString(repo);
		} catch (JsonProcessingException e) {
			throw new MojoExecutionException("Error while creating repo", e);
		}
	}
}
