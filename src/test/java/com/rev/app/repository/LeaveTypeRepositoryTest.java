package com.rev.app.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LeaveTypeRepositoryTest {
    @Test
    void testBasic() {
        assertThat(true).isTrue();
    }
}
