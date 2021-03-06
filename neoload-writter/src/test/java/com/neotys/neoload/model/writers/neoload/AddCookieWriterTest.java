package com.neotys.neoload.model.writers.neoload;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.io.Files;
import com.neotys.neoload.model.repository.AddCookie;
import com.neotys.neoload.model.repository.ImmutableAddCookie;
public class AddCookieWriterTest {

	@Test
	public void writeAddCookieXmlTest() throws ParserConfigurationException, TransformerException {
		Document doc = WrittingTestUtils.generateEmptyDocument();
		Element root = WrittingTestUtils.generateTestRootElement(doc);

		final AddCookie addCookie = ImmutableAddCookie.builder().name("setCookieForServer cookieName").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").expires("Thu, 2 Aug 2007 20:47:11 UTC").path("cookiePath").build();

		AddCookieWriter.of(addCookie).writeXML(doc, root, "setCookieForServer cookieName", Files.createTempDir().getAbsolutePath());
		String generatedResult = WrittingTestUtils.getXmlString(doc);
		final String timestamp = generatedResult.substring(generatedResult.indexOf("ts=") + 4, generatedResult.indexOf("ts=") + 17);
		final String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
				+ "<test-root><js-action filename=\"scripts/jsAction_8683a380b497cad6ceaa8d0032953c4009beb719df7cff5b04672326641ef361.js\" "
				+ "name=\"setCookieForServer cookieName\" ts=\"" + timestamp + "\" "
				+ "uid=\"8683a380b497cad6ceaa8d0032953c4009beb719df7cff5b04672326641ef361\"/></test-root>";

		Assertions.assertThat(generatedResult).isEqualTo(expectedResult);
	}
		
	@Test
	public void testbuildCookieNameValueDomain() {
		final AddCookie addCookie = ImmutableAddCookie.builder().name("name").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").build();
		final String expectedResult = "cookieName=cookieValue";
		Assertions.assertThat(AddCookieWriter.buildCookie(addCookie)).isEqualTo(expectedResult);
	}
	
	@Test
	public void testbuildCookieNameValueDomainExpires() {
		final AddCookie addCookie = ImmutableAddCookie.builder().name("name").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").expires("Thu, 2 Aug 2007 20:47:11 UTC").build();
		final String expectedResult = "cookieName=cookieValue; expires=Thu, 2 Aug 2007 20:47:11 UTC";
		Assertions.assertThat(AddCookieWriter.buildCookie(addCookie)).isEqualTo(expectedResult);
	}
	
	@Test
	public void testbuildCookieNameValueDomainPath() {
		final AddCookie addCookie = ImmutableAddCookie.builder().name("name").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").path("cookiePath").build();
		final String expectedResult = "cookieName=cookieValue; path=cookiePath";
		Assertions.assertThat(AddCookieWriter.buildCookie(addCookie)).isEqualTo(expectedResult);
	}
		
	@Test
	public void testbuildCookieNameValueDomainExpiresPath() {
		final AddCookie addCookie = ImmutableAddCookie.builder().name("name").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").expires("Thu, 2 Aug 2007 20:47:11 UTC").path("cookiePath").build();
		final String expectedResult = "cookieName=cookieValue; expires=Thu, 2 Aug 2007 20:47:11 UTC; path=cookiePath";
		Assertions.assertThat(AddCookieWriter.buildCookie(addCookie)).isEqualTo(expectedResult);
	}
	
	@Test
	public void testbuildJavascript() {
		final AddCookie addCookie = ImmutableAddCookie.builder().name("name").cookieName("cookieName").cookieValue("cookieValue").domain("cookieDomain").expires("Thu, 2 Aug 2007 20:47:11 UTC").path("cookiePath").build();
		final String expectedResult = "// Add cookie to a HTTP server.\ncontext.currentVU.setCookieForServer(\"cookieDomain\",\"cookieName=cookieValue; expires=Thu, 2 Aug 2007 20:47:11 UTC; path=cookiePath\");";
		Assertions.assertThat(AddCookieWriter.buildJavascript(addCookie)).isEqualTo(expectedResult);
	}
}
