package io.embold.scan.mvn.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

import kong.unirest.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

public class SettingsLoader {
    private final JSONObject root;
    private static final String ELEM_GAMMA_ACCESS = "gammaAccess";
    private static final String ELEM_URL = "url";
    private static final String ELEM_TOKEN = "token";
    private static final String ELEM_REPOSITORIES = "repositories";

    public SettingsLoader(String lang, String url, String token, Settings settings) throws MojoExecutionException {
        try (InputStream in = getClass().getResourceAsStream("/gammascanner_" + lang + ".json")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String content = reader.lines().collect(Collectors.joining());
                root = new JSONObject(content);
                root.getJSONObject(ELEM_GAMMA_ACCESS).put(ELEM_URL, url);
                root.getJSONObject(ELEM_GAMMA_ACCESS).put(ELEM_TOKEN, token);
                JSONObject repoWrpElem = (JSONObject) root.getJSONArray(ELEM_REPOSITORIES).get(0);
                JSONObject repoElem = repoWrpElem.getJSONObject("repository");
                repoElem.put("uid", settings.getRepo().getRepoUid());
                repoElem.put("projectName", settings.getRepo().getRepoName());
                JSONObject sourcesElem = repoElem.getJSONObject("sources");
                sourcesElem.put("baseDir", settings.getBaseDir());
                repoWrpElem.put("dataDir", settings.getDataDir());
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error while loading embold settings" + e.getMessage());
        }
    }

    public void writeScanSettings(String file) throws MojoExecutionException {
        try {
            FileUtils.write(new File(file), root.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new MojoExecutionException("Error while creating embold scan settings", e);
        }
    }
}
