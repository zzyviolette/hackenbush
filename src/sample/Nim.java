/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author zielonka
 */
public class Nim {

    private Random random = new Random();
    private boolean normal;/* true if normal Nim, false if misère Nim */
    

    public Nim(boolean normal) {
        this.normal = normal;
    }

    public boolean getNimType() {
        return normal;
    }

    /* nimSum(a,b) is just xor of a and b */
    public static int nimSum(int a, int b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("negative Nim sum argument");
        }
        return (a | b) & ~(a & b);
    }

    public static int nimSum(int[] heap) {
        int s = 0;
        for (int i = 0; i < heap.length; i++) {
            s = nimSum(s, heap[i]);
        }
        return s;
    }

    public static boolean isFinished(int[] heap) {
        for (int i = 0; i < heap.length; i++) {
            if (heap[i] > 0) {
                return false;
            }
        }
        return true;
    }

    public static Move winningMoveNormal(int[] heap) {
        int sum = nimSum(heap);
        if (sum == 0) {
            return Move.EMPTY;
        }
        for (int i = 0; i < heap.length; i++) {
            int a = nimSum(sum, heap[i]);
            if (a < heap[i]) {
                return new Move(i, a);
            }
        }
        return Move.EMPTY;
    }

    public static Move winningMoveMisere(int[] heap) {
        int numberNonZero = 0, numberLarge = 0, maxHeap = 0;

        /* numberNonZero - the number of i such that heap[i] > 0 
        *  numberLarge - the number of  i such that  heap[i] > 1
        *  maxHeap - heap[maxHeap] >= heap[i] for all i 
         */
        for (int i = 0; i < heap.length; i++) {
            if (heap[i] > 0) {
                numberNonZero++;
                if (heap[i] > heap[maxHeap]) {
                    maxHeap = i;
                }
            }
            if (heap[i] > 1) {
                numberLarge++;
            }
        }
        /* if more than one heap greater equal 2 then play the
        *  the same way as in the normal Nim
         */
        if (numberLarge >= 2) {
            return winningMoveNormal(heap);
        }

        /* The case when numberLarge <= 1 :
        *nim.generateRandomNim
        *  The optimal move is the move such that taking this move
        *  we obtain an odd number of heaps of size 1 
        *  and no heap of size greater than 1.
        *  This means that we  reduce the greatest heap 
        *  either to size 0 or 1 depending on
        *  the parity of the number of non zero heaps.
         */
        if ((numberNonZero & 1) == 1 & heap[maxHeap] > 1 ) {
            return new Move(maxHeap, 1);
        } else if ( (numberNonZero & 1) == 0 && numberNonZero > 0 ) {
            return new Move(maxHeap, 0);
        }
        
        return Move.EMPTY;
    }

    public Move winningMove(int[] heap) {
        if (normal) {
            return winningMoveNormal(heap);
        } else {
            return winningMoveMisere(heap);
        }
    }

    /* randomMove() produces a random move in the Nim game.
    *  It returns a Map containing one pair (n,k)
    *  such that n is a heap number and k is a new size of heap[n],
    *  0 <= k < heap[n]
    *  If no move is possible randomMove() returns an empty map.
     */
    public Move randomMove(int[] heap) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < heap.length; i++) {
            if (heap[i] > 0) {
                list.add(i);
            }
        }

        if (list.isEmpty()) {
            return Move.EMPTY;
        }
        int n = random.nextInt(list.size());
        int k = list.get(n);
        int l = random.nextInt(heap[k]);
        return new Move(k, l);
    }

    public Move nextMove(int[] heap) {
        Move move = winningMove(heap);
        if (move == Move.EMPTY) {
            return randomMove(heap);
        } else {
            return move;
        }
    }

    public int[] generateRandomNim(int numberOfHeaps, int maxSize) {
        int n = random.nextInt(numberOfHeaps) + 1;
        int[] heap = new int[n];
        for (int i = 0; i < heap.length; i++) {
            heap[i] = random.nextInt(maxSize) + 1;
        }
        return heap;
    }

    public static void printNim(int[] heap) {
        for (int i = 0; i < heap.length; i++) {
            System.out.print(heap[i]);
            if (i < heap.length - 1) {
                System.out.print(", ");
            } else {
                System.out.println();
            }
        }
    }

    public static void applyMove(Move move, int[] heap) {
        if (move == Move.EMPTY) {
            return;
        }
        int n = move.getIndex();
        int k = move.getSize();
        if (heap[n] <= k) {
            throw new IllegalArgumentException("invalid " + move.toString());
        }
        heap[n] = k;
        return;
    }

    public static void printFullNimPlay(Nim nim, int[] heap) {
        int i = 0;
        printNim(heap);
        while (!Nim.isFinished(heap)) {
            if ((i & 1) == 0) {
                System.out.println("Rouge");
            } else {
                System.out.println("Blue");
            }
            Move move = nim.nextMove(heap);
            applyMove(move, heap);
            printNim(heap);
            i = (i == 0) ? 1 : 0;
        }

    }

    static public class Move {

        public static final Move EMPTY = new Move(-1, 0);
        
        private int index, size;

        public Move(int index, int size) {
            this.index = index;
            this.size = size;
        }


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size){
            this.size = size;
        }

        public String toString() {
            return "Move(" + getIndex() + "," + getSize() + ")";
        }
    }

}
