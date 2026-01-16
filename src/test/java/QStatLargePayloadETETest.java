import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gportal.a2s.PlayerInfo;
import com.gportal.a2s.QueryServer;
import com.gportal.a2s.ServerInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class QStatLargePayloadETETest {
	private static QueryServer server;
	private static final int PORT = 27018; // Use a different port to avoid conflicts
	private static final String SERVER_NAME = "A".repeat(2000);

	@BeforeAll
	public static void init() throws Exception {
		ServerInfo info = new ServerInfo(
			new InetSocketAddress(InetAddress.getLocalHost(), PORT),
			(byte) 17, // protocol
			SERVER_NAME,
			"Map",
			"Folder",
			"Game",
			(short) 0,
			(byte) 0,
			(byte) 255,
			(byte) 0, // bots
			'd', // dedicated
			'l', // environment (linux)
			false,
			true, // vac (secure)
			"1.0.0.0",
			null, null, null, null, null, null
		);

		server = new QueryServer(new InetSocketAddress(InetAddress.getLocalHost(), PORT), info);

		// Add 200 players
		for (int i = 0; i < 200; i++) {
			server.players.add(new PlayerInfo((byte) i, "Player " + i, i * 10, 100.0f));
		}
		server.info.setPlayers((byte) 200);

		// Add 200 rules
		for (int i = 0; i < 200; i++) {
			server.rules.put("RuleKey" + i, "RuleValue" + i);
		}
	}

	@Test
	public void testQStatLargePayload() throws Exception {
		String address = "127.0.0.1:" + PORT;
		ProcessBuilder pb = new ProcessBuilder("qstat", "-a2s", address, "-json", "-R");
		Process process = pb.start();

		String output;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			output = reader.lines().collect(Collectors.joining("\n"));
		}

		int exitCode = process.waitFor();
		assertEquals(0, exitCode, "qstat failed with exit code " + exitCode + ". Output: " + output);

		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(output);

		assertTrue(root.isArray(), "Expected JSON array from qstat");
		assertEquals(1, root.size(), "Expected one server in response");

		JsonNode serverNode = root.get(0);
		assertEquals("online", serverNode.get("status").asText());
		assertEquals(SERVER_NAME, serverNode.get("name").asText());
		assertEquals("Map", serverNode.get("map").asText());
		assertEquals("Folder", serverNode.get("gametype").asText());
		assertEquals(200, serverNode.get("numplayers").asInt());
		assertEquals(255, serverNode.get("maxplayers").asInt());

		JsonNode rulesNode = serverNode.get("rules");
		assertNotNull(rulesNode, "Rules should not be null");

		assertEquals("11", rulesNode.get("protocol").asText());
		assertEquals("Folder", rulesNode.get("gamedir").asText());
		assertEquals("Game", rulesNode.get("gamename").asText());
		assertEquals("0", rulesNode.get("bots").asText());
		assertEquals("1", rulesNode.get("dedicated").asText());
		assertEquals("linux", rulesNode.get("sv_os").asText());
		assertEquals("1", rulesNode.get("secure").asText());
		assertEquals("1.0.0.0", rulesNode.get("version").asText());
		assertTrue(rulesNode.size() >= 200, "Expected at least 200 rules, but got " + rulesNode.size());
		assertEquals("RuleValue199", rulesNode.get("RuleKey199").asText());
	}

	@AfterAll
	public static void cleanup() {
		if (server != null) server.shutdown();
	}
}
