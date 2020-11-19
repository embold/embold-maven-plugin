package io.embold.scan.mvn;

import java.io.File;
import java.io.StringWriter;

public class EmboldScanMojoTest extends EmboldAbstractMojoTestCase {

    private StringWriter mojoOutputWriter;

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
        return getTestFile("src/test/resources/project-to-test/" + file);
    }

    public void testEmboldRun() throws Exception {
        ScanMojo mojo = getMojo("pom");
        mojo.execute();
        System.out.println(getMojoOutput());
//        assertTrue("Output:\n"+getMojoOutput(), !getMojoOutput().contains("junit:junit:3.8.1"));
//        assertOutputEqualsFileInCurrentTestProject("expected-report.txt");
    }

    protected String getMojoOutput() {
        return mojoOutputWriter.toString();
    }
}
