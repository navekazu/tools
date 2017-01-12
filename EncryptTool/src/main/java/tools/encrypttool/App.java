package tools.encrypttool;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    private static final String ENCRYPTED_FILE_DELIMITER = ".encrypted";

    public App() {
    }
    public static void main(String[] args) {
        if (args.length<=1) {
            return ;
        }

        final String password = args[0];
        final List<String> inputFiles = getInputFiles(args);

        inputFiles.parallelStream()
                .forEach(inputFile -> {
                    App app = new App();
                    app.execute(password, inputFile);
                });
    }

    static List<String> getInputFiles(String[] args) {
        return Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
    }

    boolean execute(String password, String inputFile) {
        int opmode = getOpmode(inputFile);
        String outputFile = getOutputFileName(inputFile);
        return update(opmode, inputFile, password, outputFile);
    }

    int getOpmode(String inputFile) {
        return inputFile.endsWith(ENCRYPTED_FILE_DELIMITER)? Cipher.DECRYPT_MODE: Cipher.ENCRYPT_MODE;
    }

    String getOutputFileName(String inputFile) {
        if (inputFile.endsWith(ENCRYPTED_FILE_DELIMITER)) {
            return inputFile.substring(0, inputFile.length()-ENCRYPTED_FILE_DELIMITER.length());
        } else {
            return inputFile+ENCRYPTED_FILE_DELIMITER;
        }
    }

    boolean update(int opmode, String inputFile, String password, String outputFile) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());

            byte[] bytePassword = messageDigest.digest();
            SecretKeySpec key = new SecretKeySpec(bytePassword, "AES");

            byte[] byteIv = "abcdefghijklmnop".getBytes("UTF-8");
            IvParameterSpec iv = new IvParameterSpec(byteIv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, key, iv);

            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
                 CipherOutputStream out = new CipherOutputStream(new FileOutputStream(outputFile), cipher)) {
                int data;
                while ((data=in.read())!=-1) {
                    out.write(data);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return false;
    }
}