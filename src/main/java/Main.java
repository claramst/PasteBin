import dao.PastesContentDao;
import exceptions.DuplicateIdsException;
import exceptions.InvalidIdException;
import exceptions.TableNotFoundException;
import idgenerator.DefaultPasteIdGenerator;
import idgenerator.UniqueIdGenerator;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Main {
  public static void main(String[] args) {
    Region region = Region.EU_WEST_2;
    DynamoDbClient dbClient = DynamoDbClient.builder()
            .region(region)
            .build();
    PastesContentDao pastesContentDao = new PastesContentDao(dbClient);
    UniqueIdGenerator pasteIdGenerator = new DefaultPasteIdGenerator();
    PasteBin pasteBin = new PasteBin(pasteIdGenerator, pastesContentDao);

    if (args.length <= 1) {
      System.out.println("Please input a command and an id to read or text to store");
      return;
    }
    String command = args[0];

    switch (command) {
      case "read":
        String readId = args[1];
        try {
          System.out.println(pasteBin.read(readId));
        } catch (DuplicateIdsException e) {
          System.err.println("Server Error: Multiple pastes with same id");
        } catch (InvalidIdException e) {
          System.err.println("Invalid paste id");
        } catch (TableNotFoundException e) {
          System.err.println("Server Error: Cannot find PastesContent table");
        }
        break;
      case "write":
        String content = args[1];
        String generatedId = null;
        try {
          generatedId = pasteBin.write(content);
        } catch (DuplicateIdsException e) {
          System.err.println("Duplicated ids");
        }
        System.out.println("The id of your text is stored at " + generatedId);
        break;
    }

  }
}
