package tools.encrypttool;

import tools.cli.CLIParameterParser;
import tools.cli.CLIParameterRule;
import tools.cli.CLIParameters;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class App {
    private static final String ENCRYPTED_FILE_DELIMITER = ".encrypted";
    static final String KEY_FILE_PATH = ".EncryptToolKeyFile";

    public App() {
    }
    public static void main(String[] args) {
        CLIParameters cliParameters = CLIParameterParser.parseUnixStyle(
                CLIParameterRule.builder()
                        .needValueParameters(new String[] {"k", "p"})
                        .build()
                , args
        );

        // -kと-pの両方が指定されていたら終了
        if (cliParameters.options.containsKey("k") && cliParameters.options.containsKey("p")) {
            showUsage();
            return ;
        }

        // -k？ （キーファイル作成モード）
        if (cliParameters.options.containsKey("k")) {
            final String password = cliParameters.options.get("k");

            if (password==null) {
                showUsage();
                return ;
            }

            App app = new App();
            app.createKeyFile(password);
            return ;
        }

        // -p？ （暗号化・復号化モード）
        final String password = cliParameters.options.get("p");
        final List<String> inputFiles = cliParameters.operands;

        if (inputFiles.size()==0) {
            showUsage();
            return ;
        }

        inputFiles.parallelStream()
                .forEach(inputFile -> {
                    App app = new App();
                    app.execute(password, inputFile);
                });
        return ;
    }

    void createKeyFile(String password) {
        createKeyFile(password, KEY_FILE_PATH);
    }

    void createKeyFile(String password, String keyFilePath) {
        try {
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            byte[] bytePassword = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            Path path = Paths.get(System.getProperty("user.home"), keyFilePath);

            for (byte b: bytePassword) {
                sb.append(String.format("%02x", b));
            }

            try (PrintWriter out = new PrintWriter(new FileOutputStream(path.toFile()))) {
                out.print(sb.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    byte[] readKeyFile() {
        return readKeyFile(KEY_FILE_PATH);
    }

    byte[] readKeyFile(String keyFilePath) {
        try {
            List<String> list = Files.readAllLines(Paths.get(System.getProperty("user.home"), keyFilePath));
            List<Byte> result = new ArrayList<>();

            if (list.size()==0) {
                return null;
            }

            String key = list.get(0);
            int size = key.length()/2;
            for (int i = 0; i < size; i++){
                result.add((byte)Integer.parseInt(key.substring(i*2, i*2+2), 16));
            }

            byte[] byteArray = new byte[result.size()];
            size = result.size();
            for (int i = 0; i < size; i++){
                byteArray[i] = result.get(i);
            }

            return byteArray;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void showUsage() {
        
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
            byte[] bytePassword;
            if (password!=null) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(password.getBytes());

                bytePassword = messageDigest.digest();
            } else {
                bytePassword = readKeyFile();
            }

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