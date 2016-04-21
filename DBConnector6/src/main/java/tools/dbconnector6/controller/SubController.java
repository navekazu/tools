package tools.dbconnector6.controller;

import tools.dbconnector6.MainControllerInterface;

/**
 * メインコントローラから呼び出されるサブ画面のコントローラ基底クラス
 */
public abstract class SubController {
    // メイン画面へのアクセス用インターフェース
    protected MainControllerInterface mainControllerInterface;

    /**
     * メイン画面へのアクセス用インターフェースを設定する。<br>
     * @param mainControllerInterface メイン画面へのアクセス用インターフェースへの参照
     */
    public void setMainControllerInterface(MainControllerInterface mainControllerInterface) {
        this.mainControllerInterface = mainControllerInterface;
    }
}
