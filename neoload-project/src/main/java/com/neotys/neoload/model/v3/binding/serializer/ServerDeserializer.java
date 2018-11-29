package com.neotys.neoload.model.v3.binding.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.neotys.neoload.model.v3.project.server.Authentication;
import com.neotys.neoload.model.v3.project.server.BasicAuthentication;
import com.neotys.neoload.model.v3.project.server.NegociateAuthentication;
import com.neotys.neoload.model.v3.project.server.NtlmAuthentication;
import com.neotys.neoload.model.v3.project.server.Server;

import java.io.IOException;
import java.util.Optional;

import static com.neotys.neoload.model.v3.project.server.LoginPasswordAuthentication.BASIC_AUTHENTICATION;
import static com.neotys.neoload.model.v3.project.server.LoginPasswordAuthentication.NEGOCIATE_AUTHENTICATION;
import static com.neotys.neoload.model.v3.project.server.LoginPasswordAuthentication.NTLM_AUTHENTICATION;
import static com.neotys.neoload.model.v3.project.server.Server.DEFAULT_PORT;
import static com.neotys.neoload.model.v3.project.server.Server.HOST;
import static com.neotys.neoload.model.v3.project.server.Server.NAME;
import static com.neotys.neoload.model.v3.project.server.Server.PORT;
import static com.neotys.neoload.model.v3.project.server.Server.SCHEME;

public class ServerDeserializer extends StdDeserializer<Server> {
	private static final long serialVersionUID = 3661407425897246832L;

	protected ServerDeserializer() {
		super(Server.class);
	}

	@Override
	public Server deserialize(final JsonParser jsonParser, final DeserializationContext context) throws IOException {
		final ObjectCodec codec = jsonParser.getCodec();
		final JsonNode jsonNode = codec.readTree(jsonParser);

		final String name = jsonNode.get(NAME).asText();
		final Server.Scheme scheme = getScheme(jsonNode);
		final String host = jsonNode.get(HOST).asText();
		final long port = getPort(jsonNode);

		return Server.builder()
				.name(name)
				.scheme(scheme)
				.host(host)
				.port(port)
				.authentication(getAuthentication(codec, jsonNode))
				.build();
	}

	private long getPort(final JsonNode jsonNode) {
		final JsonNode jsonNodePort = jsonNode.get(PORT);
		if (jsonNodePort != null) {
			return jsonNodePort.asLong();
		}
		return DEFAULT_PORT;
	}

	private Server.Scheme getScheme(final JsonNode jsonNode) {
		Server.Scheme scheme = Server.Scheme.HTTP;
		final JsonNode jsonScheme = jsonNode.get(SCHEME);
		if (jsonScheme != null) {
			try {
				scheme = Server.Scheme.valueOf(jsonScheme.asText().toUpperCase());
			} catch (IllegalArgumentException e) {
				//log
			}
		}
		return scheme;
	}

	private Optional<Authentication> getAuthentication(final ObjectCodec objectCodec, final JsonNode jsonNode) throws JsonProcessingException {
		JsonNode authentication = jsonNode.get(BASIC_AUTHENTICATION);
		if (authentication != null) {
			return Optional.of(objectCodec.treeToValue(authentication, BasicAuthentication.class));
		}

		authentication = jsonNode.get(NTLM_AUTHENTICATION);
		if (authentication != null) {
			return Optional.of(objectCodec.treeToValue(authentication, NtlmAuthentication.class));
		}

		authentication = jsonNode.get(NEGOCIATE_AUTHENTICATION);
		if (authentication != null) {
			return Optional.of(objectCodec.treeToValue(authentication, NegociateAuthentication.class));
		}

		return Optional.empty();
	}
}