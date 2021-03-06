package com.neotys.neoload.model.writers.neoload;

import com.neotys.neoload.model.repository.*;
import com.neotys.neoload.model.repository.Request.HttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class WrittingTestUtils {
	private WrittingTestUtils() {}
	
	public static final Server SERVER_TEST = ImmutableServer.builder()
            .name("server_test")
            .host("host_test.com")
            .port("8080")
            .scheme("http")
            .build();
	
	public static final ImmutableGetPlainRequest REQUEST_TEST = ImmutableGetPlainRequest.builder()
            .name("request_test")
            .path("/test_path")
            .server(SERVER_TEST)
            .httpMethod(HttpMethod.GET)
            .build();
	
	public static final Parameter PARAMETER_TEST = ImmutableParameter.builder()
			.name("param_name")
			.value("param_value")
			.build();
	
	public static final Parameter POST_PARAMETER_TEST = ImmutableParameter.builder()
			.name("post param_name")
			.value("post_param Value")
			.build(); 
	
	public static final GetRequest REQUEST_TEST2 = ImmutableGetPlainRequest.builder()
            .name("request_test")
            .path("/test_path")
            .server(SERVER_TEST)
            .httpMethod(HttpMethod.GET)
            .addParameters(PARAMETER_TEST)
            .build();
	
	public static final PostFormRequest REQUEST_TEST3 = ImmutablePostFormRequest.builder()
            .name("request_test")
            .path("/test_path")
            .server(SERVER_TEST)
            .httpMethod(HttpMethod.POST)
            .addParameters(PARAMETER_TEST)
            .addPostParameters(POST_PARAMETER_TEST)
            .build();
	
	public static final PostTextRequest REQUEST_TEST4 = ImmutablePostTextRequest.builder()
            .name("request_test")
            .path("/test_path")
            .server(SERVER_TEST)
            .httpMethod(HttpMethod.POST)
            .addParameters(PARAMETER_TEST)
            .data("texte a convertir en binaire")
            .build();

	public static final byte[] BINARY_DATA_TEST = {116, 101, 120, 116, 101, 32, 97, 32, 99, 111, 110, 118, 101, 114, 116, 105, 114, 32, 101, 110, 32, 98, 105, 110, 97, 105, 114, 101};
	
	public static final PostRequest REQUEST_TEST5 = ImmutablePostBinaryRequest.builder()
            .name("request_test")
            .path("/test_path")
            .server(SERVER_TEST)
            .httpMethod(HttpMethod.POST)
            .addParameters(PARAMETER_TEST)
            .binaryData(BINARY_DATA_TEST)
            .build();
	
	public static final Page PAGE_TEST = ImmutablePage.builder()
			.addChilds(WrittingTestUtils.REQUEST_TEST)
			.thinkTime(0)
			.name("page_name")
			.build();
	
	public static final Container CONTAINER_TEST = ImmutableContainer.builder()
			.addChilds(PAGE_TEST)
			.name("Container_name")
			.build();

	private static final List<String> COLUMNS;
	static {
		COLUMNS = new ArrayList<>();
		COLUMNS.add("colonneTest");
	}
	
	public static final FileVariable VARIABLE_TEST = ImmutableFileVariable.builder()
			.name("variable_test")
			.columnsDelimiter(",")
			.fileName("path_du_fichier")
			.numOfFirstRowData(2)
			.order(FileVariable.VariableOrder.SEQUENTIAL)
			.policy(FileVariable.VariablePolicy.EACH_ITERATION)
			.firstLineIsColumnName(true)
			.scope(FileVariable.VariableScope.GLOBAL)
			.columnsNames(COLUMNS)
			.noValuesLeftBehavior(FileVariable.VariableNoValuesLeftBehavior.CYCLE)
			.build();
	
	public static final FileVariable VARIABLE_TEST2 = ImmutableFileVariable.builder()
			.name("variable_test")
			.columnsDelimiter(",")
			.fileName("path_du_fichier")
			.numOfFirstRowData(2)
			.order(FileVariable.VariableOrder.RANDOM)
			.policy(FileVariable.VariablePolicy.EACH_USE)
			.firstLineIsColumnName(true)
			.scope(FileVariable.VariableScope.LOCAL)
			.columnsNames(COLUMNS)
			.noValuesLeftBehavior(FileVariable.VariableNoValuesLeftBehavior.STOP)
			.build();
	
	public static final FileVariable VARIABLE_TEST3 = ImmutableFileVariable.builder()
			.name("variable_test")
			.columnsDelimiter(",")
			.fileName("path_du_fichier")
			.numOfFirstRowData(2)
			.order(FileVariable.VariableOrder.SEQUENTIAL)
			.policy(FileVariable.VariablePolicy.EACH_VUSER)
			.firstLineIsColumnName(true)
			.scope(FileVariable.VariableScope.UNIQUE)
			.columnsNames(COLUMNS)
			.noValuesLeftBehavior(FileVariable.VariableNoValuesLeftBehavior.CYCLE)
			.build();
	
	public static Document generateEmptyDocument() throws ParserConfigurationException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		return docBuilder.newDocument();
	}

	public static Element generateTestRootElement(Document doc) {
		final Element rootElement = doc.createElement("test-root");
        doc.appendChild(rootElement);
		return rootElement;
	}
	
	public static String getXmlString(Document doc) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		
		StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);

		transformer.transform(source, result);

		return writer.toString();
	}
}
