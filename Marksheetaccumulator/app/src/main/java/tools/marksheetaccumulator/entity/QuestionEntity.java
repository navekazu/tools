package tools.marksheetaccumulator.entity;

import java.io.Serializable;

public class QuestionEntity implements Serializable {
    public Long id;
    public Long memberId;                           // メンバーID
    public Long marksheetId;                        // マークシートID
    public Integer questionNo;                      // 質問番号
    public Integer choice;                          // 選択肢
    public Integer rightNo;                         // 正解
}
