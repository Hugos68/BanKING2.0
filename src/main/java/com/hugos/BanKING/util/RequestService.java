package com.hugos.BanKING.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class RequestService {


    // Returns null if parse fails
    public JsonObject getJsonFromRequest(HttpServletRequest request) {
        JsonObject jsonObj;
        try {
             jsonObj = JsonParser.parseString(request.getReader().lines().collect(Collectors.joining(System.lineSeparator()))).getAsJsonObject();
             return jsonObj;
        } catch (IOException e) {
            return null;
        }
    }
}
