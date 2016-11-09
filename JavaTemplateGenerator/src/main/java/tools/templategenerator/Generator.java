package tools.templategenerator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Generator {
    static final String[] GENERATE_FILES_AT_PROJECT_DIRECTORY = {
            "build.gradle",
            "pom.xml",
    };
    static final String[] GENERATE_FILES_AT_MAIN_DIRECTORY = {
            "App.java",
    };
    static final String[] GENERATE_FILES_AT_TEST_DIRECTORY = {
            "AppTest.java",
    };

    public Generator() {

    }

    public void generate(GeneratorParam param) throws IOException {
        createProjectDirectory(param);
        createPackageDirectory(param);
        createFile(param);

    }

    void createProjectDirectory(GeneratorParam param) throws IOException {
        Files.createDirectories(Paths.get(param.getCurrentPath(), param.getProjectName()));
    }

    void createPackageDirectory(GeneratorParam param) throws IOException {
        createSubPackageDirectory("main", param);
        createSubPackageDirectory("test", param);
    }

    private void createSubPackageDirectory(String subName, GeneratorParam param) throws IOException {
        Files.createDirectories(getResourcesDirectory(subName, param));
        Files.createDirectories(getPackageDirectory(subName, param));
    }
    private Path getResourcesDirectory(String subName, GeneratorParam param) {
        Path projectDirectory = Paths.get(param.getCurrentPath(), param.getProjectName());          // someProject/
        Path srcDirectory = Paths.get(projectDirectory.toString(), "src");                          // someProject/src/
        Path subDirectory = Paths.get(srcDirectory.toString(), subName);                            // someProject/src/main(or test)/
        Path resourcesDirectory = Paths.get(subDirectory.toString(), "resources");                  // someProject/src/main(or test)/resources

        return resourcesDirectory;
    }
    private Path getPackageDirectory(String subName, GeneratorParam param) {
        Path projectDirectory = Paths.get(param.getCurrentPath(), param.getProjectName());          // someProject/
        Path srcDirectory = Paths.get(projectDirectory.toString(), "src");                          // someProject/src/
        Path subDirectory = Paths.get(srcDirectory.toString(), subName);                            // someProject/src/main(or test)/
        Path javaDirectory = Paths.get(subDirectory.toString(), "java");                            // someProject/src/main(or test)/java
        Path packageDirectory = Paths.get(javaDirectory.toString());                                // someProject/src/main(or test)/java/package

        for (String path: param.getPackageName().split("\\.")) {
            packageDirectory = Paths.get(packageDirectory.toString(), path);
        }

        return packageDirectory;

    }

    List<String> loadTemplate(GeneratorParam param, String fileName) throws IOException {
        if (!existsResourceFileInHomeDirectory(fileName)) {
            copyResourceFileToHomeDirectory(fileName);
        }
        return Files.readAllLines(getResourceFileNameInHomeDirectory(fileName));
    }

    private Path getResourceFileNameInHomeDirectory(String fileName) throws IOException {
        if (!Files.exists(Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator"))) {
            Files.createDirectories(Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator"));
        }
        return Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator", fileName);
    }

    private boolean existsResourceFileInHomeDirectory(String fileName) throws IOException {
        return Files.exists(getResourceFileNameInHomeDirectory(fileName));
    }

    private void copyResourceFileToHomeDirectory(String fileName) throws IOException {
        InputStream i = ClassLoader.getSystemResourceAsStream(fileName);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName)));
            PrintWriter out = new PrintWriter(new FileOutputStream(getResourceFileNameInHomeDirectory(fileName).toFile()))) {
            String line;
            while ((line=in.readLine())!=null) {
                out.println(line);
            }
        }
    }

    private void createFile(GeneratorParam param) throws IOException {
        for (String fileName: GENERATE_FILES_AT_PROJECT_DIRECTORY) {
            Path path = Paths.get(param.getCurrentPath(), param.getProjectName(), fileName);
            if (!Files.exists(path)) {
                copy(getResourceFileNameInHomeDirectory(fileName), path, param);
            }
        }
        for (String fileName: GENERATE_FILES_AT_MAIN_DIRECTORY) {
            Path path = Paths.get(param.getCurrentPath(), getPackageDirectory("main", param).toString(), fileName);
            if (!Files.exists(path)) {
                copy(getResourceFileNameInHomeDirectory(fileName), path, param);
            }
        }
        for (String fileName: GENERATE_FILES_AT_TEST_DIRECTORY) {
            Path path = Paths.get(param.getCurrentPath(), getPackageDirectory("test", param).toString(), fileName);
            if (!Files.exists(path)) {
                copy(getResourceFileNameInHomeDirectory(fileName), path, param);
            }
        }
    }
    private void copy(Path source, Path target, GeneratorParam param) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source.toFile())));
             PrintWriter out = new PrintWriter(new FileOutputStream(target.toFile()))) {
            String line;
            while ((line=in.readLine())!=null) {
                line = line.replaceAll("\\$\\{projectName\\}", param.getProjectName());
                line = line.replaceAll("\\$\\{packageName\\}", param.getPackageName());
                out.println(line);
            }
        }
    }
}
