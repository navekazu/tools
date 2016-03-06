package tools.dbconnector6;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DbStructureTreeItem extends TreeItem<String> implements Comparable {
    public enum ItemType {
        DATABASE("DATABASE", "image/database.png"),
        GROUP("Group", null),
        TABLE("TABLE", "image/table.png"),
        FUNCTION("FUNCTION", "image/function.png"),
        PROCEDURE("PROCEDURE", "image/procedure.png"),
        SCHEMA("SCHEMA", "image/schema.png");

        private String name;
        private String imagePath;
        ItemType(String name, String imagePath) {
            this.name = name;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public final Node getImage() {
            if (imagePath==null) {
                return null;
            }

            Image image = new Image(imagePath);
            ImageView iv1 = new ImageView();
            iv1.setImage(image);
            return iv1;
        }
    }

    private ItemType itemType;
    private String schema;
    public DbStructureTreeItem(ItemType type, String value, String schema) {
        super(value);
        this.itemType = type;
        this.schema = schema;
        setGraphic(itemType.getImage());

    }
    public ItemType getItemType() {
        return itemType;
    }
    public String getSchema() {
        return schema;
    }

    @Override
    public int compareTo(Object o) {
        return getValue().compareTo(((TreeItem<String>)o).getValue());
    }
}
