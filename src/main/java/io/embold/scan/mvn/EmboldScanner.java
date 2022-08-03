package io.embold.scan.mvn;

import java.io.File;
import java.util.*;

import io.embold.scan.*;
import io.embold.scan.Package;
import io.embold.scan.exec.OsCheck;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import io.embold.scan.mvn.settings.Settings;
import io.embold.scan.mvn.settings.SettingsLoader;
import io.embold.scan.mvn.util.ProcessExec;

/**
 * Scans the maven project and publishes results to the Embold Server
 */
public class EmboldScanner {

    private final String url;
    private final String token;
    private final Log logger;

    public EmboldScanner(Log logger, String url, String token) {
        this.logger = logger;
        this.url = url;
        this.token = token;
    }

    public void scan(Settings settings) throws MojoExecutionException {

        ensureScanner(settings);

        // TODO Pick-up languages dynamically
        SettingsLoader loader = new SettingsLoader("JAVA", url, token, settings);
        String scanSettingsJson = settings.getDataDir() + File.separator + "scansettings.json";
        loader.writeScanSettings(scanSettingsJson);

        runScan(settings, scanSettingsJson);
    }

    private void ensureScanner(Settings settings) throws MojoExecutionException {
        if (settings.isScannerUpdate()) {
            logger.info("Checking for scanner updates");
            Set<Package> packages = new HashSet<>();
            // TODO Packages should be dynamic based on detected languages
            packages.add(Package.JAVA);

            ModularSyncOpts syncOpts = null;
            try {
                syncOpts = new ModularSyncOpts(this.url, this.token, settings.getCoronaLocation(), packages);
            } catch (SyncException e) {
                throw new MojoExecutionException("Error while configuring the plugin", e);
            }

            try {

                ModularSyncSession session = new ModularSyncSession(syncOpts);
                session.run();
            } catch (SyncException e) {
                logger.error("Error updating embold scanner package. Attempting to scan with existing version", e);
            }
        } else {
            logger.info("Skipping scanner updates");
        }
    }

    private void runScan(Settings settings, String scanSettingsJson) throws MojoExecutionException {
        Map<String, String> envs = new HashMap<String, String>();
        envs.put("CORONA_HOME", settings.getCoronaLocation() + File.separator + "corona");
        envs.put("CORONA_LOG", settings.getDataDir() + File.separator + "logs");
        // Use the new publishing API
        envs.put("EMB_USE_DATA_API", "true");
        String scannerExecutable = "gammascanner";
        if (OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Windows)) {
            scannerExecutable += ".bat";
        }

        String gammaScannerExec = settings.getCoronaLocation() + File.separator + "corona" + File.separator
                + "scanboxwrapper" + File.separator + "bin" + File.separator + scannerExecutable;
        List<String> argList = new ArrayList<String>();
        argList.add("-c");
        argList.add("\"" + scanSettingsJson + "\"");
        if (settings.getPublishPort() != null) {
            logger.info("Using custom publish port: " + settings.getPublishPort());
            argList.add("-p");
            argList.add(String.valueOf(settings.getPublishPort()));
        }

        String[] args = new String[argList.size()];
        args = argList.toArray(args);
        ProcessExec.executeProcessSync(logger, gammaScannerExec, args, envs);
    }
}
