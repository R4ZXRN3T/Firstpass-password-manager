import org.R4ZXRN3T.firstpass.Tools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolsTests {

	@Test
	void testPasswordEncoding() {
		String password = "testPassword";
		String salt = "testSalt";

		// Test password encoding produces consistent results
		String encoded1 = Tools.encodePassword(password, salt);
		String encoded2 = Tools.encodePassword(password, salt);
		assertEquals(encoded1, encoded2);

		// Different passwords should produce different encodings
		String otherPassword = "differentPassword";
		String encodedOther = Tools.encodePassword(otherPassword, salt);
		assertNotEquals(encoded1, encodedOther);

		// Same password, different salt should produce different encodings
		String otherSalt = "differentSalt";
		String encodedOtherSalt = Tools.encodePassword(password, otherSalt);
		assertNotEquals(encoded1, encodedOtherSalt);
	}

	@Test
	void testRandomStringGeneration() {
		// Test length
		String random1 = Tools.generateRandomString(10);
		assertEquals(10, random1.length());

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