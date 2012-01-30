package mtg;

import com.sun.grizzly.http.embed.GrizzlyWebServer;
import com.sun.grizzly.http.servlet.ServletAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyAdapter;
import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        BasicConfigurator.configure();

        GrizzlyWebServer ws = new GrizzlyWebServer(19998, "src/main/webapp");

        Logger.getRootLogger().setLevel(Level.WARN);
        Logger.getLogger("mtg").setLevel(Level.DEBUG);
        LoggerFactory.getLogger("mtg").info("Go!");

        // Jersey web resources
        ServletAdapter jerseyAdapter = new ServletAdapter();
        jerseyAdapter.setContextPath("/api");
        jerseyAdapter.addInitParameter("com.sun.jersey.config.property.packages", "mtg.rest");
        jerseyAdapter.setServletInstance(new ServletContainer());

        ws.addGrizzlyAdapter(jerseyAdapter, new String[] { "/api" });
        GrizzlyAdapter staticAdapter = new GrizzlyAdapter("src/main/webapp") {
            @Override
            public void service(GrizzlyRequest request, GrizzlyResponse response) throws Exception {

            }
        };
        staticAdapter.setHandleStaticResources(true);
        ws.addGrizzlyAdapter(staticAdapter, new String[] { "/" });
        ws.start();

    }
}