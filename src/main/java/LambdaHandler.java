import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import dao.PastesContentDao;
import exceptions.DuplicateIdsException;
import exceptions.InvalidIdException;
import exceptions.TableNotFoundException;
import idgenerator.DefaultPasteIdGenerator;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class LambdaHandler {
    private Region REGION = Region.EU_WEST_2;
    private DynamoDbClient dbClient;
    private PasteBin pasteBin;

    public LambdaHandler() {
      dbClient = DynamoDbClient.builder()
              .region(REGION)
              .build();

      pasteBin = new PasteBin(new DefaultPasteIdGenerator(), new PastesContentDao(dbClient));

    }
    public String writeHandler(String input, Context context) {
      context.getLogger().log("Entered write handler: Writing content: " + input);
      try {
        String id = pasteBin.write(input);
        context.getLogger().log("Write successful. Unique id: " + id);
        return id;
      } catch (DuplicateIdsException e) {
        context.getLogger().log(e.getMessage());
      }
      return "";
  }

  public String readHandler(APIGatewayProxyRequestEvent input, Context context) {
    context.getLogger().log("Entered read handler: Finding paste with id: " + input);
//    System.out.println(input.getPathParameters());
    try {
      String content = pasteBin.read("test");
      context.getLogger().log("Read successful. Content: " + content);
      return content;
    } catch (DuplicateIdsException | InvalidIdException | TableNotFoundException e) {
      context.getLogger().log(e.getMessage());
    }

    return "";
  }

}
