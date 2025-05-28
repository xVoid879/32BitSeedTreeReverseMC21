import java.util.ArrayList;
import java.util.List;

public class TreeReverse {

    public static void main(String[] args) {
        List<Thread> list = new ArrayList<>();
        int part = Integer.MAX_VALUE/4;
        for (long i = -4; i < 4; i++) {

            long start = i * part;
            long end = i * part + part;

            System.out.println();
            System.out.println("kernel: " + (i + 4));
            System.out.println((int) start);
            System.out.println((int) end);
            list.add(new Thread(() -> {
                try {
                    submittable((int) start, (int) end);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }
        for (Thread thread : list) {
            thread.start();
        }
        for (Thread thread : list) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void submittable(int start, int end) {
        long time = System.currentTimeMillis();
        
        int[][] trees = {
            {3, 1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {7, 1, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {12, 2, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {5, 7, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {1, 15, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {5, 14, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {9, 12, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {12, 14, 6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        };


        List<Integer> defaultList = new ArrayList<>();
        for (int treeNum = 0; treeNum < trees.length; treeNum++) {
            defaultList.add(treeNum);
        }

        XoroRandom xoroRandom = new XoroRandom(1);
        ChunkRandom random = new ChunkRandom(xoroRandom);
        XoroRandom leaveXoroRandom = new XoroRandom(1);
        ChunkRandom leaveRandom = new ChunkRandom(leaveXoroRandom);

        seedloop:
        for (int intSeed = start; intSeed < end; intSeed++) {

            long seed = (long)intSeed;

            if (seed % 10000000 == 0) {
                if (start == 0) {
                    System.out.println(((float) seed * 100 / end) + "% at second " + ((System.currentTimeMillis() - time) / 1000));
                }
            }

            
            long popseed = random.setPopulationSeed(seed,384,-64);

            popseed += (9*10000)+25;
            random.setSeed(popseed);

            List<Integer> copyTreePositions = new ArrayList<>(defaultList);

            int x = random.nextInt(16);

            
            for (int i = 0; i < 100; i++) {
                int z = random.nextInt(16);

                treeCheckLoop:
                for (int treeNum : copyTreePositions) {
                    int[] tree = trees[treeNum];
                    if (x == tree[0] && z == tree[1]) {
                        xoroRandom.copySeedTo(leaveXoroRandom);
                        leaveXoroRandom.skip(2);
                        if (leaveRandom.nextInt(3) == tree[2]) {
                            
                            leaveXoroRandom.skip(5);
                            for (int leave = 3; leave < 15; leave++) {
                                int leaveData = tree[leave];
                                if (leaveRandom.nextInt(2) != leaveData && leaveData != -1) {
                                    break treeCheckLoop;
                                }
                            }
                            leaveXoroRandom.copySeedTo(xoroRandom);
                            z = random.nextInt(16);
                            copyTreePositions.remove((Integer) treeNum);
                            if (copyTreePositions.size() < 5) {
                                System.out.println("seed: "+intSeed);
                                continue seedloop;
                            }
                            break;
                        }
                    }
                }
                x = z;
            }
        }
    }
}
