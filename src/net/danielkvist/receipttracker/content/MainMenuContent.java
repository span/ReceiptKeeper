package net.danielkvist.receipttracker.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.danielkvist.receipttracker.R;

public class MainMenuContent
{

    public static class DummyItem
    {

        public String id;
        public String content;

        public DummyItem(String id, String content)
        {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString()
        {
            return content;
        }
    }

    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static
    {
        addItem(new DummyItem("1", "Add"));
        addItem(new DummyItem("2", "Search"));
        addItem(new DummyItem("3", "Settings"));
    }

    private static void addItem(DummyItem item)
    {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
