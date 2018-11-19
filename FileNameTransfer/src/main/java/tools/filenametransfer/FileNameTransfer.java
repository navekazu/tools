package tools.filenametransfer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileNameTransfer {
    private List<FileNameTransferBase> transferList;
    private String fromDirectory;
    private String toDirectory;
    private String workDirectory;

    public FileNameTransfer() {
    }

    private void initTransferList() throws IOException {
        transferList = new ArrayList<>();
        transferList.add(new SplitMainTitleAndSubTitle());
        transferList.add(new FullwidthToHalfwidth());

        // プレフィックスの作成
        DecimalFormat df = new DecimalFormat("00");
        for (int i=0; i<transferList.size(); i++) {
            transferList.get(i).setPrefixName(df.format(i+1));
        }

        // 基底workディレクトリの設定
        for (FileNameTransferBase t: transferList) {
            t.setBaseWorkDirectory(workDirectory);
        }

        // workディレクトリの作成
        for (FileNameTransferBase t: transferList) {
            String w = t.getWorkDirectory();
            Path p = Paths.get(w);
            Files.createDirectories(p);
        }

        // 最初の既定タスク(fromディレクトリから最初のタスクにファイルを転送する)
        NopTransfer firstTransfer = new NopTransfer();
        firstTransfer.setBaseWorkDirectory(fromDirectory);
        transferList.add(0, firstTransfer);

        // 最後の既定タスク(最後のタスクからtoディレクトリにファイルを転送する)
        NopTransfer lastTransfer = new NopTransfer();
        lastTransfer.setBaseWorkDirectory(toDirectory);
        transferList.add(lastTransfer);
    }

    private void run() throws IOException {
        for (int i=0; i<transferList.size(); i++) {
            FileNameTransferBase current = transferList.get(i);
            FileNameTransferBase next = (i+1<transferList.size())? transferList.get(i+1): null;
            List<Path> files;
            String workDirectory = current.getWorkDirectory();

            // ファイル名変更
            files = Files.list(Paths.get(workDirectory))
                    .filter(p-> !Files.isDirectory(p))
                    .collect(Collectors.toList());

            for (Path p: files) {
                current.process(workDirectory, p.getFileName().toString());
            }

            // ファイル移動
            if (next==null) {
                continue;
            }
            files = Files.list(Paths.get(workDirectory))
                    .filter(p-> !Files.isDirectory(p))
                    .collect(Collectors.toList());

            for (Path p: files) {
                Files.move(p, Paths.get(next.getWorkDirectory(), p.getFileName().toString()));
            }
        }
    }

    public static void main(String[] args) throws IOException {

        FileNameTransfer fnt = new FileNameTransfer();
        fnt.fromDirectory = args[0];
        fnt.toDirectory = args[1];
        fnt.workDirectory = args[2];

        fnt.initTransferList();
        fnt.run();
    }

}
