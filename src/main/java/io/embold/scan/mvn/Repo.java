package io.embold.scan.mvn;

import io.embold.scan.mvn.util.JsonBuilder;
import org.apache.maven.plugin.MojoExecutionException;

public class Repo {

    private final String repoUid;
    private final String repoName;
    private final String repoType = "remote";
    private final String repoLanguage = "JAVA"; // TODO dynamically selected

    public Repo(String repoUid, String repoName) {
        super();
        this.repoUid = repoUid;
        this.repoName = repoName;
    }

    public String getRepoUid() {
        return repoUid;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoType() { return repoType; }

    public String getRepoLanguage() { return repoLanguage; }

    @Override
    public String toString() {
        return "Repository [uid=" + repoUid + ", name=" + repoName + "]";
    }

    public String jsonString() throws MojoExecutionException {
        return JsonBuilder.repoData(this);
    }
}
