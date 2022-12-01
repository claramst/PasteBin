import dao.PastesContentDao;
import exceptions.DuplicateIdsException;
import exceptions.InvalidIdException;
import exceptions.TableNotFoundException;
import idgenerator.UniqueIdGenerator;

public class PasteBin {
  private UniqueIdGenerator pasteIdGenerator;
  private PastesContentDao pastesDao;
  private int MAX_WRITE_ATTEMPTS = 10;

  public PasteBin(UniqueIdGenerator pasteIdGenerator, PastesContentDao pastesDao) {
    this.pasteIdGenerator = pasteIdGenerator;
    this.pastesDao = pastesDao;
  }
  public String read(String id) throws DuplicateIdsException, InvalidIdException, TableNotFoundException {
    String content = "";
    content = pastesDao.readPaste(id);
    return content;
  }

  public String write(String text) throws DuplicateIdsException {
    String uniqueId = pasteIdGenerator.generateUniqueId();
    int writeAttempts = 0;

    while (writeAttempts < MAX_WRITE_ATTEMPTS) {
      try {
        pastesDao.readPaste(uniqueId);
        writeAttempts++;
        uniqueId = pasteIdGenerator.generateUniqueId();
      } catch (InvalidIdException | TableNotFoundException ignored) {
        // We have a unique id
        break;
      }
    }
    pastesDao.writePaste(uniqueId, text);
    return uniqueId;
  }

}
