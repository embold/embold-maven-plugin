package io.embold.scan.mvn;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.embold.scan.mvn.settings.Settings;
import kong.unirest.Unirest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Goal which scans the maven project with embold
 */
@Mojo(name = "embold", requiresDependencyResolution = ResolutionScope.TEST, aggregator = true)
public class ScanMojo extends AbstractMojo {
    /**
     * Embold Server URL
     */
    @Parameter(property = "embold.host.url", required = true)
    private String emboldHostUrl;

    /**
     * Embold Token
     */
    @Parameter(property = "embold.user.token", required = true)
    private String emboldUserToken;

    /**
     * Embold analyser (corona) home directory - analyser will be downloaded here
     * on-the-fly
     */
    @Parameter(property = "embold.scanner.location", required = false)
    private String emboldScannerLocation;

    /**
     * Temporary optional port to publish scan results (will be removed once the new API is available)
     */
    @Parameter(property = "embold.publish.port", required = false)
    private Integer emboldPublishPort = null;

    /**
     * Enable/Disable scanner update
     */
    @Parameter(property = "embold.scanner.update", required = false)
    private final boolean emboldScannerUpdate = Boolean.TRUE;

    @Component
    MavenSession mavenSession;

    private static final String SEP = File.separator;

    public void execute() throws MojoExecutionException {

        try {

            if (StringUtils.isEmpty(emboldScannerLocation)) {
                emboldScannerLocation = mavenSession.getLocalRepository().getBasedir() + SEP +
                        "io" + SEP + "embold" + SEP + "scan" + SEP + "embold-maven-plugin";
                super.getLog().info("Resolved scanner location: " + emboldScannerLocation);
            } else {
                super.getLog().info("User-specified scanner location: " + emboldScannerLocation);
            }

            super.getLog().info("Using Embold url: " + emboldHostUrl);

            EmboldClient embApiClient = new EmboldClient(getLog(), emboldHostUrl, emboldUserToken);

            MavenProject topLevelProject = mavenSession.getTopLevelProject();

            String repoName = topLevelProject.getGroupId() + ":" + topLevelProject.getArtifactId();

            String repoUid = getRepoUid(repoName);

            Repo repo = new Repo(repoUid, repoName);

            embApiClient.getCreateRepo(repo);

//            mavenSession.getAllProjects();

            EmboldScanner scanner = new EmboldScanner(getLog(), emboldHostUrl, emboldUserToken);
            Settings settings = new Settings(repo,
                    FilenameUtils.normalize(topLevelProject.getBasedir().getAbsolutePath()),
                    FilenameUtils.normalize(topLevelProject.getBasedir() + File.separator + ".embold_data"),
                    emboldScannerLocation, emboldPublishPort, emboldScannerUpdate);
            scanner.scan(settings);
        } finally {
            Unirest.shutDown();
        }
    }

    private String getRepoUid(String repoName) throws MojoExecutionException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] hashInBytes = md.digest(repoName.getBytes(StandardCharsets.UTF_8));
            final StringBuffer hexString = new StringBuffer();
            for (final byte element : hashInBytes) {
                final String hex = Integer.toHexString(0xFF & element);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new MojoExecutionException("Error while generating embold repo UID", e);
        }
    }

    void setEmboldHostUrl(String emboldHostUrl) {
        this.emboldHostUrl = emboldHostUrl;
    }

    void setEmboldUserToken(String emboldUserToken) {
        this.emboldUserToken = emboldUserToken;
    }

    public void setEmboldScannerLocation(String emboldScannerLocation) {
        this.emboldScannerLocation = emboldScannerLocation;
    }

    public void setEmboldPublishPort(Integer emboldPublishPort) {
        this.emboldPublishPort = emboldPublishPort;
    }
}
