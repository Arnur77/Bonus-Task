# BST vs Hash Table Performance Analysis

## Goal

This project compares insertion, searching, and deletion in:
- an unbalanced Binary Search Tree (BST)
- a hash table using separate chaining and resizing

The program tests 100, 1,000, and 10,000 unique integer keys. The keys are shuffled before insertion. Each operation is timed with `System.nanoTime()`. The program runs one warm-up and averages five measured runs. It also runs correctness tests before the benchmark.

## How to run

Open `Main.java` in IntelliJ IDEA and run it, or use a terminal in the folder containing the file:

```bash
javac Main.java
java Main
```

The program prints correctness-test results and benchmark results, then writes the measured data to `results.csv` in the working directory. After the final run, use the values from that CSV in this README.

## Time complexity

Let `n` be the number of stored keys and `h` the height of the BST.

| Structure / operation | Best case | Average case | Worst case |
|---|---:|---:|---:|
| BST insert | O(1), insertion at an empty root | O(log n) for a reasonably balanced/random tree | O(n) |
| BST search | O(1), key at the root | O(log n) for a reasonably balanced/random tree | O(n) |
| BST delete | O(1) in a simple root-leaf case | O(log n) for a reasonably balanced/random tree | O(n) |
| Hash Table insert | O(1) when no resize or collision work is needed | O(1) amortized with a good hash distribution and resizing | O(n) |
| Hash Table search | O(1) when the key is at the first bucket entry | O(1) with few collisions | O(n) |
| Hash Table delete | O(1) when the key is at the first bucket entry | O(1) with few collisions | O(n) |

A basic BST does not balance itself. Sorted insertion can make it a chain, so its operations can take O(n). A self-balancing tree such as an AVL tree or Red-Black Tree guarantees O(log n) worst-case search, insertion, and deletion, but this project implements a basic unbalanced BST.

The hash table handles collisions using linked lists (separate chaining). Its average performance depends on the hash function, load factor, and collision distribution. Many keys in the same bucket can make an operation O(n). Hash-table insertion is amortized O(1) because resizing occasionally requires moving all stored keys.

## Experimental results

The program writes actual timings to `results.csv`. Copy the values from your own final run into this table before submitting. Times are in nanoseconds (ns).

| n | BST insert (ns) | Hash insert (ns) | BST search (ns) | Hash search (ns) | BST delete (ns) | Hash delete (ns) |
|---:|---:|---:|---:|---:|---:|---:|
| 100 | 15180 | 34420 | 22700 | 5020 | 35200 | 10680 |
| 1000 | 121280 | 131820 | 95340 | 35220 | 201760 | 33540 |
| 10000 | 1259740 | 731500 | 780960 | 197700 | 1608440 | 162260 |
## Discussion

The results show that the Hash Table was faster for searching and deletion at all three input sizes. For insertion, the BST was faster with 100 and 1,000 elements, while the Hash Table was faster with 10,000 elements.

For 10,000 elements, BST insertion took 1,157,296 ns, compared with 576,890 ns for the Hash Table. Searching took 573,872 ns for the BST and 195,835 ns for the Hash Table. Deletion took 1,202,901 ns for the BST and 132,766 ns for the Hash Table.

These results suggest that the Hash Table is effective for exact-key operations in this implementation. However, execution times depend on the hardware, JVM optimization, memory allocation, and input order. A BST also provides ordered traversal and can support range queries, which are not directly supported by a standard Hash Table.
## Conclusion

In this experiment, the Hash Table generally performed better for searching and deletion, while insertion performance depended on the number of elements. This is consistent with the expected average time complexities: O(1) for a Hash Table and O(log n) for a reasonably balanced ordinary BST. Both structures can have O(n) worst-case operations.

The Hash Table is a good choice when fast exact-key lookup is the main goal. A BST is useful when data must be maintained in sorted order or when range queries are required. The experiment also shows that theoretical complexity does not determine every measured result, because implementation details and runtime conditions affect execution time.
## Correctness tests

Before benchmarking, the program checks empty structures, insertion, searching for existing and missing keys, duplicate insertion, deletion of a node with two children, deletion of a missing key, collisions, resizing, and deletion of all keys. The program should print `All correctness tests passed.` before the benchmark.

## Limitations

This is a simple educational benchmark, not a rigorous microbenchmark. It uses one warm-up and five measured repetitions. Results can vary between runs and machines. Both implementations store unique integer keys; duplicate keys are ignored.
