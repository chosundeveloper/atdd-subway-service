package nextstep.subway.line.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("거리")
public class DistanceTest {

    @DisplayName("거리 생성")
    @ParameterizedTest
    @ValueSource(ints = {1})
    void constructor(int distance) {
        assertThatNoException().isThrownBy(() -> new Distance(distance));
    }

    @DisplayName("거리는 0보다 커야한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void negative(int distance) {
        assertThatThrownBy(() -> new Distance(distance))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
