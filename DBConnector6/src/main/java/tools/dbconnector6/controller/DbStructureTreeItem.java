package tools.dbconnector6.controller;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * メイン画面左上のデータベース構造ツリーに使用するツリーアイテム。<br>
 */
public class DbStructureTreeItem extends TreeItem<String> implements Comparable {
    /**
     * ツリーアイテムの種類。<br>
     * ツリーアイテムを用いて、以下のようなツリーが構成される。<br>
     * <pre>
     *   DATABASE
     *     + SCHEMA
     *         + GROUP
     *             + TABLE
     *         + GROUP
     *             + FUNCTION
     *         + GROUP
     *             + PROCEDURE
     * </pre>
     */
    public enum ItemType {
        /**
         * データベース。必ずルート。<br>
         */
        DATABASE("DATABASE", "image/database.png"),

        /**
         * データベースにぶら下がる「スキーマ」。<br>
         */
        SCHEMA("SCHEMA", "image/schema.png"),

        /**
         * スキーマにぶら下がる「グループ」。<br>
         */
        GROUP("Group", null),

        /**
         * グループにぶら下がる「テーブル」。<br>
         * テーブル、ビュー、シノニムなどが該当する。<br>
         */
        TABLE("TABLE", "image/table.png"),

        /**
         * グループにぶら下がる「ファンクション」。<br>
         */
        FUNCTION("FUNCTION", "image/function.png"),

        /**
         * グループにぶら下がる「プロシージャ」。<br>
         */
        PROCEDURE("PROCEDURE", "image/procedure.png"),
        ;

        private String name;        // 表示内容
        private String imagePath;   // イメージファイルへのリソースパス

        /**
         * コンストラクタ
         * @param name 表示内容
         * @param imagePath イメージファイルへのリソースパス
         */
        ItemType(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        /**
         * 表示内容を返す。<br>
         * @return 表示内容
         */
        public String getName() {
            return name;
        }

        /**
         * 読み込んだイメージを返す。
         * @return 読み込んだイメージデータ
         */
        public final Node getImage() {
            if (imagePath==null) {
                return null;
            }

            Image image = new Image(imagePath);
            ImageView imageView = new ImageView();
            imageView.setImage(image);

            return imageView;
        }
    }

    private ItemType itemType;      // ツリーアイテムの種類
    private String schema;          // 取得元のスキーマ名

    /**
     * コンストラクタ。<br>
     * @param type ツリーアイテムの種類
     * @param value 表示内容
     * @param schema 取得元のスキーマ名
     */
    public DbStructureTreeItem(ItemType type, String value, String schema) {
        super(value);
        this.itemType = type;
        this.schema = schema;
        setGraphic(itemType.getImage());
    }

    /**
     * ツリーアイテムの種類を返す。<br>
     * @return ツリーアイテムの種類
     */
    public ItemType getItemType() {
        return itemType;
    }

    /**
     * 取得元のスキーマ名を返す。<br>
     * @return 取得元のスキーマ名
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Comparableの実装で、表示内容を辞書的に比較した結果を返す。
     * @param o 比較対象のツリーアイテム
     * @return 表示内容が等しい場合は 0。
     *          自身の表示内容が指定された表示内容より辞書式に小さい場合は、0 より小さい値。
     *          自身の表示内容がが指定された表示内容より辞書式に大きい場合は、0 より大きい値
     */
    @Override
    public int compareTo(Object o) {
        return getValue().compareTo(((TreeItem<String>)o).getValue());
    }
}
