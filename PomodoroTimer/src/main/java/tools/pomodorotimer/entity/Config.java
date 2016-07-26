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
    private double screenX;     // 初期表示位置(X座標)
    private double screenY;     // 初期表示位置(Y座標)
    private Mode mode;          // 動作モード

    private int workTime;       // 作業時間
    private int breakTime;      // 休憩時間
}
