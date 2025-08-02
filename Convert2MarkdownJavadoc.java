/*
 * Copyright 2025 XIAM Solutions B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// `Convert2MarkdownJavadoc` converts the traditional JavaDoc comments with Markdown Javadoc
/// as introduced by Java 23.
///
/// Also see <https://docs.oracle.com/en/java/javase/24/javadoc/using-markdown-documentation-comments.html>
///
/// Example:
///
/// Original code
///
/// ```java
/// /**
///  * A method that does <code>something</code>.
///  * <ul>
///  *   <li>First</li>
///  *   <li>Second</li>
///  * </ul>
///  */
/// ```
///
/// After conversion: 
///
/// ```java
/// /// A method that does `something`.
/// ///
/// /// - First
/// /// - Second
/// ```
///
/// @since INFOMAS 3.1
public final class Convert2MarkdownJavadoc {

    private static final Pattern JAVADOC_COMMENT =  
        Pattern.compile("/\\*\\*(.*?)\\*/", Pattern.DOTALL); // multi-line
    private static final int BASE_INDENT = 20;
    private static final String PREFIX = " ".repeat(BASE_INDENT) + "/// ";
    private final Path root;

    /// Create a new `Convert2MarkdownJavadoc` instance
    ///
    /// @param root The root directory to search
    public Convert2MarkdownJavadoc(Path root) {
        this.root = root;
    }

    /// Scan for all `*.java` files in the specified directory and subdirectories.
    /// For each java file, replace all traditional Javadoc comments `/**  */` to the Markdown
    /// Javadoc format `\\\`.
    /// 
    /// @throws IOException when an IO exception is thrown while reading / writing the Java file
    public void execute() throws IOException {
        try (var stream = Files.walk(root)) {
            stream
                .sorted()
                .filter(f -> f.getFileName().toString().endsWith(".java"))
                .forEach(this::convert);
        }
    }

    // private

    private void convert(Path file) {
        try {
            String content = Files.readString(file);
            Matcher matcher = JAVADOC_COMMENT.matcher(content);
            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                int start = matcher.start();
                int lineStart = content.lastIndexOf('\n', start);
                if (lineStart >= 0) {
                    // +1 to exclude the newline character itself, so we only get the line content
                    String lineBefore = content.substring(lineStart + 1, start);
                    if (lineBefore.stripLeading().startsWith("///")) {
                        continue;  // skip already Markdown-style javadoc
                    }
                }
                String javadoc = matcher.group(1);
                String converted = convertBlock(javadoc);
                matcher.appendReplacement(result, Matcher.quoteReplacement(converted));
            }
            if (result.length() == 0) {
                // log("No javadoc to convert, skipped: %s", file);
                return;
            }
            matcher.appendTail(result);
            content = result.toString();
            Files.writeString(file, content);
            log("Updated: %s", file);
        } catch (IOException ex) {
            log("Error in '%s': %s", file, ex);
        }
    }

    private String convertBlock(String javadoc) {
        String[] lines = javadoc.split("\n");
        StringBuilder newLines = new StringBuilder();
        for (int i = 0; i < lines.length; ++i) {
            String line = lines[i];
            if (line.isBlank()) {
                continue;
            }

            line = line.replaceAll("<p>", "\n");
            line = line.replaceAll("</p>", "");
            line = line.replaceAll("<code>(.*?)</code>", "`$1`");
            line = line.replaceAll("\\{@code\\s+([^}]+)}", "`$1`");
            line = line.replaceAll("<b>(.*?)</b>", "**$1**");
            line = line.replaceAll("<strong>(.*?)</strong>", "**$1**");
            line = line.replaceAll("<i>(.*?)</i>", "*$1*");
            line = line.replaceAll("<em>(.*?)</em>", "*$1*");
            line = line.replaceAll("<ul>", "");
            line = line.replaceAll("</ul>", "");
            line = line.replaceAll("<li>", "- ");
            line = line.replaceAll("</li>", "");
            line = line.replaceAll("<pre><code>", "```");
            line = line.replaceAll("</code></pre>", "```");

            int p = line.indexOf('*');
            int offset = p < 2 || newLines.isEmpty() ? BASE_INDENT : BASE_INDENT - 4;
            newLines.append(PREFIX, offset, PREFIX.length())
                    .append(line.substring(p + 1).trim())
                    .append("\n");
        }
        newLines.setLength(newLines.length() - 1);
        return newLines.toString();
    }

    private void log(String msg, Object... args) {
        System.out.println(args.length == 0 ? msg : msg.formatted(args));
    }

    /// Start the conversion process.
    /// 
    /// @param args At most a single argument is expected, the root directory/
    /// If not specified, the current directory is used as root
    /// 
    /// @throws IOException when an IO exception is thrown while reading / writing the Java file
    public static void main(String[] args) throws IOException {
        Path root = args.length > 0 ? Path.of(args[0]) : Path.of(".");
        var converter = new Convert2MarkdownJavadoc(root);
        converter.execute();
    }

}