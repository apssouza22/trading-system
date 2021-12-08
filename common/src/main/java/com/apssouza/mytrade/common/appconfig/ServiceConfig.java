package com.apssouza.mytrade.common.appconfig;


import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class responsible to config the application
 */
public class ServiceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceConfig.class);

    /**
     * Load the configuration properties
     *
     * @param configPathDir config path
     * @param env           environment
     * @return config provider
     */
    public static ConfigurationProvider load(String configPathDir, String env) {
        String configLocation = ServiceConfig.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (configPathDir != null) {
            configLocation = configPathDir;
        }

        var filePath = Paths.get(String.format("%s/properties.%s.yml", configLocation, env));
        List<Path> classPathFiles = Arrays.asList(Paths.get("properties.yml"));
        List<Path> filePaths = Arrays.asList(filePath);

        Stream.of(classPathFiles, filePaths)
                .flatMap(i -> i.stream())
                .forEach(file -> LOG.info("Loading configuration from {}", file));

        ConfigurationSource classPathSource = new ClasspathConfigurationSource(() -> classPathFiles);
        ConfigurationSource fileSource = new FilesConfigurationSource(() -> filePaths);
        ConfigurationSource envSource = new EnvironmentVariablesConfigurationSource();
        ConfigurationSource mergedSource = new MergeConfigurationSource(
                classPathSource,
                fileSource,
                envSource
        );
        return new ConfigurationProviderBuilder()
                .withConfigurationSource(mergedSource)
                .build();
    }

    /**
     * Bind the gRPC server properties to the ServerConfigDto
     *
     * @param config amadeus service's server config
     * @return {@link ServerConfigDto}
     */
    public static ServerConfigDto bindServerProps(ConfigurationProvider config) {
        return config.bind("grpc.server", ServerConfigDto.class);
    }

}
