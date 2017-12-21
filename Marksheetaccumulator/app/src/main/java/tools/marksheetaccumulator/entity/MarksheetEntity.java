package tools.marksheetaccumulator.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.marksheetaccumulator.QuestionOptions;

public class MarksheetEntity implements Serializable {
    public Long id;
    public Long memberId;                           // メンバーID
    public String title;                            // タイトル
    public Integer questionNumber;                  // 問題数
    public QuestionOptions questionOptions;         // 選択肢
    public Integer optionNumber;                    // 選択肢数
    public Date createDate;                         // 作成日
    public Date updateDate;                         // 更新日
    public Map<Integer, QuestionEntity> questionEntityMap
                = new HashMap<>();                // 質問リスト
}
