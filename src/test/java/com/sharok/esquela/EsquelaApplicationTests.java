package com.sharok.esquela;

import com.sharok.esquela.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class EsquelaApplicationTests extends BaseControllerTests {

    @MockBean private StudentService userService;

    @Test
    void contextLoads() {}
}
