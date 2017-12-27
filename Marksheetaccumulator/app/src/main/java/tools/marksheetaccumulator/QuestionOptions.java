package tools.marksheetaccumulator;

import java.util.HashMap;
import java.util.Map;

public enum QuestionOptions {
    ALPHABET(0),
    NUMBER(1),
    KATAKANA(2),
    ;
    private static QuestionOptions[] options = new QuestionOptions[]{
            ALPHABET,
            NUMBER,
            KATAKANA,
    };

    private static Map<Integer, String[]> optionValues = new HashMap<>();
    static {
        optionValues.put(ALPHABET.number, new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", });
        optionValues.put(NUMBER.number, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", });
        optionValues.put(KATAKANA.number, new String[]{"ア", "イ", "ウ", "エ", "オ", "カ", "キ", "ク", "ケ", "コ", });
    }

    private final int number;

    private QuestionOptions(int number) {
        this.number = number;
    }

    public static QuestionOptions getQuestionOptions(int value) {
        return getQuestionOptions((long)value);
    }
    public static QuestionOptions getQuestionOptions(long value) {
        for (QuestionOptions opt: options) {
            if (opt.number==value) {
                return opt;
            }
        }
        throw new IllegalArgumentException();
    }
    public int getValue() {
        return number;
    }
    public String[] getOptionValues() {
        return optionValues.get(this.number);
    }
}
