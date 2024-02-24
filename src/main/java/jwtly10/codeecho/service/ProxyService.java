package jwtly10.codeecho.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTRequest;
import jwtly10.codeecho.model.TranscriptResponse;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class ProxyService {

    private final HttpClient client;

    public ProxyService(HttpClient client) {
        this.client = client;
    }

    /**
     * Transcribes the given audio data and returns the transcript and confidence
     *
     * @param audio audio data to be transcribed
     * @return a TranscriptResponse object that contains the transcript and confidence of transcription
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the current thread is interrupted
     */
    public TranscriptResponse transcribeAudio(byte[] audio) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/api/v1/transcribe");

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

    /**
     * Streams responses from ChatGPT, using a callback to handle actions due to asynchronous nature
     *
     * @param req      a ChatGPT request
     * @param callback a callback that handles the response
     *                 The callback handles 3 scenarios:
     *                 1. onData: do X when data is received
     *                 2. onError: do X when an error occurs, with the error message
     *                 3. onComplete: do X when the request has finished streaming
     */
    public void getChatGPTResponse(ChatGPTRequest req, AsyncCallback<String> callback) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            URI url = URI.create("http://localhost:8080/api/v1/chatgpt/stream");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(req)))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                    .thenAccept(response -> {

                        if (response.statusCode() != 200) {
                            SwingUtilities.invokeLater(() -> {
                                if (callback != null) {
                                    callback.onError(new Exception("Failed to get response from ChatGPT"));
                                }
                            });
                            return;
                        }


                        try (var reader = new BufferedReader(new InputStreamReader(response.body()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                String finalLine = line;

                                SwingUtilities.invokeLater(() -> {
                                    if (callback != null) {
                                        callback.onResult(finalLine);
                                    }
                                });
                            }

                            SwingUtilities.invokeLater(() -> {
                                if (callback != null) {
                                    callback.onComplete();
                                }
                            });
                        } catch (IOException e) {
                            SwingUtilities.invokeLater(() -> {
                                if (callback != null) {
                                    callback.onError(e);
                                }
                            });
                        }
                    })
                    .join();
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onError(e);
                }
            });
        }
    }
}
