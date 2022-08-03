package io.embold.scan.mvn.settings;

import io.embold.scan.mvn.Repo;

public class Settings {
    private final Repo repo;
    private final String baseDir;
    private final String dataDir;
    private final String coronaLocation;
    private final Integer publishPort;
    private final boolean scannerUpdate;


    public Settings(Repo repo, String baseDir, String dataDir, String coronaLocation,
                    Integer publishPort, boolean scannerUpdate) {
        super();
        this.repo = repo;
        this.baseDir = baseDir;
        this.dataDir = dataDir;
        this.coronaLocation = coronaLocation;
        this.publishPort = publishPort;
        this.scannerUpdate = scannerUpdate;
    }

    public Repo getRepo() {
        return repo;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getCoronaLocation() {
        return coronaLocation;
    }

    public Integer getPublishPort() {
        return publishPort;
    }

    public boolean isScannerUpdate() {
        return scannerUpdate;
    }
}
