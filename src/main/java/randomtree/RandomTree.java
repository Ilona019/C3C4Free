package randomtree;

import java.util.*;

public class RandomTree {

    // 1) Любая последовательность длины n − 2, составленная из чисел 1, 2, . . . , n,
    // является кодом Прюфера некоторого дерева.
    // 2) Код Прюфера – это способ взаимно однозначного кодирования помеченных деревьев с n вершинами
    // с помощью последовательности n-2 целых чисел в отрезке [1,n].
    // То есть, можно сказать, что код Прюфера – это биекция между всеми остовными деревьями полного графа и числовыми последовательностями.
    // Пребразование кода Прюфера в дерево
    public static List<Integer>[] pruferCode2Tree(int[] pruferCode) {
        int n = pruferCode.length + 2;

        List<Integer>[] tree = new List[n];
        for (int i = 0; i < n; i++)
            tree[i] = new ArrayList<>();
        int[] degree = new int[n];
        Arrays.fill(degree, 1);
        for (int v : pruferCode)
            ++degree[v];
        int ptr = 0;
        while (degree[ptr] != 1)
            ++ptr;
        int leaf = ptr;
        for (int v : pruferCode) {
            tree[leaf].add(v);
            tree[v].add(leaf);
            --degree[leaf];
            --degree[v];
            if (degree[v] == 1 && v < ptr) {
                leaf = v;
            } else {
                for (++ptr; ptr < n && degree[ptr] != 1; ++ptr) ;
                leaf = ptr;
            }
        }
        for (int v = 0; v < n - 1; v++) {
            if (degree[v] == 1) {
                tree[v].add(n - 1);
                tree[n - 1].add(v);
            }
        }
        return tree;
    }

    // Пребразование дерева в код Прюфера
    public static int[] tree2PruferCode(List<Integer>[] tree) {
        int n = tree.length;
        int[] parent = new int[n];
        parent[n - 1] = -1;
        pruferDfs(tree, parent, n - 1);
        int[] degree = new int[n];
        int ptr = -1;
        for (int i = 0; i < n; ++i) {
            degree[i] = tree[i].size();
            if (degree[i] == 1 && ptr == -1)
                ptr = i;
        }
        int[] res = new int[n - 2];
        int leaf = ptr;
        for (int i = 0; i < n - 2; ++i) {
            int next = parent[leaf];
            res[i] = next;
            --degree[next];
            if (degree[next] == 1 && next < ptr) {
                leaf = next;
            } else {
                ++ptr;
                while (ptr < n && degree[ptr] != 1)
                    ++ptr;
                leaf = ptr;
            }
        }
        return res;
    }

    static void pruferDfs(List<Integer>[] tree, int[] parent, int v) {
        for (int i = 0; i < tree[v].size(); ++i) {
            int to = tree[v].get(i);
            if (to != parent[v]) {
                parent[to] = v;
                pruferDfs(tree, parent, to);
            }
        }
    }

    // precondition: n >= 2
    public static List<Integer>[] getRandomTree(int V, Random rnd) {
        int[] a = new int[V - 2];
        for (int i = 0; i < a.length; i++) {
            a[i] = rnd.nextInt(V);
        }
        return pruferCode2Tree(a);
    }

}
