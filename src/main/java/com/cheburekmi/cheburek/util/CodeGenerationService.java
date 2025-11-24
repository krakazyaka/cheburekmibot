package com.cheburekmi.cheburek.util;

import com.cheburekmi.cheburek.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CodeGenerationService {

    private final UserRepository userRepository;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 4;
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Генерирует случайный уникальный четырехзначный код
     * @return уникальный код
     */
    public String generateUniqueUserCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 100; // Защита от бесконечного цикла
        
        do {
            code = generateRandomCode();
            attempts++;
            
            if (attempts > maxAttempts) {
                throw new RuntimeException("Не удалось сгенерировать уникальный код после " + maxAttempts + " попыток");
            }
        } while (isCodeExists(code));
        
        return code;
    }
    
    /**
     * Генерирует случайный код
     * @return случайный код
     */
    private String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        
        return code.toString();
    }
    
    /**
     * Проверяет существование кода в базе данных
     * @param code код для проверки
     * @return true если код уже существует
     */
    private boolean isCodeExists(String code) {
        return userRepository.existsByUserCode(code);
    }
}