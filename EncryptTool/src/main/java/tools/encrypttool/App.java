package tools.encrypttool;

import javax.crypto.Cipher;
import java.util.Arrays;

public class App {
    private static final String ENCRYPTED_FILE_DELIMITER = ".encrypted";

    public App() {
    }
    public static void main(String[] args) {
        Arrays.asList(args).stream()
                .forEach(arg -> {
                    App app = new App();
                    app.execute(arg);
                });
    }

    void execute(String file) {

    }

    int getOpmode(String file) {
        return file.endsWith(ENCRYPTED_FILE_DELIMITER)? Cipher.DECRYPT_MODE: Cipher.ENCRYPT_MODE;
    }

    String getOutputFileName(String file) {
        if (file.endsWith(ENCRYPTED_FILE_DELIMITER)) {
            return file.substring(0, file.length()-ENCRYPTED_FILE_DELIMITER.length());
        } else {
            return file+ENCRYPTED_FILE_DELIMITER;
        }
    }

    void update(int opmode, String inputFile, String outputFile) {

    }
}