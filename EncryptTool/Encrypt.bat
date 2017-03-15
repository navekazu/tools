@rem create key file
@rem java -classpath "%REPOSITORY_HOME%\tools\EncryptTool\build\libs\EncryptTool-1.0.jar" tools.encrypttool.App -k %*

@rem encrypt
java -classpath "%REPOSITORY_HOME%\tools\EncryptTool\build\libs\EncryptTool-1.0.jar" tools.encrypttool.App %*
pause
