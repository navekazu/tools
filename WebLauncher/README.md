#WebLauncher
WebLauncher is boot the browser with selected URL and input search word.

#Required JDK version
Java 8 or later.

#Building WebLauncher
*Required Maven.

- Clone it using Git: `https://github.com/navekazu/tools.git`
- cd into tools/WebLauncher
- Build it using Maven: `mvn compile`
- Make the executable jar file. Using Maven: `mvn assembly:single`

#How to use
Boot the WebLauncher.
- cd into target
- Execute the `WebLauncher-1.0-SNAPSHOT-jar-with-dependencies.jar` jar file.

#Change the default launch browser
You can change default launch browser.
- Open the `~/.WebLauncherBrowserPath` with the text editor.
- Update the path of the browser to the top line.

#Change the default URL list
You can change default URL list.
- Open the `~/.WebLauncherUrl` with the text editor.
- Update the URL list.

