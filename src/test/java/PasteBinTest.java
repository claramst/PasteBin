import dao.PastesContentDao;
import exceptions.DuplicateIdsException;
import exceptions.InvalidIdException;
import exceptions.TableNotFoundException;
import idgenerator.UniqueIdGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PasteBinTest {
  @Mock
  private PastesContentDao pastesContentDao;
  @Mock
  private UniqueIdGenerator pasteIdGenerator;
  @Mock
  private DynamoDbClient dbClient;

  private PasteBin pasteBin;

  private static final String TEST_ID = "testId";
  private static final String TEST_CONTENT = "Some test content to store";

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    pasteBin = new PasteBin(pasteIdGenerator, pastesContentDao);
  }

  @Test
  public void testWritePaste() throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    // given
    when(pasteIdGenerator.generateUniqueId()).thenReturn(TEST_ID);
    when(dbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().build());
    when(pastesContentDao.readPaste(TEST_ID)).thenThrow(InvalidIdException.class);

    // when
    String id = pasteBin.write(TEST_CONTENT);

    // then
    assertEquals(id, TEST_ID);
  }

  @Test(expected = DuplicateIdsException.class)
  public void testWriteDuplicatePaste() throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    // given
    when(pasteIdGenerator.generateUniqueId()).thenReturn(TEST_ID);
    when(dbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().build());
    when(pastesContentDao.readPaste(TEST_ID)).thenThrow(DuplicateIdsException.class);

    // when
    pasteBin.write(TEST_CONTENT);
    pasteBin.write(TEST_CONTENT);
  }

  @Test
  public void testReadPaste() throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    // given
    Map<String, AttributeValue> testItemsResponse = new HashMap<>();
    testItemsResponse.put(TEST_ID, AttributeValue.builder().s(TEST_CONTENT).build());
    when(dbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().item(testItemsResponse).build());
    when(pastesContentDao.readPaste(TEST_ID)).thenReturn(TEST_CONTENT);

    // when
    String content = pasteBin.read(TEST_ID);

    // then
    assertEquals(content, TEST_CONTENT);
  }

  @Test (expected = InvalidIdException.class)
  public void testReadInvalidPasteId() throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    // given
    when(pastesContentDao.readPaste(TEST_ID)).thenThrow(InvalidIdException.class);
    Map<String, AttributeValue> testItemsResponse = new HashMap<>();
    testItemsResponse.put("id", AttributeValue.builder().s(TEST_ID).build());
    when(dbClient.getItem(any(GetItemRequest.class))).thenReturn(GetItemResponse.builder().item(testItemsResponse).build());

    // when
    pasteBin.read(TEST_ID);

  }

}

