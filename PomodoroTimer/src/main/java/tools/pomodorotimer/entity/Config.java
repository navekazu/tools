package tools.pomodorotimer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Config {
    private double screenX;
    private double screenY;

}
