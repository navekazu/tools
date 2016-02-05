package tools.weblauncher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AppTest {
    @Before
    public void before() {
    }

    @After
    public void after() {
    }

    @Test
    public void loadDefaultLaunchTemplateTest() {
        MainController ctl = new MainController();

        try {
            Path defaultWebLauncherBrowserPath = Paths.get(System.getProperty("user.home"), ".WebLauncherBrowserPath");
            if (Files.exists(defaultWebLauncherBrowserPath)) {
                Files.delete(defaultWebLauncherBrowserPath);
            }
            Path defaultWebLauncherTemplate = Paths.get(System.getProperty("user.home"), ".WebLauncherUrl");
            if (Files.exists(defaultWebLauncherTemplate)) {
                Files.delete(defaultWebLauncherTemplate);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<String> list;

            // WebLauncherBrowserPathが削除され、新規作成した結果を検証
            list = ctl.loadTemplate(MainController.DEFAULT_LAUNCH_BROWSER_PATH, new String[]{"C:\\Program Files\\Mozilla Firefox\\firefox.exe"});

            assertEquals(list.size(), 1);
            assertEquals(list.get(0), "C:\\Program Files\\Mozilla Firefox\\firefox.exe");

            // WebLauncherTemplateが削除され、新規作成した結果を検証
            list = ctl.loadTemplate(MainController.DEFAULT_LAUNCH_URL, new String[]{"http://eow.alc.co.jp/search?q=${searchWord}", "https://www.google.co.jp/search?q=${searchWord}"});

            assertEquals(list.size(), 2);
            assertEquals(list.get(0), "http://eow.alc.co.jp/search?q=${searchWord}");
            assertEquals(list.get(1), "https://www.google.co.jp/search?q=${searchWord}");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
