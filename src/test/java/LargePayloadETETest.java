import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.gportal.a2s.PlayerInfo;
import com.gportal.a2s.QueryClient;
import com.gportal.a2s.QueryServer;
import com.gportal.a2s.ServerInfo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LargePayloadETETest {
	private static QueryClient client;
	private static QueryServer server;
	private static final int PORT = 27016;

	@BeforeAll
	public static void init() throws Exception {
		client = new QueryClient();
		
		// Create a very long server name (2000 chars)
		StringBuilder longName = new StringBuilder();
		for (int i = 0; i < 2000; i++) {
			longName.append("A");
		}

		ServerInfo info = new ServerInfo(
			new InetSocketAddress(InetAddress.getLocalHost(), PORT),
			(byte) 17,
			longName.toString(),
			"Map",
			"Folder",
			"Game",
			(short) 0,
			(byte) 0, // players - will be updated by server.players.size() if it was dynamic, but here it's static in ServerInfo
			(byte) 255,
			(byte) 0,
			'd',
			'l',
			false,
			true,
			"1.0.0.0",
			null, null, null, null, null, null
		);

		server = new QueryServer(new InetSocketAddress(InetAddress.getLocalHost(), PORT), info);

		// Add 200 players to exceed typical MTU/buffer limits
		for (int i = 0; i < 200; i++) {
			server.players.add(new PlayerInfo((byte) i, "Player " + i, i * 10, 100.0f));
		}
		
		// Update player count in info to reflect actual players
		server.info.setPlayers((byte) 200);

		// Add many rules
		for (int i = 0; i < 200; i++) {
			server.rules.put("RuleKey" + i, "RuleValue" + i);
		}
	}

	@Test
	public void testLargeInfoReply() throws Exception {
		ServerInfo info = client.queryServer(new InetSocketAddress(InetAddress.getLocalHost(), PORT)).get(5, TimeUnit.SECONDS);
		assertNotNull(info);
		assertEquals(2000, info.name().length());
	}

	@Test
	public void testLargePlayerReply() throws Exception {
		List<PlayerInfo> players = client.queryPlayers(new InetSocketAddress(InetAddress.getLocalHost(), PORT)).get(5, TimeUnit.SECONDS);
		assertNotNull(players);
		assertEquals(200, players.size());
		assertEquals("Player 199", players.get(199).name());
	}

	@Test
	public void testLargeRulesReply() throws Exception {
		Map<String, String> rules = client.queryRules(new InetSocketAddress(InetAddress.getLocalHost(), PORT)).get(5, TimeUnit.SECONDS);
		assertNotNull(rules);
		assertEquals(200, rules.size());
		assertEquals("RuleValue199", rules.get("RuleKey199"));
	}

	@AfterAll
	public static void cleanup() {
		if (client != null) client.shutdown();
		if (server != null) server.shutdown();
	}
}
