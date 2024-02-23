package jwtly10.codeecho.service;

import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.ibm.icu.impl.Assert.fail;
import static jwtly10.codeecho.model.ChatGPTRole.system;
import static jwtly10.codeecho.model.ChatGPTRole.user;

public class ProxyServiceTest {
    @Test
    public void testGoodGetChatGPTResponse() {
        List<ChatGPTMessage> messages = List.of(
                new ChatGPTMessage(user, "Hello, my name is Josh"),
                new ChatGPTMessage(system, "Hey Josh, I'm ChatGPT. How can I help you today?")
        );

        ChatGPTRequest req = new ChatGPTRequest(messages, "Do you remember my name?");

        CountDownLatch latch = new CountDownLatch(1);
        ProxyService proxyService = new ProxyService(HttpClient.newHttpClient());


        final String[] responseData = new String[1];

        try {
            proxyService.getChatGPTResponse(req, new AsyncCallback<>() {
                @Override
                public void onResult(String data) {
                    responseData[0] = data;
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    fail("The request failed.");
                }

                @Override
                public void onComplete() {
                    latch.countDown();
                }
            });
            latch.await();

            Assertions.assertTrue(responseData[0].contains("Josh"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}