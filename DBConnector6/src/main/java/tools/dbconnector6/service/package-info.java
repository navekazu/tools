/**
 * バックグラウンド実行機能を提供する。<br>
 * バックグラウンド実行はBackgroundServiceクラスで行い、実行対象はBackgroundServiceInterfaceインターフェースを実装したクラスとなる。<br>
 * BackgroundServiceInterfaceインターフェースを実装したクラスは、下記がある。<br>
 * <ul>
 *     <li>DbStructureUpdateService ： 画面左上のデータベース構造を解析し表示するサービス。</li>
 *     <li>TableStructureTabPaneUpdateService ： 選択した画面左上のデータベース構造に応じて、画面左下のタブの制御を行うサービス。</li>
 *     <li>TableStructureUpdateService ： 選択した画面左上のデータベース構造に応じて、テーブルが選択されたらカラムの一覧やインデックスの一覧など、更なる解析を行うサービス。</li>
 *     <li>QueryExecuteService ： 画面右上のSQLクエリ入力欄で入力された内容、もしくは指定されたSQLファイルの内容を実行するサービス。</li>
 *     <li>ReservedWordUpdateService ： データベース接続時に取得できた全スキーマのテーブルやカラムの情報を取得する。取得した情報は予約語表示機能で利用する。</li>
 *     <li>SqlEditorLaunchService ： SQLエディタを起動し、終了するまで待機する。終了後は編集した内容をクエリ入力欄に反映する。</li>
 * </ul>
 */
package tools.dbconnector6.service;