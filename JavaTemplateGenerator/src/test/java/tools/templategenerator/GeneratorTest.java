package tools.templategenerator;

import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class GeneratorTest {
    @BeforeClass
    public static void beforeClass() throws Exception {
    }

    @AfterClass
    public static void afterClass() throws Exception {
    }

    @Before
    public void before() throws Exception {
/*
        if (Files.exists(Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator"))) {
            for (String path: Generator.GENERATE_FILES) {
                Files.deleteIfExists(Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator", path));
            }
            Files.deleteIfExists(Paths.get(System.getProperty("user.home"), ".JavaTemplateGenerator"));
        }
*/
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void loadTemplateTest() throws IOException {
        Generator generator = new Generator();
        GeneratorParam param = GeneratorParam.builder()
                .currentPath(".")
                .projectName("SampleProject")
                .packageName("sample.project")
                .build();

        for (String path: Generator.GENERATE_FILES_AT_PROJECT_DIRECTORY) {
            generator.loadTemplate(param, path);
        }
        for (String path: Generator.GENERATE_FILES_AT_MAIN_DIRECTORY) {
            generator.loadTemplate(param, path);
        }
        for (String path: Generator.GENERATE_FILES_AT_TEST_DIRECTORY) {
            generator.loadTemplate(param, path);
        }
        assertTrue(true);
    }

    @Test
    public void createProjectDirectoryTest() throws IOException {
        Generator generator = new Generator();
        GeneratorParam param = GeneratorParam.builder()
                .currentPath(".")
                .projectName("SampleProject")
                .packageName("sample.project")
                .build();

        generator.createProjectDirectory(param);
        assertTrue(Files.exists(Paths.get(".", "SampleProject")));
    }
    @Test
    public void createPackageDirectoryTest() throws IOException {
        Generator generator = new Generator();
        GeneratorParam param = GeneratorParam.builder()
                .currentPath(".")
                .projectName("SampleProject")
                .packageName("sample.project")
                .build();

        generator.createPackageDirectory(param);
        assertTrue(Files.exists(Paths.get(".", "SampleProject")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "main")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "main", "java")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "main", "resources")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "test")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "test", "java")));
        assertTrue(Files.exists(Paths.get(".", "SampleProject", "src", "test", "resources")));
    }

    @Test
    public void generateTest() throws IOException {
        Generator generator = new Generator();
        GeneratorParam param = GeneratorParam.builder()
                .currentPath(".")
                .projectName("SampleProject2")
                .packageName("sample.project")
                .build();
        generator.generate(param);
    }
}
