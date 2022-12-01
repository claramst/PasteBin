package idgenerator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DefaultPasteIdGeneratorTest {
  private DefaultPasteIdGenerator defaultPasteIdGenerator;

  @Before
  public void setUp() {
    defaultPasteIdGenerator = new DefaultPasteIdGenerator();
  }

  @Test
  public void AlphaNumericCharactersTest() {
    assertFalse(defaultPasteIdGenerator.generateUniqueId().chars().anyMatch(Character::isLetterOrDigit));
  }
}
