# a2s-java

A Valve Steam Query Protocol (A2S) implementation for Java using Netty.

This library allows you to query game servers that implement the Source Engine Query protocol, such as Counter-Strike, Team Fortress 2, Rust, ARK, and many others.

> [!NOTE]  
> This code is based on and used from [yeetus-desastroesus/A2S-Java](https://github.com/yeetus-desastroesus/A2S-Java).

## Features

- **A2S_INFO**: Get server information (name, map, player count, etc.).
- **A2S_PLAYER**: Get detailed list of players currently on the server.
- **A2S_RULES**: Get server rules/CVars.
- **Asynchronous**: Built on Netty for high-performance, non-blocking I/O.
- **Server Implementation**: Includes a basic A2S server implementation for testing or mocking.

## Installation

The library is published on the Maven registry. You can include it in your project using Maven or Gradle.

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.gportal</groupId>
    <artifactId>a2s</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```gradle
dependencies {
    implementation 'com.gportal:a2s:<version>'
}
```

## Usage

### Querying a Server (Client)

```java
import com.gportal.source.query.QueryClient;
import com.gportal.source.query.ServerInfo;
import com.gportal.source.query.PlayerInfo;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Example {
    public static void main(String[] args) throws Exception {
        QueryClient client = new QueryClient();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 27015);

        // Query Server Info
        CompletableFuture<ServerInfo> infoFuture = client.queryServer(address);
        infoFuture.thenAccept(info -> System.out.println("Server Name: " + info.name()));

        // Query Players
        CompletableFuture<List<PlayerInfo>> playersFuture = client.queryPlayers(address);
        playersFuture.thenAccept(players -> players.forEach(p -> System.out.println("Player: " + p.name())));

        // Query Rules
        CompletableFuture<Map<String, String>> rulesFuture = client.queryRules(address);
        rulesFuture.thenAccept(rules -> System.out.println("Rules count: " + rules.size()));

        // Don't forget to shutdown when done
        // client.shutdown();
    }
}
```

### Starting an A2S Server

```java
import com.gportal.source.query.QueryServer;
import com.gportal.source.query.ServerInfo;
import com.gportal.source.query.PlayerInfo;

public class ServerExample {
    public static void main(String[] args) {
        ServerInfo info = new ServerInfo(
            null, (byte)17, "My Java Game Server", "de_dust2", "csgo", "Counter-Strike: Global Offensive",
            (short)730, (byte)0, (byte)20, (byte)0, 'd', 'l', false, true, "1.0.0.0",
            null, null, null, null, null, null
        );

        QueryServer server = new QueryServer(27015, info);
        
        // Add some players or rules
        server.players.add(new PlayerInfo((byte)0, "Gordon Freeman", (short)100, 300.0f));
        server.rules.put("mp_timelimit", "30");

        System.out.println("A2S Server started on port 27015");
    }
}
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
