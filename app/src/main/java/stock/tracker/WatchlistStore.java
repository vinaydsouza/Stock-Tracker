package stock.tracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class WatchlistStore {
    private static final Path STORE_FILE = Path.of(System.getProperty("user.home"), ".stock-tracker-watchlist.json");

    public static record WatchlistData(Map<String, String> symbolGroups, Set<String> groups, String selectedGroup) {
        public WatchlistData {
            symbolGroups = Map.copyOf(symbolGroups);
            groups = Set.copyOf(groups);
            selectedGroup = selectedGroup == null ? "" : selectedGroup;
        }
    }

    public WatchlistData load() {
        if (!Files.exists(STORE_FILE)) {
            return new WatchlistData(new LinkedHashMap<>(), new LinkedHashSet<>(), "");
        }
        try {
            String text = Files.readString(STORE_FILE, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(text);
            JSONArray groupArray = json.optJSONArray("groups");
            Set<String> groups = new LinkedHashSet<>();
            if (groupArray != null) {
                for (int i = 0; i < groupArray.length(); i++) {
                    groups.add(groupArray.getString(i));
                }
            }
            JSONObject symbolMap = json.optJSONObject("symbols");
            Map<String, String> symbolGroups = new LinkedHashMap<>();
            if (symbolMap != null) {
                for (String key : symbolMap.keySet()) {
                    symbolGroups.put(key, symbolMap.getString(key));
                }
            }
            String selected = json.optString("selectedGroup", "");
            return new WatchlistData(symbolGroups, groups, selected);
        } catch (IOException e) {
            return new WatchlistData(new LinkedHashMap<>(), new LinkedHashSet<>(), "");
        }
    }
    public void save(Map<String, String> symbolGroups, Set<String> groups, String selectedGroup) {
        try {
            JSONObject json = new JSONObject();
            json.put("groups", new JSONArray(groups));
            JSONObject symbolsNode = new JSONObject();
            symbolGroups.forEach(symbolsNode::put);
            json.put("symbols", symbolsNode);
            json.put("selectedGroup", selectedGroup == null ? "" : selectedGroup);
            Files.writeString(STORE_FILE, json.toString(2), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save watchlist", e);
        }
    }
}
