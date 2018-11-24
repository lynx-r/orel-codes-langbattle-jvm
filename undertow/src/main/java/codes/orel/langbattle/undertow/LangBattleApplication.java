package codes.orel.langbattle.undertow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static java.lang.String.format;

public class LangBattleApplication {

  public static final String ERROR_READ_JSON = "Error while reading json";
  public static final String ERROR_WRITE_JSON = "Error while writing json";
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

  public static void main(final String[] args) {
    Undertow server = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(exchange ->
            exchange.getRequestReceiver().receiveFullString((exchangeResp, message) -> {
              var typeRef = new TypeReference<Map<String, Object>>() {
              };
              Map<String, Object> body;
              try {
                body = objectMapper.readValue(message, typeRef);
              } catch (IOException e) {
                exchangeResp.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchangeResp.getResponseSender().send(ERROR_READ_JSON);
                return;
              }
              var firstName = (String) body.get("first_name");
              var lastName = (String) body.get("last_name");
              var currentDate = FORMATTER.format(ZonedDateTime.now());
              body.put("first_name", format("%s %s", firstName, DigestUtils.md5Hex(firstName)));
              body.put("last_name", format("%s %s", lastName, DigestUtils.md5Hex(lastName)));
              body.put("current_time", currentDate);
              body.put("say", "Java is the best");
              String str;
              try {
                str = objectMapper.writeValueAsString(body);
              } catch (JsonProcessingException e) {
                exchangeResp.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchangeResp.getResponseSender().send(ERROR_WRITE_JSON);
                return;
              }
              exchangeResp.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
              exchangeResp.getResponseSender().send(str);
            })).build();
    server.start();
  }
}