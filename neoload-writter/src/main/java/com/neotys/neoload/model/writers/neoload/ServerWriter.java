package com.neotys.neoload.model.writers.neoload;
import org.w3c.dom.Document;
import com.neotys.neoload.model.repository.Server;

public class ServerWriter {

	public static final String XML_TAG_NAME = "http-server";
    public static final String XML_ATTRIBUTE_NAME = "name";
	public static final String XML_ATTRIBUTE_PORT = "port";
	public static final String XML_ATTRIBUTE_HOST = "hostname";
	public static final String XML_ATTRIBUTE_SSL = "ssl";
    public static final String XML_ATTRIBUTE_UID = "uid";

	private final Server server;

	public ServerWriter(Server server) {
	    this.server = server;
	}

	public static ServerWriter of(Server serv) {
        return new ServerWriter(serv);
    }

    public void writeXML(final Document document, final org.w3c.dom.Element currentElement) {
    	org.w3c.dom.Element xmlServer = document.createElement(XML_TAG_NAME);
        xmlServer.setAttribute(XML_ATTRIBUTE_NAME, server.getName());
        xmlServer.setAttribute(XML_ATTRIBUTE_HOST, server.getHost());
        xmlServer.setAttribute(XML_ATTRIBUTE_PORT, server.getPort());
        xmlServer.setAttribute(XML_ATTRIBUTE_SSL, String.valueOf("https".equals(server.getScheme().orElse("http"))));
        xmlServer.setAttribute(XML_ATTRIBUTE_UID, server.getName());
        currentElement.appendChild(xmlServer);
    }
}
