package win.stvworlds.stvvelocity.config;

import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private final Path configPath;
    private final Logger logger;
    private CommentedConfigurationNode root;

    public ConfigManager(Path dataDirectory, Logger logger) {
        this.configPath = dataDirectory.resolve("config.conf");
        this.logger = logger;
    }

    public void load() {
        try {
            if (Files.notExists(configPath)) {
                Files.createDirectories(configPath.getParent());
                try (InputStream in = getClass().getResourceAsStream("/config.conf")) {
                    if (in != null) {
                        Files.copy(in, configPath);
                    }
                }
            }

            HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                    .path(configPath)
                    .build();

            root = loader.load();
        } catch (IOException e) {
            logger.error("Failed to load config", e);
        }
    }

    public CommentedConfigurationNode getRoot() {
        return root;
    }
}
