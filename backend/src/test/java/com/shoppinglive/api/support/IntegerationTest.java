package com.shoppinglive.api.support;

import com.shoppinglive.api.config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class IntegerationTest extends TestContainer{

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void cleanup() {
        databaseCleanup.execute();
    }

    @Autowired
    protected SpringBootTestSupport testSupport;
}
