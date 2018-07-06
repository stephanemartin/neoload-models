package com.neotys.neoload.model.readers.loadrunner;

import static org.junit.Assert.assertEquals;

import java.util.Base64;

import com.neotys.neoload.model.listener.TestEventListener;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.neotys.neoload.model.repository.ImmutableParameter;
import com.neotys.neoload.model.repository.ImmutablePage;
import com.neotys.neoload.model.repository.ImmutablePostTextRequest;
import com.neotys.neoload.model.repository.ImmutableServer;
import com.neotys.neoload.model.repository.Page;
import com.neotys.neoload.model.repository.Parameter;
import com.neotys.neoload.model.repository.PostBinaryRequest;
import com.neotys.neoload.model.repository.PostRequest;
import com.neotys.neoload.model.repository.Request;
import com.neotys.neoload.model.repository.Request.HttpMethod;
import com.neotys.neoload.model.repository.Server;

public class WebCustomRequestTest {
	
	public static final MethodCall WEB_CUSTOM_DATA_TEST = ImmutableMethodCall.builder()
			.name("\"test_web_custom_data\"")
			.addParameters("\"test_web_custom_data\"")
			.addParameters("\"URL=https://server.test.com/test/path?ArgWithValue2=value%204\"")
			.addParameters("\"Method=POST\"")
			.addParameters("\"Resource=0\"")
			.addParameters("\"RecContentType=application/json\"")
			.addParameters("\"Referer=referer_test\"")
			.addParameters("\"Snapshot=tX.inf\"")
			.addParameters("\"Mode=HTML\"")
			.addParameters("ITEMDATA")
			.addParameters("\"EncType=application/x-www-form-urlencoded;charset=utf-8\"")
			//la chaine suivante signifie : "texte a convertir en binaire"
			//en hexa cela donne : 7465787465206120636f6e76657274697220656e2062696e61697265
			//en byte[] on a alors : ????
			//base64 generated by neoload : <![CDATA[dGV4dGUgYSBjb252ZXJ0aXIgZW4gYmluYWlyZQ==]]>
			.addParameters("\"BodyBinary=\\\\x74\\\\x65\\\\x78\\\\x74\\\\x65\\\\x20\\\\x61 convertir en bin\\\\x61\\\\x69re\"")
			//.addParameters("\"BodyBinary=\\\\x74\\\\x65\\\\x78\\\\x74\\\\x65\\\\x20\\\\x61 convertir en binaire\"")
			.addParameters("LAST")
			.build();
	
	public static final MethodCall WEB_CUSTOM_DATA_TEST2 = ImmutableMethodCall.builder()
			.name("\"test_web_custom_data\"")
			.addParameters("\"test_web_custom_data\"")
			.addParameters("\"URL=https://server.test.com/test/path?ArgWithValue2=value%204\"")
			.addParameters("\"Method=POST\"")
			.addParameters("\"Resource=0\"")
			.addParameters("\"RecContentType=application/json\"")
			.addParameters("\"Referer=referer_test\"")
			.addParameters("\"Snapshot=tX.inf\"")
			.addParameters("\"Mode=HTML\"")
			.addParameters("ITEMDATA")
			.addParameters("\"EncType=application/x-www-form-urlencoded;charset=utf-8\"")
			//la chaine suivante signifie : "texte a convertir en binaire"
			//en hexa cela donne : 7465787465206120636f6e76657274697220656e2062696e61697265
			//en byte[] on a alors : ????
			//base64 generated by neoload : <![CDATA[dGV4dGUgYSBjb252ZXJ0aXIgZW4gYmluYWlyZQ==]]>
			.addParameters("\"Body=Texte du body à tester\"")
			.addParameters("LAST")
			.build();
	
	public static final Server SERVER_TEST = ImmutableServer.builder()
            .name("server.test.com")
            .host("server.test.com")
            .port("443")
            .scheme("https")
            .build();
	
	public static final Parameter PARAM_TEST_3 = ImmutableParameter.builder()
			.name("ArgWithValue2")
			.value("value 4")
			.build();
	
	public static final PostRequest REQUEST_TEST = ImmutablePostTextRequest.builder()
            .name("/test/path")
            .path("/test/path")
            .server(SERVER_TEST)
            .contentType("application/x-www-form-urlencoded;charset=utf-8")
            .httpMethod(HttpMethod.POST)
            .addParameters(PARAM_TEST_3)
            .data("Texte du body à tester")
            .build();

	public static final Page PAGE_TEST = ImmutablePage.builder()
			.addChilds(REQUEST_TEST)
			.thinkTime(0)
			.name("test_web_custom_data")
			.build();
	
	/*@Test
	public void buildPostParamsFromExtractTest() {
		List<Parameter> expectedResult = new ArrayList<>();
		expectedResult.add(PARAM_TEST_1);
		expectedResult.add(PARAM_TEST_2);
		List<String> input = new ArrayList<>();
		input.add("Name=param1");
		input.add("Value=value1");
		input.add("ENDITEM");
		input.add("Name=param 2");
		input.add("Value=Value 2");
		input.add("ENDITEM");
		List<Parameter> generatedResult = WebSubmitData.buildPostParamsFromExtract(input);
		
		assertEquals(expectedResult, generatedResult);
	}*/

	@Test
	public void binaryDataTest() {
		final LoadRunnerReader reader = new LoadRunnerReader(new TestEventListener(), "", "");
		final ImmutablePage pageGenerated = (ImmutablePage) WebCustomRequest.toElement(reader,"{", "}",WEB_CUSTOM_DATA_TEST, null, null);
		assertEquals("dGV4dGUgYSBjb252ZXJ0aXIgZW4gYmluYWlyZQ==", Base64.getEncoder().encodeToString(((PostBinaryRequest)pageGenerated.getChilds().get(0)).getBinaryData()));
	}
	
	@Test
	public void toElementTest() {
		final LoadRunnerReader reader = new LoadRunnerReader(new TestEventListener(), "", "");
		ImmutablePage pageGenerated = (ImmutablePage) WebCustomRequest.toElement(reader,"{", "}",WEB_CUSTOM_DATA_TEST2, null, null);

		final Request requestGenerated = (Request) pageGenerated.getChilds().get(0);
		pageGenerated = pageGenerated.withChilds(ImmutableList.of(requestGenerated));
		
		assertEquals(PAGE_TEST, pageGenerated);
	}
}
