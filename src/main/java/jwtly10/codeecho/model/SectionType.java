package jwtly10.codeecho.model;

public class SectionType {
    public enum SecType {
        CODE, TEXT
    }

    private final SecType type;
    private final String content;
    private String language;

    public SectionType(SecType type, String content, String language) {
        this.type = type;
        this.content = content;
        this.language = language;
    }

    public SectionType(SecType type, String content) {
        this.type = type;
        this.content = content;
        this.language = "none";
    }

    public SecType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
