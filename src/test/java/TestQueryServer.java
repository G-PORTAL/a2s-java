import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.gportal.source.query.PlayerInfo;
import com.gportal.source.query.QueryClient;
import com.gportal.source.query.QueryServer;
import com.gportal.source.query.ServerInfo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestQueryServer {
	private static QueryClient client;
	private static QueryServer server;

	@BeforeAll
	public static void initClientAndServer() {
		client = new QueryClient();
		server = new QueryServer(27017, new ServerInfo(null, (byte)17, "ExampleServer", "ExampleMap", "ExampleFolder", "ExampleGame", (short)0, (byte)4, (byte)10, (byte)0, 'd', 'l', false, true, "1.0.0.0", null, null, null, null, null, null));
		server.rules.put("Hello", "World");
		server.players.add(new PlayerInfo((byte)0, "ExamplePlayer", (short)500, 60f));
	}

	@Test
	public void testQueryServer() throws UnknownHostException, InterruptedException, ExecutionException, TimeoutException {
		System.out.println(client.queryServer(new InetSocketAddress(InetAddress.getLocalHost(), 27017)).get(5000, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testQueryPlayers() throws UnknownHostException, InterruptedException, ExecutionException, TimeoutException {
		System.out.println(client.queryPlayers(new InetSocketAddress(InetAddress.getLocalHost(), 27017)).get(5000, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testQueryRules() throws UnknownHostException, InterruptedException, ExecutionException, TimeoutException {
		System.out.println(client.queryRules(new InetSocketAddress(InetAddress.getLocalHost(), 27017)).get(5000, TimeUnit.MILLISECONDS));
	}

	@AfterAll
	public static void cleanUp() {
		client.shutdown();
		server.shutdown();
	}
}
