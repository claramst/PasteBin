package idgenerator;

import java.util.UUID;

public class DefaultPasteIdGenerator implements UniqueIdGenerator {
  @Override
  public String generateUniqueId() {
    UUID randomUUID = UUID.randomUUID();
    return randomUUID.toString().replaceAll("-", "").substring(0, 10);
  }
}
