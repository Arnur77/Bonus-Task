import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {
    static class BST {
        private static class Node {
            int key;
            Node left;
            Node right;

            Node(int key) {
                this.key = key;
            }
        }

        private Node root;
        private int size;

        public int size() {
            return size;
        }

        public void insert(int key) {
            root = insert(root, key);
        }

        private Node insert(Node node, int key) {
            if (node == null) {
                size++;
                return new Node(key);
            }

            if (key < node.key) {
                node.left = insert(node.left, key);
            } else if (key > node.key) {
                node.right = insert(node.right, key);
            }
            return node;
        }

        public boolean search(int key) {
            Node current = root;
            while (current != null) {
                if (key == current.key) {
                    return true;
                }
                current = key < current.key ? current.left : current.right;
            }
            return false;
        }

        public void delete(int key) {
            boolean existed = search(key);
            if (existed) {
                root = delete(root, key);
                size--;
            }
        }

        private Node delete(Node node, int key) {
            if (node == null) {
                return null;
            }

            if (key < node.key) {
                node.left = delete(node.left, key);
            } else if (key > node.key) {
                node.right = delete(node.right, key);
            } else {
                if (node.left == null) {
                    return node.right;
                }
                if (node.right == null) {
                    return node.left;
                }

                Node successor = minimum(node.right);
                node.key = successor.key;
                node.right = delete(node.right, successor.key);
            }
            return node;
        }

        private Node minimum(Node node) {
            while (node.left != null) {
                node = node.left;
            }
            return node;
        }
    }

    static class HashTable {
        private static class Entry {
            int key;
            Entry next;

            Entry(int key, Entry next) {
                this.key = key;
                this.next = next;
            }
        }

        private Entry[] buckets;
        private int size;
        private static final double MAX_LOAD_FACTOR = 0.75;

        HashTable(int initialCapacity) {
            buckets = new Entry[Math.max(4, initialCapacity)];
        }

        public int size() {
            return size;
        }

        private int index(int key) {
            return Math.floorMod(Integer.hashCode(key), buckets.length);
        }

        public void insert(int key) {
            if (search(key)) {
                return; // Duplicate keys are ignored.
            }

            if ((size + 1.0) / buckets.length > MAX_LOAD_FACTOR) {
                resize();
            }

            int bucket = index(key);
            buckets[bucket] = new Entry(key, buckets[bucket]);
            size++;
        }

        public boolean search(int key) {
            Entry current = buckets[index(key)];
            while (current != null) {
                if (current.key == key) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        public void delete(int key) {
            int bucket = index(key);
            Entry current = buckets[bucket];
            Entry previous = null;

            while (current != null) {
                if (current.key == key) {
                    if (previous == null) {
                        buckets[bucket] = current.next;
                    } else {
                        previous.next = current.next;
                    }
                    size--;
                    return;
                }
                previous = current;
                current = current.next;
            }
        }

        private void resize() {
            Entry[] oldBuckets = buckets;
            buckets = new Entry[oldBuckets.length * 2];
            size = 0;

            for (Entry entry : oldBuckets) {
                while (entry != null) {
                    insert(entry.key);
                    entry = entry.next;
                }
            }
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("FAIL: " + message);
        }
        System.out.println("PASS: " + message);
    }

    private static void runCorrectnessTests() {
        System.out.println("=== Correctness tests ===");

        BST tree = new BST();
        HashTable table = new HashTable(4);

        check(!tree.search(10) && !table.search(10), "empty structures do not contain keys");

        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int value : values) {
            tree.insert(value);
            table.insert(value);
        }
        check(tree.size() == 7 && table.size() == 7, "insert stores seven unique keys");
        check(tree.search(40) && table.search(40), "both structures find an existing key");
        check(!tree.search(99) && !table.search(99), "both structures reject a missing key");

        tree.insert(40);
        table.insert(40);
        check(tree.size() == 7 && table.size() == 7, "duplicate insertion is ignored");

        tree.delete(50);
        table.delete(50);
        check(!tree.search(50) && !table.search(50), "deleting root removes the key");
        check(tree.search(20) && tree.search(40) && tree.search(60) && tree.search(80),
                "BST keeps other keys after deleting a node with two children");
        check(table.search(20) && table.search(40) && table.search(60) && table.search(80),
                "Hash Table keeps other keys after deletion");

        tree.delete(999);
        table.delete(999);
        check(tree.size() == table.size(), "deleting a missing key changes neither size");


        HashTable collisionTable = new HashTable(4);
        for (int i = 0; i < 100; i++) {
            collisionTable.insert(i * 4);
        }
        boolean allFound = true;
        for (int i = 0; i < 100; i++) {
            allFound &= collisionTable.search(i * 4);
        }
        check(allFound && collisionTable.size() == 100, "Hash Table handles collisions and resizing");

        for (int i = 0; i < 100; i++) {
            collisionTable.delete(i * 4);
        }
        check(collisionTable.size() == 0 && !collisionTable.search(0),
                "Hash Table deletes all keys");

        System.out.println("All correctness tests passed.\n");
    }

    private static int[] makeKeys(int n, long seed) {
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) {
            keys[i] = i;
        }

        Random random = new Random(seed);
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = keys[i];
            keys[i] = keys[j];
            keys[j] = temp;
        }
        return keys;
    }

    private static long timeInsertBST(int[] keys) {
        BST tree = new BST();
        long start = System.nanoTime();
        for (int key : keys) {
            tree.insert(key);
        }
        long elapsed = System.nanoTime() - start;
        if (tree.size() != keys.length) {
            throw new IllegalStateException("BST insertion correctness check failed");
        }
        return elapsed;
    }

    private static long timeInsertHash(int[] keys) {
        HashTable table = new HashTable(4);
        long start = System.nanoTime();
        for (int key : keys) {
            table.insert(key);
        }
        long elapsed = System.nanoTime() - start;
        if (table.size() != keys.length) {
            throw new IllegalStateException("Hash Table insertion correctness check failed");
        }
        return elapsed;
    }

    private static long timeSearchBST(int[] keys) {
        BST tree = new BST();
        for (int key : keys) {
            tree.insert(key);
        }
        long checksum = 0;
        long start = System.nanoTime();
        for (int key : keys) {
            if (tree.search(key)) {
                checksum++;
            }
        }
        long elapsed = System.nanoTime() - start;
        if (checksum != keys.length) {
            throw new IllegalStateException("BST search correctness check failed");
        }
        return elapsed;
    }

    private static long timeSearchHash(int[] keys) {
        HashTable table = new HashTable(4);
        for (int key : keys) {
            table.insert(key);
        }
        long checksum = 0;
        long start = System.nanoTime();
        for (int key : keys) {
            if (table.search(key)) {
                checksum++;
            }
        }
        long elapsed = System.nanoTime() - start;
        if (checksum != keys.length) {
            throw new IllegalStateException("Hash Table search correctness check failed");
        }
        return elapsed;
    }

    private static long timeDeleteBST(int[] keys) {
        BST tree = new BST();
        for (int key : keys) {
            tree.insert(key);
        }
        long start = System.nanoTime();
        for (int key : keys) {
            tree.delete(key);
        }
        long elapsed = System.nanoTime() - start;
        if (tree.size() != 0) {
            throw new IllegalStateException("BST deletion correctness check failed");
        }
        return elapsed;
    }

    private static long timeDeleteHash(int[] keys) {
        HashTable table = new HashTable(4);
        for (int key : keys) {
            table.insert(key);
        }
        long start = System.nanoTime();
        for (int key : keys) {
            table.delete(key);
        }
        long elapsed = System.nanoTime() - start;
        if (table.size() != 0) {
            throw new IllegalStateException("Hash Table deletion correctness check failed");
        }
        return elapsed;
    }

    private static long[] measure(int n, int repetitions) {
        long[] totals = new long[6];

        for (int run = 0; run <= repetitions; run++) {
            int[] keys = makeKeys(n, 12345L + run);
            long[] times = {
                timeInsertBST(keys),
                timeInsertHash(keys),
                timeSearchBST(keys),
                timeSearchHash(keys),
                timeDeleteBST(keys),
                timeDeleteHash(keys)
            };

            if (run > 0) {
                for (int i = 0; i < totals.length; i++) {
                    totals[i] += times[i];
                }
            }
        }

        for (int i = 0; i < totals.length; i++) {
            totals[i] /= repetitions;
        }
        return totals;
    }

    public static void main(String[] args) {
        runCorrectnessTests();

        int[] sizes = {100, 1000, 10000};
        int repetitions = 5;
        String header = "n,BST_insert_ns,Hash_insert_ns,BST_search_ns,Hash_search_ns,BST_delete_ns,Hash_delete_ns";

        System.out.println("=== Benchmark results (average of " + repetitions
                + " runs after one warm-up) ===");
        System.out.println(header);

        try (FileWriter csv = new FileWriter("results.csv")) {
            csv.write(header + System.lineSeparator());

            for (int n : sizes) {
                long[] result = measure(n, repetitions);
                StringBuilder line = new StringBuilder().append(n);
                for (long value : result) {
                    line.append(",").append(value);
                }
                System.out.println(line);
                csv.write(line + System.lineSeparator());
            }

            System.out.println("\nSaved results to results.csv.");
        } catch (IOException e) {
            System.err.println("Could not write results.csv: " + e.getMessage());
        }
    }
}
