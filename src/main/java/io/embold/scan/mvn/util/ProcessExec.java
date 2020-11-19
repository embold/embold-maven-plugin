package io.embold.scan.mvn.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.apache.commons.exec.environment.EnvironmentUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import io.embold.scan.exec.OsCheck;
import io.embold.scan.exec.OsCheck.OSType;

public class ProcessExec {
    public static void executeProcessSync(Log logger, String executable, String[] args, Map<String, String> env)
            throws MojoExecutionException {

        // For some reason maven is modifying this variable incorrectly (the dreaded
        // space-in-path issue on Windows), so resetting it
        // as our batch scripts set it correctly before launching java
        if (OsCheck.getOperatingSystemType().equals(OSType.Windows)) {
            env.put("JAVACMD", "");
        }

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("Executing synchronous command " + executable + " with args: " + Arrays.toString(args));
            }

            Map<String, String> envs = EnvironmentUtils.getProcEnvironment();
            envs.putAll(env);

            CommandLine cmdLine = new CommandLine(executable);

            for (int i = 0; i < args.length; i++) {
                cmdLine.addArgument(args[i], false);
            }

            DefaultExecutor executor = new DefaultExecutor();
            ExecuteWatchdog watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
            executor.setWatchdog(watchdog);
            executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
            executor.execute(cmdLine, envs);
        } catch (IOException e) {
            throw new MojoExecutionException("Error while launching embold scan", e);
        }
    }
}
