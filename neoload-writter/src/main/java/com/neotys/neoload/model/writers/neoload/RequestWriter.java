package com.neotys.neoload.model.writers.neoload;

import com.neotys.neoload.model.repository.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.neotys.neoload.model.writers.neoload.NeoLoadWriter.RECORDED_REQUESTS_FOLDER;
import static com.neotys.neoload.model.writers.neoload.NeoLoadWriter.RECORDED_RESPONSE_FOLDER;

public class RequestWriter extends ElementWriter {

	private static Logger LOG = LoggerFactory.getLogger(RequestWriter.class);

	public static final String XML_TAG_NAME = "http-action";
	public static final String XML_ATTR_METHOD = "method";
	public static final String XML_ATTR_ACTION_TYPE = "actionType";
	public static final String XML_ATTR_SERV_UID = "serverUid";
	public static final String XML_ATTR_PATH = "path";
	public static final String XML_ATTR_ASSERT_BLOC = "assertions";

    private static final String XML_TAG_RECORDED_REQUEST = "requestContentFileDescription";
    private static final String XML_TAG_RECORDED_RESPONSE = "responsePageFileDescription";
    private static final String XML_TAG_REQUEST_HEADER = "header";
    private static final String XML_TAG_RESPONSE_HEADERS = "responseHeaders";

	private static final String DEFAULT_ACTION_TYPE = "1";

	public RequestWriter(Request request) {
		super(request);
	}

	@Override
	public void writeXML(final Document document, final Element currentElement, final String parentPath, final String outputFolder) {
		Element xmlRequest = document.createElement(XML_TAG_NAME);
		Request request = (Request) this.element;
		super.writeXML(document, xmlRequest, parentPath, outputFolder);

		xmlRequest.setAttribute(XML_ATTR_METHOD, request.getHttpMethod().toString());
		request.getServer().ifPresent(server -> xmlRequest.setAttribute(XML_ATTR_SERV_UID, server.getName()));
		xmlRequest.setAttribute(XML_ATTR_ACTION_TYPE, DEFAULT_ACTION_TYPE);
		request.getPath().ifPresent(path -> xmlRequest.setAttribute(XML_ATTR_PATH, path));

		request.getExtractors().forEach(extractElem -> ExtractorWriter.of(extractElem).writeXML(document, xmlRequest));
		writeValidationSection(request, document, xmlRequest);
		writeParameters(request, document, xmlRequest);
		request.getHeaders().forEach(header -> HeaderWriter.writeXML(document, xmlRequest, header));

        writeRecordedFiles(request, document, xmlRequest);

		currentElement.appendChild(xmlRequest);
	}

    private void writeValidationSection(final Request request, final Document document, Element xmlRequest) {
		if (request.getValidators().isEmpty())
			return;

		Element xmlAssertBloc = document.createElement(XML_ATTR_ASSERT_BLOC);
		request.getValidators().forEach(validElem -> ValidatorWriter.getWriterFor(validElem).writeXML(document, xmlAssertBloc));

		xmlRequest.appendChild(xmlAssertBloc);
	}

	public void writeParameters(final Request request, final Document document, Element xmlRequest) {
		request.getParameters().forEach(paramElem -> ParameterWriter.of(paramElem).writeXML(document, xmlRequest, Optional.empty()));
	}

    private void writeRecordedFiles(Request request, Document document, Element xmlRequest) {
        request.getRecordedFiles().ifPresent(recordedFiles -> {
            //Request header
            if (!isNullOrEmpty(recordedFiles.recordedRequestHeaderFile())) {
				writeRecordedRequestHeaders(recordedFiles.recordedRequestHeaderFile(), document, xmlRequest);
            }

            //Request body
            if (!isNullOrEmpty(recordedFiles.recordedRequestBodyFile())) {
				final Element element = document.createElement(XML_TAG_RECORDED_REQUEST);
				final Path recordedRequestBodyPath = Paths.get(recordedFiles.recordedRequestBodyFile());
				final String fileName = "req_" + recordedRequestBodyPath.getFileName().toString();
				element.setTextContent(RECORDED_REQUESTS_FOLDER + "/" + fileName);
				xmlRequest.appendChild(element);

				//FIXME Copy files "lrProjectFolder/data/t22_RequestHeader.htm" and "lrProjectFolder/data/t22_RequestBody.htm"
				//to "nlProjectFolder/recorded-responses/t22.htm"
            }

            //Response header
            if (!isNullOrEmpty(recordedFiles.recordedResponseHeaderFile())) {
				writeRecordedResponseHeaders(recordedFiles.recordedResponseHeaderFile(), document, xmlRequest);
            }

            //Response body
            if (!isNullOrEmpty(recordedFiles.recordedResponseBodyFile())) {
				final Element element = document.createElement(XML_TAG_RECORDED_RESPONSE);
				final Path recordedResponseBodyPath = Paths.get(recordedFiles.recordedResponseBodyFile());
				final String fileName = "res_" + recordedResponseBodyPath.getFileName().toString();
				element.setTextContent(RECORDED_RESPONSE_FOLDER + "/" + fileName);
				xmlRequest.appendChild(element);

				//FIXME Copy file "lrProjectFolder/data/t22.htm"
				//to "nlProjectFolder/recorded-responses/t22.htm"
            }
        });
    }

	private void writeRecordedResponseHeaders(String recordedResponseHeaderFile, Document document, Element xmlRequest) {
		try {
			final String responseHeaders = new String(Files.readAllBytes(Paths.get(recordedResponseHeaderFile)), "UTF-8");
			final Element element = document.createElement(XML_TAG_RESPONSE_HEADERS);
			element.setTextContent(responseHeaders);
			xmlRequest.appendChild(element);
		} catch (IOException e) {
			LOG.error("Can not write recorded response headers", e);
		}
	}

	private void writeRecordedRequestHeaders(String recordedRequestHeaderFile, Document document, Element xmlRequest) {
		try {
			final Properties properties = new Properties();
			properties.load(Files.newInputStream(Paths.get(recordedRequestHeaderFile)));
			properties.forEach((key, value) -> {
				if (key instanceof String && value instanceof String) {
					final Element element = document.createElement(XML_TAG_REQUEST_HEADER);
					element.setAttribute("name", (String) key);
					element.setAttribute("value", (String) value);
					xmlRequest.appendChild(element);
				}
			});
		} catch (IOException e) {
			LOG.error("Can not write recorded request headers", e);
		}
	}
}
