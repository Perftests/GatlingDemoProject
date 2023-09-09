import java.time.Duration;
import java.util.*;

import ch.qos.logback.core.net.SyslogOutputStream;
import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.core.controller.inject.open.HeavisideOpenInjection;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class Simulation100users2hrs extends Simulation {

        public static void main(String[] args) {
                GatlingPropertiesBuilder props = new GatlingPropertiesBuilder()
                                .simulationClass("perfproxy.Simulation100users2hrs"); // Replace with your actual
                                                                                      // simulation class
                Gatling.fromMap(props.build());

        }

        {
                FeederBuilder<String> loginFeeder = csv(
                                "D:\\Projects\\GatlingDemoProject\\src\\test\\resources\\userDetails.csv")
                                .circular();
                HttpProtocolBuilder httpProtocol = http
                                .baseUrl("https://perfproxy.cnx.cwp.pnp-hcl.com");
                // .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif",
                // ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2",
                // ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*",
                // ".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico",
                // ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg",
                // ".*detectportal\\.firefox\\.com.*"))
                // .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64)
                // AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

                ScenarioBuilder scn = scenario("perf")
                                .feed(loginFeeder)

                                // Launch PerfProxy URL
                                .exec(
                                                http("Launch URL")
                                                                .get("/homepage/login")
                                                                .disableFollowRedirect()
                                                                .check(status().is(302)))
                                .pause(10)

                                // Add Username and Password
                                .exec(
                                                http("submit")
                                                                .post("/homepage/j_security_check")

                                                                .formParam("service.name", "")
                                                                .formParam("secure", "")
                                                                .formParam("fragment", "")
                                                                .formParam("j_username", "$(name)")
                                                                .formParam("j_password", "$(password)")
                                                                .body(StringBody(
                                                                                "{\"j_username\": \"#{name}\",\"j_password\": \"#{password}\""))
                                                                .check(status().is(302))
                                                                .disableFollowRedirect()

                                )
                                .exec(
                                                http("redirect login")
                                                                .post("/homepage/j_security_check")
                                                                .formParam("service.name", "")
                                                                .formParam("secure", "")
                                                                .formParam("fragment", "")
                                                                // .formParam("j_username", "jjones2")
                                                                // .formParam("j_password", "password")
                                                                .formParam("j_username", "${name}")
                                                                .formParam("j_password", "${password}"))

                                .pause(2)
                                // click on Activity app

                                .exec(
                                                http("On Activities Page")
                                                                .get("/activities")
                                                                .check(status().is(302))
                                                                .disableFollowRedirect())

                                .pause(10)
                                // My Activities
                                .exec(
                                                http("My Activity")
                                                                .get("/activities/service/html/mainpage")
                                                                .check(status().is(200))

                                )

                                .pause(2)
                                // click on Files
                                .exec(
                                                http("Files")
                                                                .get("/files/")
                                                                .check(status().is(200))

                                )
                                .pause(10)
                                .exec(
                                                http("My Files")
                                                                .get("/files/form/api/myuserlibrary/feed?page=1&pageSize=25&sK=modified&sO=dsc&preview=true")
                                                                .check(status().is(200))

                                )

                                .pause(10)
                                // On Communities
                                .exec(
                                                http("All My Communities")
                                                                .get("/communities/service/html/allmycommunities")
                                                                .check(status().is(200))

                                )

                                .pause(10)
                                // I am Owner Communities
                                .exec(
                                                http("Communities I own")
                                                                .get(
                                                                                "/communities/service/atom/forms/catalog/owned?results=10&start=0&sortKey=FIELD_LAST_VISITED_DATE&sortOrder=desc&facet=%7B%22id%22%3A%22tag%22%2C%22count%22%3A%2030%7D&format=XML&userCacheKey=10051994&dojo.preventCache=1408029823506")
                                                                .check(status().is(200))

                                )

                                .pause(10)

                                // Click On Forums:

                                .exec(
                                                http("Forums")
                                                                .get("/forums/html/my?view=following")
                                                                .check(status().is(200))

                                )
                                .pause(10)

                                // Click On Forums ImAn Owner
                                .exec(
                                                http("Forums I own")
                                                                .get("/forums/html/my?view=owner&filter=forums")
                                                                .check(status().is(200))

                                )
                                .pause(10)

                                // Click on Blogs Appp
                                .exec(
                                                http("Blogs")
                                                                .get("/blogs")
                                                                .check(status().is(302))
                                                                .disableFollowRedirect())
                                .pause(10)

                                // Open My Blogs Page
                                .exec(
                                                http("My Blog")
                                                                .get("/blogs/roller-ui/allblogs?email=ajones2%40cnx.pnp-hcl.com&lang=en_us")
                                                                .check(status().is(200)))

                                .pause(10)
                                // Logout
                                .exec(
                                                http("Logout")
                                                                .get("/homepage/ibm_security_logout?logoutExitPage=%2F")
                                                                .check(status().is(302))
                                                                .disableFollowRedirect())
                                .exec(
                                                http("Logout _2")
                                                                .post("/homepage/login/")
                                                                .check(status().is(200))

                                );

                // scn.injectOpen(constantUsersPerSec(10).during (10))
                // scn.injectOpen(rampUsers(5).during(Duration.ofMinutes(5))))
                // scn.injectClosed(constantConcurrentUsers(10).during(10)))
                // scn.injectOpen(rampUsers(2).during(Duration.ofSeconds(5)))).protocols(httpProtocol);
                // scn.injectClosed(constantConcurrentUsers(3).during(Duration.ofMinutes(5))));
                // scn.injectClosed(constantConcurrentUsers(5).during(Duration.ofMinutes(10))))

                setUp(
                                (
                                // scn.injectClosed(constantConcurrentUsers(3).during(300))))
                                scn.injectClosed(constantConcurrentUsers(100).during(Duration.ofMinutes(120)))
                                                .protocols(httpProtocol)));
        }
}
