package jwtly10.codeecho.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jwtly10.codeecho.model.TranscriptResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class ProxyService {

    /**
     * Transcribes the given audio data and returns the transcript and confidence
     *
     * @param audio audio data to be transcribed
     */
    public TranscriptResponse transcribeAudio(byte[] audio) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/api/v1/transcribe");

        HttpClient client = HttpClient.newHttpClient();

        String base64Data = Base64.getEncoder().encodeToString(audio);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(base64Data))
                .build();

        HttpResponse<String> res = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(res.body(), TranscriptResponse.class);
    }
}
