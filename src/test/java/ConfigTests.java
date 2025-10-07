import org.R4ZXRN3T.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTests {

	@BeforeEach
	void setUp() {
		// Reset the configList between tests
		Config.resetForTesting();
	}

	@Test
	void testSetAndGetConfig() {
		Config.setConfig(Config.ConfigKey.PASSWORD, "testPassword");
		assertEquals("testPassword", Config.getConfig(Config.ConfigKey.PASSWORD));
	}

	@Test
	void testGetConfigWithNonExistentKey() {
		assertNull(Config.getConfig(Config.ConfigKey.PASSWORD));
	}

	@Test
	void testConfigFilePathForCurrentOS() {
		Path configPath = Config.getConfigFilePath();
		assertNotNull(configPath);
		assertTrue(configPath.toString().endsWith("config.json") ||
				configPath.toString().endsWith("Firstpass/config.json"));
	}
}