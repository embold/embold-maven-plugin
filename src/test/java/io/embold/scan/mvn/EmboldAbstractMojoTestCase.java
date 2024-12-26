package io.embold.scan.mvn;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.*;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class EmboldAbstractMojoTestCase extends AbstractMojoTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp(); // Ensure proper setup for Mojo lookups
    }

    protected MavenSession newMavenSession() {
        try {
            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
            MavenExecutionResult result = new DefaultMavenExecutionResult();

            MavenExecutionRequestPopulator populator = getContainer().lookup(MavenExecutionRequestPopulator.class);
            populator.populateDefaults(request);

            request.setSystemProperties(System.getProperties());

            DefaultMaven maven = (DefaultMaven) getContainer().lookup(Maven.class);
            DefaultRepositorySystemSession repoSession =
                    (DefaultRepositorySystemSession) maven.newRepositorySession(request);

            repoSession.setLocalRepositoryManager(new SimpleLocalRepositoryManagerFactory()
                    .newInstance(repoSession, new LocalRepository(request.getLocalRepository().getBasedir())));

            return new MavenSession(getContainer(), repoSession, request, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected MavenSession newMavenSession(MavenProject project) {
        MavenSession session = newMavenSession();
        session.setCurrentProject(project);
        session.setProjects(Arrays.asList(project));
        return session;
    }

    protected Mojo lookupConfiguredMojo(File pom, String goal) throws Exception {
        org.junit.jupiter.api.Assertions.assertNotNull(pom, "POM file cannot be null");
        org.junit.jupiter.api.Assertions.assertTrue(pom.exists(), "POM file must exist");

        ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

        ScanMojo mojo = (ScanMojo) lookupConfiguredMojo(project, goal);
        mojo.mavenSession = newMavenSession(project);
        return mojo;
    }

    protected String emboldUrl() {
        String url = System.getProperty("embold.host.url", System.getenv("embold.host.url"));
        return StringUtils.defaultIfEmpty(url, "");
    }

    protected String emboldToken() {
        String token = System.getProperty("embold.user.token", System.getenv("embold.user.token"));
        return StringUtils.defaultIfEmpty(token, "");
    }
}
