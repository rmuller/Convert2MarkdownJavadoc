# README

This project contains a simple command-line tool to convert traditional `/** ... */` Javadoc comments into modern [Java 23–style](https://openjdk.org/jeps/467) `///`-based Markdown documentation.

## ✨ Introduction: Markdown in Javadoc (Java 23+)

Starting with **Java 23**, Javadoc supports writing documentation comments using **Markdown syntax** via line comments prefixed with `///`. This was introduced by [JEP 467: Markdown Documentation Comments](https://openjdk.org/jeps/467).

### ✅ Why Markdown Support Matters

- **Simpler syntax** — More intuitive than HTML tags like `<ul>`, `<code>`, etc.
- **Improved readability** — Cleaner for both source code and rendered documentation.
- **Better tooling support** — IDEs and static site generators increasingly support Markdown.
- **Easy formatting** — Lists, code blocks, emphasis, and links are much easier to write.

## 🔧 What This Tool Does

`Convert2MarkdownJavadoc` scans `.java` files and replaces traditional Javadoc comments with `///`-based Markdown equivalents, preserving indentation.

### Converts:

| Traditional Javadoc           | Converted Markdown     |
| ----------------------------- | ---------------------- |
| `<p>` and `</p>`              | Blank line             |
| `<code>text</code>`           | `` `text` ``           |
| `{@code text}`                | `` `text` ``           |
| `<ul><li>Item</li></ul>`      | `- Item`               |
| `<pre><code>...</code></pre>` | ``` fenced code blocks |

This is easy to extend. Contributions are welcome!

## 🚀 Usage

### ✅ Run without compiling (Java 11+):

Since the script is a **single-file Java source program** with a `main()` method, it can be run without compiling using the Java 11+ feature.

```bash
java Convert2MarkdownJavadoc.java <directory>
```

This will process all .java files recursively under the specified directory and update them in-place. If the directory is not specified, the current directory is used.

> ⚠️ **Tip**: Back up your files or use version control before running the tool.

## 📁 Example

### Before

```java
/**
 * Performs the task.
 * <p>It is <code>important</code>.</p>
 * <ul>
 *   <li>First</li>
 *   <li>Second</li>
 * </ul>
 */
```

### After

```java
/// Performs the task.
///
/// It is `important`.
///
/// - First
/// - Second
```

Note: Preserves indentation to match surrounding code.

## 📚 References

- [JEP 467: Markdown Documentation Comments](https://openjdk.org/jeps/467)
- [Oracle Docs: Using Markdown in Javadoc (Java 24)](https://docs.oracle.com/en/java/javase/24/javadoc/using-markdown-documentation-comments.html)
- [Javadoc Enhancements in Java 23 – Baeldung](https://www.baeldung.com/java-23-new-features#markdown-javadoc)

## 🧑‍💻 Requirements

- **Java 17+** to run this tool (uses modern Files.readString, etc.)
- **Java 23+** to use the resulting Markdown Javadoc in real code

## 📌 Limitations

- Only standard formatting tags are handled (`<p>`, `<code>`, `<ul>`, etc.). Does not handle edge-case tags like `<table>` or `<img>`
- Overwrites files directly — use with version control or backups!
- Assumes consistent indentation (e.g., 4 spaces)

## 🏁 License

Apache 2.0 License.

## 🙌 Acknowledgments

Created by XIAM Solutions B.V. to ease the transition to modern Javadoc with Markdown in Java 23 and beyond.

Code review and writing 90% of this `README.md` done by ChatGPT.
