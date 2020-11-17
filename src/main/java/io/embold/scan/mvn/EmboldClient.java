package io.embold.scan.mvn;

import kong.unirest.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

/**
 * Talks to the Embold Server using REST APIs for getting/creating repositories, etc.
 */
public class EmboldClient {

    private String apiVersion = "v1";
    private final String GET_REPO = "/api/" + apiVersion + "/repositories";
    private final String CREATE_REPO = "/api/" + apiVersion + "/repositories";

    private String emboldUrl;
    private String emboldToken;
    private Log logger;

    public EmboldClient(Log logger, String emboldUrl, String emboldToken) {
        super();
        this.logger = logger;
        this.emboldUrl = emboldUrl;
        this.emboldToken = emboldToken;
    }

    public void getCreateRepo(Repo repo) throws MojoExecutionException {
        boolean repoFound = false;
        HttpResponse<JsonNode> response = Unirest.get(this.emboldUrl + GET_REPO + "/" + repo.getRepoUid())
                .header("accept", "application/json").header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.emboldToken).asJson();
        if (response.isSuccess()) {
            JsonNode body = response.getBody();
            if (StringUtils.isNotEmpty(body.getObject().getString("repoName"))) {
                logger.info("Found repo: " + repo.getRepoUid() + ":" + repo.getRepoName());
                repoFound = true;
            }
        } else {
            if (response.getStatus() != 403 && response.getStatus() != 400) {
                // Actual error
                String msg = "Error while getting repo details from Embold server: " + errorResponse(response);
                logger.error(msg);
                throw new MojoExecutionException(msg);
            }
        }

        if (!repoFound) {
            logger.info("Creating: " + repo);
            response = Unirest.post(this.emboldUrl + CREATE_REPO).header("accept", "application/json")
                    .header("Content-Type", "application/json").header("Authorization", "Bearer " + this.emboldToken)
                    .body(repo.jsonString()).asJson();
            if (!response.isSuccess()) {
                String msg = "Error while creating repo on Embold server: " + errorResponse(response);
                logger.error(msg);
                throw new MojoExecutionException(msg);
            }
        }
    }

    private String errorResponse(HttpResponse<JsonNode> response) {
        JSONObject body = response.getBody().getObject();
        String error = "Unknown error";
        if (body.has("error")) {
            JSONObject errObj = body.getJSONObject("error");
            StringBuilder sb = new StringBuilder();
            sb.append(errObj.has("code") ? errObj.getInt("code") : -1).append(" | ")
                    .append(errObj.has("name") ? errObj.getString("name") : "Unknown error").append(" | ")
                    .append(errObj.has("message") ? errObj.getString("message") : "");
            error = sb.toString();
        }
        return error;
    }
}
