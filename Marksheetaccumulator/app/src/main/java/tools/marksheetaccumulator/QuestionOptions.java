package tools.marksheetaccumulator;

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
}
