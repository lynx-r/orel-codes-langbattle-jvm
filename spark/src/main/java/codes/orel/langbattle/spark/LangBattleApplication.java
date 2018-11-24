package codes.orel.langbattle.spark;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.lang.String.format;
import static spark.Spark.*;

/**
 * User: aleksey
 * Date: 23/11/2018
 * Time: 06:45
 */
public class LangBattleApplication {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

  public static void main(String[] args) {
    after((req, resp) -> {
      resp.header("Content-Type", "application/json");
    });

    post("/", (req, res) -> {
      var typeRef = new TypeReference<Map<String, Object>>() {
      };
      Map<String, Object> body = objectMapper.readValue(req.body(), typeRef);
      var firstName = (String) body.get("first_name");
      var lastName = (String) body.get("last_name");
      var currentDate = FORMATTER.format(ZonedDateTime.now());
      body.put("first_name", format("%s %s", firstName, DigestUtils.md5Hex(firstName)));
      body.put("last_name", format("%s %s", lastName, DigestUtils.md5Hex(lastName)));
      body.put("current_time", currentDate);
      body.put("say", "Java is the best");
      return objectMapper.writeValueAsString(body);
    });

    System.out.println("Spark started on http://localhost:" + port());
  }
}
