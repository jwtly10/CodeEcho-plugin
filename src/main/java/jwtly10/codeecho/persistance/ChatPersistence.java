package jwtly10.codeecho.persistance;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;
import jwtly10.codeecho.model.ChatSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ChatPersistence {

    private static final Logger log = Logger.getInstance(ChatPersistence.class);
    private static final String SESSIONS_FILE = "sessions.json";
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    ;

    public static void saveSessions(List<ChatSession> sessions) throws IOException {
        // TODO: SANITIZE ALL USER INPUTS
        String filePath = PathManager.getConfigPath() + "/" + SESSIONS_FILE;
        try {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonString = mapper.writeValueAsString(sessions);
            Files.write(Paths.get(filePath), jsonString.getBytes());
        } catch (Exception e) {
            log.error("Failed to save sessions", e);
            throw e;
        }
    }

    public static List<ChatSession> loadSessions() throws IOException {
        String filePath = PathManager.getConfigPath() + "/" + SESSIONS_FILE;
        try {
            File file = new File(filePath);
            return mapper.readValue(file, new TypeReference<List<ChatSession>>() {
            });
        } catch (IOException e) {
            log.error("Failed to load sessions", e);
            throw e;
        }
    }

}