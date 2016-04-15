package tools.dbconnector6.service;

import javafx.concurrent.Task;

/**
 *
 * @param <P>
 * @param <M>
 */
public interface BackgroundServiceInterface<P, M> {

    /**
     * バックグラウンドで実行する処理を実装する。<br>
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    public void run(Task task) throws Exception;

    /**
     * バックグラウンド実行をキャンセルするたびに呼び出される。<br>
     */
    public void cancel() throws Exception;

    /**
     * Serviceの状態がCANCELLED状態に遷移するたびに呼び出される。<br>
     */
    public void cancelled();

    /**
     * Serviceの状態がFAILED状態に遷移するたびに呼び出される。<br>
     */
    public void failed();

    public void updateUIPreparation(final P uiParam) throws Exception;
    public void updateUI(final M uiParam) throws Exception;

    /**
     * もし実行中ではない時にキャンセル要求があった場合のメッセージ。<br>
     * @return メッセージ
     */
    public String getNotRunningMessage();
}
