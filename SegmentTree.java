import java.util.Scanner;

/**
 * Created by mshah2 on 08/03/2018.
 */
public class SegmentTree {

    private static class SegmentNode {
        private int maxInSegment;
        private long sumOfSegment;


        public int getMaxValue() {
            return maxInSegment;
        }

        public long getMaxSum() {
            return sumOfSegment;
        }

        /**
         * Assigns the leaf value.
         * @param underlying The value to assign this leaf node.
         */
        public void assign(int underlying) {
            maxInSegment = underlying;
            sumOfSegment = underlying;
        }

        public void merge(SegmentNode left, SegmentNode right) {
            if ((left == null) && (right == null)) {
                return;
            }

            if (left == null) {
                maxInSegment = right.getMaxValue();
                sumOfSegment = right.getMaxSum() + maxInSegment;
            }
            else if (right == null) {
                maxInSegment = left.getMaxValue();
                sumOfSegment = left.getMaxSum() + maxInSegment;
            }
            else {
                maxInSegment = Math.max(left.getMaxValue(), right.getMaxValue());
                sumOfSegment = left.getMaxSum() + right.getMaxSum() + maxInSegment;
            }
        }
    }


    private final int elements;
    private final SegmentNode[] nodes;


    private SegmentTree(int[] source) {
        elements = source.length - 1;

        // Determine the size the of the backing array.
        int size = calculateTreeSize(source.length);
        nodes = new SegmentNode[size];

        // We want to start the backing array indexing from 1 to make node
        // traversal calculations easier.
        build(source, 1, 0, elements);

    }

    private void build(int[] source, int index, int low, int high) {
        // Create a new node within the backing array.
        nodes[index] = new SegmentNode();

        // Reached the leaf level and exit point!
        if (low == high) {
            nodes[index].assign(source[low]);
            return;
        }

        // Divide tree further and process both sides.
        int left = 2 * index;
        int right = left + 1;
        int mid = (low + high) / 2;

        // Build left node
        build(source, left, low, mid);

        // Build right node
        build(source, right, mid + 1, high);

        // Merge the left and right nodes built above
        nodes[index].merge(nodes[left], nodes[right]);
    }

    private SegmentNode find(int index, int left, int right, int low, int high) {
        // We've reached the data for the range we're looking for.
        if ((left == low) && (right == high)) {
            return nodes[index];
        }

        // Divide and conquer.
        int mid = (left + right) / 2;
        if (low > mid) {
            // Go down the right side
            return find((2 * index) + 1, mid + 1, right, low, high);
        }

        if (high <= mid) {
            // Keep on going down the left side.
            return find(2 * index, left, mid, low, high);
        }

        // Straddling two segments
        SegmentNode leftNode = find(2 * index, left, mid, low, mid);
        SegmentNode rightNode = find((2 * index) + 1, mid + 1, right, mid + 1, high);
        SegmentNode result = new SegmentNode();
        result.merge(leftNode, rightNode);

        return result;
    }

    public long getMaxSum(int low, int high) {
        SegmentNode node = find(1, 0, elements, low, high);
        return node.getMaxSum();
    }

    /**
     * Calculates the number of nodes required to the build the tree to
     * represent the number of elements in the source range.
     *
     * @param elements The number of elements in the source range.
     * @return The number of nodes required in the tree.
     */
    private static int calculateTreeSize(int elements) {
        int size = 1;
        while (size < elements) {
            size = size << 1;

            // Overflow indicating that we don't have the capacity to build a
            // a tree for the number of elements.
            if (size < 0) {
                throw new IllegalArgumentException("The number of elements supplied breaches storage capacity.");
            }
        }

        return size << 1;
    }


    /**
     * Builds an instance of the <code>SegmentTree</code> using the source array.
     *
     * @param source  The source array.
     * @return An instance of the <code>SegmentTree</code>
     */
    public static SegmentTree build(int[] source) {
        SegmentTree tree = new SegmentTree(source);
        return tree;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int elements = scanner.nextInt();
        int queries = scanner.nextInt();

        int[] source = new int[elements];
        for (int element = 0; element < elements; element++) {
            source[element] = scanner.nextInt();
        }


        SegmentTree tree = build(source);
        for (int query = 0; query < queries; query++) {

            // Make adjustments for the indexing as the index in the query starts from 1.
            int low = scanner.nextInt() - 1;
            int high = scanner.nextInt() - 1;

            System.out.println(tree.getMaxSum(low, high));
        }

        scanner.close();
    }
}
