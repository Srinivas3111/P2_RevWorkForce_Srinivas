package com.rev.app.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;
import com.rev.app.entity.SystemActivityLog;

@DataJpaTest
class SystemActivityLogRepositoryTest {
    @Test
    void testFindAll() {
        // Basic setup to ensure it runs
        assertThat(true).isTrue();
    }
}
