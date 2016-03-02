package tools.dbconnector6;

import javafx.scene.control.TreeItem;

public class DbStructureTreeItem extends TreeItem<String> implements Comparable {
    public enum ItemType {
        DATABASE("DATABASE"),
        GROUP("Group"),
        TABLE("TABLE"),
        FUNCTION("FUNCTION"),
        PROCEDURE("PROCEDURE"),
        SCHEMA("SCHEMA");

        private String name;
        ItemType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private ItemType itemType;
    private String schema;
    public DbStructureTreeItem(ItemType type, String value, String schema) {
        super(value);
        this.itemType = type;
        this.schema = schema;

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
