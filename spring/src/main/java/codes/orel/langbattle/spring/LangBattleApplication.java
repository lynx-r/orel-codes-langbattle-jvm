package codes.orel.langbattle.spring;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.lang.String.format;

@RestController
@SpringBootApplication
public class LangBattleApplication {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

  public static void main(String[] args) {
    SpringApplication.run(LangBattleApplication.class, args);
  }

  @PostMapping("/")
  public ResponseEntity<Map<String, Object>> postJson(@RequestBody() Map<String, Object> body) {
    var firstName = (String) body.get("first_name");
    var lastName = (String) body.get("last_name");
    var currentDate = FORMATTER.format(ZonedDateTime.now());
    body.put("first_name", format("%s %s", firstName, DigestUtils.md5Hex(firstName)));
    body.put("last_name", format("%s %s", lastName, DigestUtils.md5Hex(lastName)));
    body.put("current_time", currentDate);
    body.put("say", "Java is the best");
    return ResponseEntity.ok(body);
  }
}
