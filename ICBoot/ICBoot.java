import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ICBoot {
    private static final Map<Character, Integer[]> keyMap = new HashMap<>();
    static {
        keyMap.put('A', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_A});
        keyMap.put('B', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_B});
        keyMap.put('C', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_C});
        keyMap.put('D', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_D});
        keyMap.put('E', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_E});
        keyMap.put('F', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_F});
        keyMap.put('G', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_G});
        keyMap.put('H', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_H});
        keyMap.put('I', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_I});
        keyMap.put('J', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_J});
        keyMap.put('K', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_K});
        keyMap.put('L', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_L});
        keyMap.put('M', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_M});
        keyMap.put('N', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_N});
        keyMap.put('O', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_O});
        keyMap.put('P', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_P});
        keyMap.put('Q', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Q});
        keyMap.put('R', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_R});
        keyMap.put('S', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_S});
        keyMap.put('T', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_T});
        keyMap.put('U', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_U});
        keyMap.put('V', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_V});
        keyMap.put('W', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_W});
        keyMap.put('X', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_X});
        keyMap.put('Y', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Y});
        keyMap.put('Z', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_Z});
        keyMap.put('a', new Integer[]{KeyEvent.VK_A});
        keyMap.put('b', new Integer[]{KeyEvent.VK_B});
        keyMap.put('c', new Integer[]{KeyEvent.VK_C});
        keyMap.put('d', new Integer[]{KeyEvent.VK_D});
        keyMap.put('e', new Integer[]{KeyEvent.VK_E});
        keyMap.put('f', new Integer[]{KeyEvent.VK_F});
        keyMap.put('g', new Integer[]{KeyEvent.VK_G});
        keyMap.put('h', new Integer[]{KeyEvent.VK_H});
        keyMap.put('i', new Integer[]{KeyEvent.VK_I});
        keyMap.put('j', new Integer[]{KeyEvent.VK_J});
        keyMap.put('k', new Integer[]{KeyEvent.VK_K});
        keyMap.put('l', new Integer[]{KeyEvent.VK_L});
        keyMap.put('m', new Integer[]{KeyEvent.VK_M});
        keyMap.put('n', new Integer[]{KeyEvent.VK_N});
        keyMap.put('o', new Integer[]{KeyEvent.VK_O});
        keyMap.put('p', new Integer[]{KeyEvent.VK_P});
        keyMap.put('q', new Integer[]{KeyEvent.VK_Q});
        keyMap.put('r', new Integer[]{KeyEvent.VK_R});
        keyMap.put('s', new Integer[]{KeyEvent.VK_S});
        keyMap.put('t', new Integer[]{KeyEvent.VK_T});
        keyMap.put('u', new Integer[]{KeyEvent.VK_U});
        keyMap.put('v', new Integer[]{KeyEvent.VK_V});
        keyMap.put('w', new Integer[]{KeyEvent.VK_W});
        keyMap.put('x', new Integer[]{KeyEvent.VK_X});
        keyMap.put('y', new Integer[]{KeyEvent.VK_Y});
        keyMap.put('z', new Integer[]{KeyEvent.VK_Z});
        keyMap.put('0', new Integer[]{KeyEvent.VK_0});
        keyMap.put('1', new Integer[]{KeyEvent.VK_1});
        keyMap.put('2', new Integer[]{KeyEvent.VK_2});
        keyMap.put('3', new Integer[]{KeyEvent.VK_3});
        keyMap.put('4', new Integer[]{KeyEvent.VK_4});
        keyMap.put('5', new Integer[]{KeyEvent.VK_5});
        keyMap.put('6', new Integer[]{KeyEvent.VK_6});
        keyMap.put('7', new Integer[]{KeyEvent.VK_7});
        keyMap.put('8', new Integer[]{KeyEvent.VK_8});
        keyMap.put('9', new Integer[]{KeyEvent.VK_9});
        keyMap.put('@', new Integer[]{KeyEvent.VK_AT});
        keyMap.put('\\', new Integer[]{KeyEvent.VK_BACK_SLASH});
        keyMap.put('^', new Integer[]{KeyEvent.VK_CIRCUMFLEX});
        keyMap.put(']', new Integer[]{KeyEvent.VK_CLOSE_BRACKET});
        keyMap.put(':', new Integer[]{KeyEvent.VK_COLON});
        keyMap.put(',', new Integer[]{KeyEvent.VK_COMMA});
        keyMap.put('$', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_4});//KeyEvent.VK_DOLLAR});
        keyMap.put('=', new Integer[]{KeyEvent.VK_EQUALS});
        keyMap.put('!', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_1});//KeyEvent.VK_EXCLAMATION_MARK});
        keyMap.put('(', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_8});//KeyEvent.VK_LEFT_PARENTHESIS});
        keyMap.put('-', new Integer[]{KeyEvent.VK_MINUS});
        keyMap.put('#', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_3});//KeyEvent.VK_NUMBER_SIGN});
        keyMap.put('[', new Integer[]{KeyEvent.VK_OPEN_BRACKET});
        keyMap.put('.', new Integer[]{KeyEvent.VK_PERIOD});
        keyMap.put('+', new Integer[]{KeyEvent.VK_PLUS});
        keyMap.put(')', new Integer[]{KeyEvent.VK_SHIFT, KeyEvent.VK_9});//KeyEvent.VK_RIGHT_PARENTHESIS});
        keyMap.put(';', new Integer[]{KeyEvent.VK_SEMICOLON});
        keyMap.put('/', new Integer[]{KeyEvent.VK_SLASH});
        keyMap.put('_', new Integer[]{KeyEvent.VK_UNDERSCORE});
    }

    public static void main(String[] args) {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(10);

            int shiftArgs = 0;
            if (args[0].contains("http")) {
                Runtime runtime = Runtime.getRuntime();
//                runtime.exec(new String[]{"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe", "--profile-directory='Profile 2'", "https://www.yahoo.co.jp/"});
//                runtime.exec("\"C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe\" --profile-directory=\"Profile 2\" https://www.yahoo.co.jp/");
                runtime.exec(args[0]);
                shiftArgs = 1;
            } else {
                keyPush(robot, new int[]{KeyEvent.VK_ALT, KeyEvent.VK_TAB});
            }

            robot.delay(3000);
            keyPush(robot, KeyEvent.VK_NONCONVERT);
            keyPush(robot, new int[]{KeyEvent.VK_SHIFT, KeyEvent.VK_TAB});

            for (int i = shiftArgs; i < args.length; i++) {
                typing(robot, args[i]);
                keyPush(robot, KeyEvent.VK_TAB);
            }
            keyPush(robot, KeyEvent.VK_SPACE);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void typing(Robot robot, String inputValue) {
        char[] charArray = inputValue.toCharArray();
        for (char c: charArray) {
            if (!keyMap.containsKey(c)) {
                continue;
            }

            keyPush(robot, keyMap.get(c));
        }
    }

    private static void keyPush(Robot robot, int keycode) {
        keyPush(robot, new int[]{keycode});
    }

    private static void keyPush(Robot robot, int[] keycodes) {
        List<Integer> list = new ArrayList<>();
        Arrays.stream(keycodes).forEach(list::add);
        keyPush(robot, list);
    }

    private static void keyPush(Robot robot, Integer[] keycodes) {
        List<Integer> list = new ArrayList<>();
        Arrays.stream(keycodes).forEach(list::add);
        keyPush(robot, list);
    }

    private static void keyPush(Robot robot, List<Integer> list) {
        list.stream().forEach(robot::keyPress);
        Collections.reverse(list);
        list.stream().forEach(robot::keyRelease);
    }
}
