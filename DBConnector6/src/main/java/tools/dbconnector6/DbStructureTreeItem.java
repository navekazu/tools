package tools.dbconnector6;

import javafx.scene.control.TreeItem;

public class DbStructureTreeItem extends TreeItem<String> {
    public enum ItemType {
        TABLE("Table"),
        FUNCTION("Function"),
        PROCEDURE("Procedure"),
        SCHEMA("Schema");

        private String name;
        ItemType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private ItemType itemType;
    public DbStructureTreeItem(ItemType type, String value) {
        super(value);
        itemType = type;
    }
    public ItemType getItemType() {
        return itemType;
    }
}
