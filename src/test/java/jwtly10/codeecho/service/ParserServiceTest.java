package jwtly10.codeecho.service;

import jwtly10.codeecho.model.SectionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParserServiceTest {

    @Test
    public void testParseMarkdownIntoSection() {
        String markdown = """
                To write to a file in Java, you can use the `FileWriter` class. Here's an example:
                ```java
                    import java.io.FileWriter;
                    import java.io.IOException;

                    public class WriteToFile {
                        public static void main(String[] args) {
                            String filename = "output.txt";
                            String content = "Hello, World!";

                            try (FileWriter writer = new FileWriter(filename)) {
                                writer.write(content);
                            } catch (IOException e) {
                                System.err.println("Error writing to file: " + e.getMessage());
                            }
                        }
                    }
                ```
                So this is how you can write to a file in Java. You can also print out the data using something like:
                                
                ```java
                    public class Main {
                        public static void main(String[] args) {
                            System.out.println("Hello world);
                        }
                    }
                ```
                                
                So thats how you do that
                """;

        ParserService parserService = new ParserService();
        List<SectionType> sections = parserService.parseMarkdownIntoSections(markdown);

        Assertions.assertEquals(5, sections.size());
    }

    @Test
    public void testParserMultipleLanguages() {
        String markdown = """
                To write to a file in Java, you can use the `FileWriter` class. Here's an example:
                ```java
                    import java.io.FileWriter;
                    import java.io.IOException;

                    public class WriteToFile {
                        public static void main(String[] args) {
                            String filename = "output.txt";
                            String content = "Hello, World!";

                            try (FileWriter writer = new FileWriter(filename)) {
                                writer.write(content);
                            } catch (IOException e) {
                                System.err.println("Error writing to file: " + e.getMessage());
                            }
                        }
                    }
                ```
                So this is how you can write to a file in Java. You can also print out the data using something like:
                                
                ```go
                    package main
                    import "fmt"
                    func main(){
                        fmt.Println("Hello, World!")
                    }
                ```
                """;

        ParserService parser = new ParserService();
        List<SectionType> sections = parser.parseMarkdownIntoSections(markdown);

        for (SectionType section : sections) {
            System.out.printf("Type: %s, Language: %s, Content: %s\n", section.getType(), section.getLanguage(), section.getContent());
        }

        Assertions.assertEquals(4, sections.size());
        Assertions.assertEquals(SectionType.SecType.TEXT, sections.get(0).getType());
        Assertions.assertEquals(SectionType.SecType.CODE, sections.get(1).getType());
        Assertions.assertEquals("java", sections.get(1).getLanguage());
        Assertions.assertEquals(SectionType.SecType.TEXT, sections.get(2).getType());
        Assertions.assertEquals(SectionType.SecType.CODE, sections.get(3).getType());
        Assertions.assertEquals("go", sections.get(3).getLanguage());
    }
}
