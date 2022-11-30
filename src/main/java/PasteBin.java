import com.amazonaws.services.lambda.runtime.Context;

public class PasteBin {
  public static String handleRequest(String arg, Context context) {
    return arg;
  }

  public String read(String url) {
    // Get url from database
    // Get content from s3 bucket
    return "PastedContent";
  }

  public String write(String text) {
    // Generate unique url
    // Check if there's a duplicate
    // Save url to sql pastes table
    // Saves text to s3 bucket
    return "url";
  }

}
