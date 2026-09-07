package win.stvworlds.stvvelocity.misc;

import com.velocitypowered.api.proxy.Player;
import org.slf4j.Logger;
import win.stvworlds.stvvelocity.config.ConfigManager;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CheckPermit {
    private static final URI HAS_PERMIT_ENDPOINT = URI.create("https://api.stvworlds.win/user/hasPermit");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    private static final Map<UUID, Instant> permitCache = new ConcurrentHashMap<>();

    private CheckPermit() {
    }

    public static boolean checkPermit(Player player, ConfigManager configManager, Logger logger) {
        UUID uuid = player.getUniqueId();

        Instant cached = permitCache.get(uuid);
        if (cached != null && Instant.now().isBefore(cached)) {
            return true;
        }

        String encodedUuid = URLEncoder.encode(uuid.toString(), StandardCharsets.UTF_8);
        String apiKey = configManager.getRoot().node("api-key").getString("api.key");

        if (apiKey.isBlank()) {
            logger.error("API key is not configured (api.key).");
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(HAS_PERMIT_ENDPOINT + "?searchType=UUID_MC&value=" + encodedUuid))
                .header("X-API-KEY", apiKey)
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.error("Permit check failed with HTTP status {}.", response.statusCode());
                return false;
            }

            String body = response.body().trim();
            if (!body.equalsIgnoreCase("true") && !body.equalsIgnoreCase("false")) {
                logger.error("Permit check returned an invalid response: {}", body);
                return false;
            }

            boolean hasPermit = Boolean.parseBoolean(body);
            if (hasPermit) {
                permitCache.put(uuid, Instant.now().plus(CACHE_TTL));
            }
            return hasPermit;
        } catch (IOException e) {
            logger.error("Could not check player permit: {}", e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
