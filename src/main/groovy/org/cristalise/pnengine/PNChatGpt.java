package org.cristalise.pnengine;

import java.util.Arrays;

public class PNChatGpt {
    private int[][] incidenceMatrix;
    private int[] marking;
    
    public PNChatGpt(int[][] incidenceMatrix, int[] marking) {
        this.incidenceMatrix = incidenceMatrix;
        this.marking = marking;
    }
    
    public void fireTransition(int transition) {
        int[] delta = getColumn(incidenceMatrix, transition);
        int[] newMarking = vectorAddition(marking, delta);
        
        if (isValidMarking(newMarking)) {
            marking = newMarking;
        } else {
            throw new RuntimeException("Invalid marking");
        }
    }
    
    private int[] getColumn(int[][] matrix, int column) {
        int[] result = new int[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = matrix[i][column];
        }
        return result;
    }
    
    private int[] vectorAddition(int[] a, int[] b) {
        if (a.length != b.length) {
            throw new RuntimeException("Vectors are not the same length");
        }
        
        int[] result = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }
    
    private boolean isValidMarking(int[] marking) {
        for (int m : marking) {
            if (m < 0) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Incidence Matrix:\n");
        for (int[] row : incidenceMatrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        sb.append("Marking: ").append(Arrays.toString(marking)).append("\n");
        return sb.toString();
    }
    
    public static void main(String[] args) {
        int[][] incidenceMatrix = {{1, -1, 0}, {-1, 1, 1}, {0, -1, -1}};
        int[] marking = {2, 0, 0};
        
        PNChatGpt petriNet = new PNChatGpt(incidenceMatrix, marking);
        System.out.println(petriNet);
        
        petriNet.fireTransition(0);
        System.out.println(petriNet);
        
        petriNet.fireTransition(1);
        System.out.println(petriNet);
    }
}
