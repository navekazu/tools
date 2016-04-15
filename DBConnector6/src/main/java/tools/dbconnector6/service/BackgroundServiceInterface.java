package tools.dbconnector6.service;

import javafx.concurrent.Task;

/**
 * バックグラウンドサービス実行用インターフェース。<br>
 * @param <P> 更新の前処理に必要なパラメータのタイプ
 * @param <U> 更新処理に必要なパラメータのタイプ
 */
public interface BackgroundServiceInterface<P, U> {

    /**
     * バックグラウンドで実行する処理を実装する。<br>
     * @param task 生成したバックグラウンド実行を行うTaskのインスタンス
     * @throws Exception 何らかのエラーが発生し処理を中断する場合
     */
    public void run(Task task) throws Exception;

    /**
     * 更新の前処理。<br>
     * 画面項目のクリアなど、更新結果を反映する前の準備を実装する。<br>
     * @param prepareUpdateParam 前処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    public void prepareUpdate(final P prepareUpdateParam) throws Exception;

    /**
     * 更新処理。<br>
     * バックグラウンドで実行した結果を画面等に反映する。<br>
     * @param updateParam 更新処理に必要なパラメータ
     * @throws Exception 何らかのエラーが発生した場合
     */
    public void update(final U updateParam) throws Exception;

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

    /**
     * もし実行中ではない時にキャンセル要求があった場合のメッセージ。<br>
     * @return メッセージ
     */
    public String getNotRunningMessage();
}
