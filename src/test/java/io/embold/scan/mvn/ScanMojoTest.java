package io.embold.scan.mvn;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.io.StringWriter;

/**
 * ScanMojoTest runs the plugin-based scan against a test project.
 * For this to work, set the following environment vars before running tests:
 * EMBOLD_URL: points to your Embold Server
 * EMBOLD_TOKEN: Embold access token
 * For example:
 * mvn clean install -Dembold.host.url=<url> -Dembold.user.token=<token>
 */
public class ScanMojoTest extends EmboldAbstractMojoTestCase {

    private StringWriter mojoOutputWriter;

    public void testEmboldRun() throws Exception {
        ScanMojo mojo = this.getMojo("pom");
        mojo.execute();
    }

    protected ScanMojo getMojo(String projectSubdir) throws Exception {
        ScanMojo emboldMojo = (ScanMojo) lookupConfiguredMojo(getTestFileInCurrentTestProject("pom.xml"), "embold");
        assertNotNull(emboldMojo);
        mojoOutputWriter = new StringWriter();
        // Points to a test Embold instance specifically created to receive mvn plugin
        // analysis results created by unit tests
        emboldMojo.setEmboldHostUrl(emboldUrl());
        emboldMojo.setEmboldUserToken(emboldToken());
        return emboldMojo;
    }

    protected File getTestFileInCurrentTestProject(String file) {
        return getTestFile("src/test/resources/unit/project-to-test/" + file);
    }

    protected String getMojoOutput() {
        return mojoOutputWriter.toString();
    }
}
