import org.R4ZXRN3T.firstpass.Tools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolsTests {

	@Test
	void testPasswordEncoding() {
		String password = "testPassword";

		// Argon2id hashes are non-deterministic (each call embeds a new random salt),
		// so we verify via verifyPassword rather than equality.
		String encoded = Tools.encodePassword(password);
		assertTrue(encoded.startsWith("$argon2id$"), "Hash should be an Argon2id encoded string");

		// Correct password + salt should verify successfully
		assertTrue(Tools.verifyPassword(password, encoded));

		// Different password should not verify
		assertFalse(Tools.verifyPassword("differentPassword", encoded));
	}

	@Test
	void testRandomStringGeneration() {

		// Test with custom character set
		String customSet = "ABC123";
		String random2 = Tools.generateRandomString(15, customSet);
		assertEquals(15, random2.length());

		// Verify characters are from the custom set
		for (char c : random2.toCharArray()) {
			assertTrue(customSet.indexOf(c) >= 0);
		}
	}

	@Test
	void testXmlValidation() {
		String input = "Test & <example> with \"quotes\" and 'apostrophes'";
		String expected = "Test &amp; &lt;example&gt; with &quot;quotes&quot; and &apos;apostrophes&apos;";

		String validated = Tools.validateForXML(input);
		assertEquals(expected, validated);

		// Test round-trip conversion
		String original = Tools.returnOriginalValue(validated);
		assertEquals(input, original);
	}
}