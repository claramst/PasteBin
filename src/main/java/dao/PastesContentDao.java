package dao;

import exceptions.DuplicateIdsException;
import exceptions.InvalidIdException;
import exceptions.TableNotFoundException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.HashMap;
import java.util.Map;

public class PastesContentDao {
  private static final String TABLE_NAME = "PastesContent";
  private static final String KEY = "id";
  private static final String CONTENT = "content";

  private DynamoDbClient dbClient;

  public PastesContentDao(DynamoDbClient dbClient) {
    this.dbClient = dbClient;
  }

  public String readPaste(String keyVal) throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    Map<String, AttributeValue> readItems = new HashMap<>();
    readItems.put(KEY, AttributeValue.builder().s(keyVal).build());

    GetItemRequest request = GetItemRequest.builder()
            .tableName(TABLE_NAME)
            // Eventual consistency is sufficient for our case
            .consistentRead(false)
            .key(readItems)
            .attributesToGet(CONTENT)
            .build();
    String content = "";

    try {
      GetItemResponse response = dbClient.getItem(request);
      Map<String, AttributeValue> responseItems = response.item();

      if (responseItems != null && responseItems.size() != 0) {
        if (responseItems.entrySet().size() > 1) {
          throw new DuplicateIdsException("Multiple pastes with the same id");
        }
        AttributeValue pasteAttr = responseItems.get(CONTENT);
        content = pasteAttr.s();
      } else {
        throw new InvalidIdException("Could not find paste id");
      }
    } catch (ResourceNotFoundException e) {
      throw new TableNotFoundException("Error: The Amazon DynamoDB table " + TABLE_NAME + " can't be found.\n");
    }
    return content;
  }

  public void writePaste(String keyVal,
                                    String contentVal) {

    Map<String, AttributeValue> itemValues = new HashMap<>();
    itemValues.put(KEY, AttributeValue.builder().s(keyVal).build());
    itemValues.put(CONTENT, AttributeValue.builder().s(contentVal).build());

    PutItemRequest request = PutItemRequest.builder()
            .tableName(TABLE_NAME)
            .item(itemValues)
            .build();

    try {
      dbClient.putItem(request);
    } catch (ResourceNotFoundException e) {
      System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", TABLE_NAME);
    }
  }

}
