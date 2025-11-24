package com.cheburekmi.cheburek.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TelegramAuthService {

    @Value("${telegram.bot.token}")
    private String botToken;

    public boolean validateInitData(String initData) {
        try {
            Map<String, String> params = parseInitData(initData);
            
            String hash = params.get("hash");
            if (hash == null) {
                return false;
            }

            String authDate = params.get("auth_date");
            if (authDate == null) {
                return false;
            }

            long authTimestamp = Long.parseLong(authDate);
            long currentTimestamp = System.currentTimeMillis() / 1000;
            long timeDifference = currentTimestamp - authTimestamp;
            
            if (timeDifference > 300) {
                return false;
            }

            params.remove("hash");

            String dataCheckString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("\n"));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] secretKey = digest.digest(botToken.getBytes(StandardCharsets.UTF_8));

            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "HmacSHA256");
            hmac.init(secretKeySpec);

            byte[] hashBytes = hmac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String calculatedHash = bytesToHex(hashBytes);

            return calculatedHash.equalsIgnoreCase(hash);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractTelegramId(String initData) {
        try {
            Map<String, String> params = parseInitData(initData);
            String userJson = params.get("user");
            
            if (userJson != null) {
                return extractIdFromUserJson(userJson);
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> parseInitData(String initData) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = initData.split("&");
        
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        
        return params;
    }

    private String extractIdFromUserJson(String userJson) {
        int idStart = userJson.indexOf("\"id\":");
        if (idStart == -1) {
            return null;
        }
        
        idStart += 5;
        
        while (idStart < userJson.length() && 
               (userJson.charAt(idStart) == ' ' || userJson.charAt(idStart) == ':')) {
            idStart++;
        }
        
        int idEnd = idStart;
        while (idEnd < userJson.length() && 
               Character.isDigit(userJson.charAt(idEnd))) {
            idEnd++;
        }
        
        if (idEnd > idStart) {
            return userJson.substring(idStart, idEnd);
        }
        
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
