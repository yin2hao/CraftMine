import java.util.ArrayList;
import java.util.List;

// 背包类实现，包含物品存储获取操作
public class Inventory {
    // 背包容量max
    private final int MAX_SIZE;
    // 储存物品列表
    private final List<Item> items;
    // 记录已使用的格子数
    private int usedSlots;

    // 用方法初始化背包
    public Inventory(int size) {
        this.MAX_SIZE = size;
        this.items = new ArrayList<>(size);
        this.usedSlots = 0;
        // 这时填充空的物品槽
        for (int i = 0; i < size; i++) {
            items.add(new Item("空槽", 0, -1)); // maxStack设为-1表示空槽
        }
    }

    // 构造方法，使用默认容量
    public Inventory() {
        this(27);
    }

    // 添加物品
    public boolean addItem(Item item) {
        if (isFull()) {
            return false; // 满了
        }

        // 首先尝试堆叠到相同类型的物品槽
        for (int i = 0; i < items.size(); i++) {
            Item existing = items.get(i);
            if (existing.isSameType(item) && existing.getCount() < existing.getMaxStack()) {
                int availableSpace = existing.getMaxStack() - existing.getCount();
                int transferAmount = Math.min(availableSpace, item.getCount());

                existing.setCount(existing.getCount() + transferAmount);
                item.setCount(item.getCount() - transferAmount);

                if (item.getCount() <= 0) {
                    return true;
                }
            }
        }

        // 没有可堆叠的槽，尝试放入空槽
        for (int i = 0; i < items.size(); i++) {
            Item slot = items.get(i);
            if (slot.isAir()) {
                items.set(i, item);
                usedSlots++;
                return true;
            }
        }

        return false; // 用来示警
    }

    // 把背包中物品移除
    public Item removeItem(int index, int count) {
        if (index < 0 || index >= items.size()) {
            return new Item("空槽", 0, -1); // 索引出错
        }

        Item item = items.get(index);
        if (item.isAir()) {
            return new Item("空槽", 0, -1); // 空槽
        }

        if (count <= 0) {
            return new Item("空槽", 0, -1); // 移除数量不合法
        }

        if (count >= item.getCount()) { // 清空物品
            items.set(index, new Item("空槽", 0, -1));
            usedSlots--;
            return new Item(item.getName(), item.getCount(), item.getMaxStack());
        } else {
            // 移除部分
            Item removed = new Item(item.getName(), count, item.getMaxStack());
            item.setCount(item.getCount() - count);
            return removed;
        }
    }

    // 获取指定位置物品
    public Item getItem(int index) {
        if (index < 0 || index >= items.size()) {
            return new Item("空槽", 0, -1);
        }
        return items.get(index);
    }

    // 检查背包满了没有
    public boolean isFull() {
        return usedSlots >= MAX_SIZE;
    }

    public boolean isEmpty() {
        return usedSlots == 0;
    }

    // 交换两个物品槽物品
    public void swapItems(int index1, int index2) {
        if (index1 < 0 || index1 >= items.size() || index2 < 0 || index2 >= items.size()) {
            return; // 依旧索引无效
        }

        Item temp = items.get(index1);
        items.set(index1, items.get(index2));
        items.set(index2, temp);
    }

    public void display() {
        System.out.println("===== 背包内容 =====");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (!item.isAir()) {
                System.out.println(i + ": " + item.getName() + " x" + item.getCount());
            } else {
                System.out.println(i + ": 空槽");
            }
        }
        System.out.println("已使用格子: " + usedSlots + "/" + MAX_SIZE);
        System.out.println("===================");
    }

    // 物品类内部实现
    public static class Item {
        private String name;
        private int count;
        private int maxStack;

        // 最大堆叠数量
        public Item(String name, int count, int maxStack) {
            this.name = name;
            this.count = count;
            this.maxStack = maxStack;
        }

        // 判断是否为同一种类型物品
        public boolean isSameType(Item other) {
            return this.name.equals(other.name) && this.maxStack == other.maxStack;
        }

        // 判断是否为空槽
        public boolean isAir() {
            return "空槽".equals(name) && count == 0 && maxStack == -1;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public int getCount() {
    return count;
}

public void setCount(int count) {
    this.count = count;
}

public int getMaxStack() {
    return maxStack;
}

public void setMaxStack(int maxStack) {
    this.maxStack = maxStack;
}
    }

public static void main(String[] args) {
    // 创建一个背包
    Inventory inventory = new Inventory();
    // 添加物品
    Item apple = new Item("苹果", 5, 64);
    Item bread = new Item("面包", 10, 64);
    Item diamond = new Item("钻石", 3, 64);

    inventory.addItem(apple);
    inventory.addItem(bread);
    inventory.addItem(diamond);

    Item moreApples = new Item("苹果", 70, 64);  // 超过堆叠上限的测试
    boolean added = inventory.addItem(moreApples);
    System.out.println("物品添加: " + (added ? "成功" : "失败"));
    // 显示背包内容
    inventory.display();
    // 移除物品
    Item removed = inventory.removeItem(0, 3);
    System.out.println("移除了: " + removed.getName() + " x" + removed.getCount());
    // 再展示一次背包内部
    inventory.display();
}
}