import java.time.Duration;
import java.util.*;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class atonce10users5mins extends Simulation {

        private int usersPerSecond = 5;
        private int totalUsers = 5;

        public static void main(String[] args) {
                GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                                .simulationClass("perfproxy.perflogin"); // Replace with your actual simulation class
                Gatling.fromMap(props.build());

        }

        {

                FeederBuilder<String> loginFeeder = csv(
                                "D:\\Gatling\\gatling-maven-plugin-demo-java-main\\gatling-maven-plugin-demo-java-main\\src\\test\\resources\\userDetails.csv")
                                .circular();

                HttpProtocolBuilder httpProtocol = http
                                .baseUrl("https://perfproxy.cnx.cwp.pnp-hcl.com")
                                .inferHtmlResources(AllowList(),
                                                DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg",
                                                                ".*\\.ico", ".*\\.woff",
                                                                ".*\\.woff2",
                                                                ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg",
                                                                ".*detectportal\\.firefox\\.com.*", ".*\\.js",
                                                                ".*\\.css",
                                                                ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico",
                                                                ".*\\.woff", ".*\\.woff2",
                                                                ".*\\.(t|o)tf", ".*\\.png",
                                                                ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
                                .userAgentHeader(
                                                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

                ScenarioBuilder scn = scenario("perflogin")
                                .feed(loginFeeder)
                                .exec(
                                                http("Login")
                                                                .get("/homepage/login/")
                                                                .check(status().is(200))

                                )
                                .pause(7)
                                .exec(
                                                http("submit")
                                                                .post("/homepage/j_security_check")

                                                                .formParam("service.name", "")
                                                                .formParam("secure", "")
                                                                .formParam("fragment", "")
                                                                .formParam("j_username", "$(name)")
                                                                .formParam("j_password", "$(password)")

                                                                .body(
                                                                                StringBody("{\"j_username\": \"#{name}\",\"j_password\": \"#{password}\""))

                                                                .check(status().is(200))

                                )

                                .exec(
                                                http("Homepage")
                                                                .get("/homepage/web/updates/")
                                                                .check(status().is(200)))
                                .exec(
                                                http("Communities")
                                                                .get("/communities/service/html/allmycommunities")
                                                                .check(status().is(200)))

                                .exec(
                                                http("Files")
                                                                .get("/files/app#")
                                                                .check(status().is(200)))

                                .exec(
                                                http("Wikis")
                                                                .get("/wikis/home")
                                                                .check(status().is(200)))

                                .exec(
                                                http("Forums")
                                                                .get("/forums/html/my")
                                                                .check(status().is(200)))

                                .exec(
                                                http("Profiles")
                                                                .get("/profiles/html/networkView.do?widgetId=friends")
                                                                .check(status().is(200)))

                                .exec(
                                                http("Blogs")
                                                                .get("/blogs")
                                                                .check(status().is(200)))

                                .pause(6)
                                .exec(
                                                http("Logout")
                                                                .get("/homepage/ibm_security_logout?logoutExitPage=%2F")

                                );

                setUp(
                                scn.injectOpen(
                                                atOnceUsers(10) // All 50 users ramp up at once
                                )).protocols(httpProtocol)
                                .maxDuration(Duration.ofMinutes(5)); // Simulation runs for 30 minutes

                // setUp(scn.injectClosed(constantConcurrentUsers(10).during(Duration.ofMinutes(5)))
                // .protocols(httpProtocol));

        }
}
