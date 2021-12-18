package main;

import java.util.ArrayList;

/**
 * @author Arefeva Ilona
 */
public class Matrix {

    private final int[][] matrix;
    private int countVertex;

    public Matrix(int countVertex, ArrayList<String> coordinatesVertex) {
        matrix = new int[countVertex][];
        this.countVertex = countVertex;

        for (int i = 1, l = 1; i < countVertex; i++, l++) {
            matrix[i] = new int[l];
            String[] coordinateXY = coordinatesVertex.get(i).split("\\s+");
            for (int j = 0; j < l; j++) {
                String[] coordinateAB = coordinatesVertex.get(j).split("\\s+");
                int distance = distanceBetweenTwoVertices(Integer.parseInt(coordinateXY[0]), Integer.parseInt(coordinateXY[1]), Integer.parseInt(coordinateAB[0]), Integer.parseInt(coordinateAB[1]));
                matrix[i][j] = distance;
            }
        }
    }

    // расстояние между вершинами (x,y) и (a,b) на Манхэтеской плоскости
    public int distanceBetweenTwoVertices(int x, int y, int a, int b) {
        return (Math.abs(x - a) + Math.abs(y - b));
    }

    // получить элемент из треугольного массива по индексам строки и столбца;
    public int getWeight(int i, int j) {
        if (i < j) {
            return matrix[j][i];
        } else if (i == j) {
            return 0;
        }
        return matrix[i][j];
    }

    // вернуть число вершин
    public int getCountVerteces() {
        return countVertex;
    }

    // вывод матрицы в консоль;
    public void printMatrix() {
        for (int i = 1, l = 1; i < matrix.length; i++, l++) {
            System.out.print(i + ")\t");
            for (int j = 0; j < l; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.print("\t");
        for (int i = 0; i < matrix.length - 1; i++) {
            System.out.print(i + ")\t");
        }
        System.out.println();
    }

}