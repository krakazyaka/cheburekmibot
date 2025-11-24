package com.cheburekmi.cheburek.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CodeGenerationServiceTest {

    @Autowired
    private CodeGenerationService codeGenerationService;
    
    @Test
    void testGenerateUniqueUserCode() {
        String code = codeGenerationService.generateUniqueUserCode();
        
        assertNotNull(code);
        assertEquals(4, code.length());
        assertTrue(code.matches("[A-Z0-9]{4}"));
        
        // Проверяем, что код состоит только из разрешенных символов
        for (char c : code.toCharArray()) {
            assertTrue(Character.isUpperCase(c) || Character.isDigit(c));
        }
    }
}