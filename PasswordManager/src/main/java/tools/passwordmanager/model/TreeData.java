package tools.passwordmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// see -> http://qiita.com/opengl-8080/items/f7112240c72d61d4cdf4
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeData {
    private String name;
    private List<TreeData> childList = new ArrayList<>();
    private List<Pair> pairs = new ArrayList<>();

    public void addPairs(String key, String value) {
        addPairs(new Pair(key, value));
    }

    public void addPairs(Pair pair) {
        pairs.add(pair);
    }

    public String getValue(String key) {
        for (Pair pair: pairs) {
            if (key.equals(pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }

    public void removeValue(String key) {
        for (Pair pair: pairs) {
            if (key.equals(pair.getKey())) {
                pairs.remove(pair);
                break;
            }
        }
    }

    public void addChild(TreeData treeData) {
        childList.add(treeData);
    }
    public String toString() {
        return name;
    }
}
