package com.example.pes.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class PCA {

    private static final double threshold = 0.95;// 特征值阈值

    /**
     * 中心化，使每个样本的均值为0
     *
     * @param primary 原始二维数组矩阵
     *
     * @return averageArray 中心化后的矩阵
     */
    public double[][] changeAverageToZero(ArrayList<ArrayList> primary) {
        System.out.println("均值化…");
        int n = primary.size();				// 样本数：59
        int m = primary.get(0).size()-1;	// 特征数：1764  **建议修改。
        double[] sum = new double[m];
        double[] average = new double[m];
        double[][] averageArray = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sum[i] += (double)primary.get(j).get(i);
            }
            average[i] = sum[i] / n;
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                averageArray[j][i] = (double)primary.get(j).get(i) - average[i];
            }
        }
        return averageArray;
    }

    /**
     * 计算协方差矩阵
     *
     * @param matrix  均值化后的矩阵
     *
     * @return result 协方差矩阵
     */
    public double[][] getVarianceMatrix(double[][] matrix) {
        System.out.println("协方差矩阵…");
        int n = matrix.length;		// 行数，样本数
        int m = matrix[0].length;	// 列数，特征数
        double[][] result = new double[m][m];// 协方差矩阵
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double temp = 0;
                for (int k = 0; k < n; k++) {
                    temp += matrix[k][i] * matrix[k][j];
                }
                result[i][j] = temp / (n - 1);
            }
        }
        return result;
    }

    /**
     * 特征值分解？
     * */
    public EigenvalueDecomposition getEig(double[][] matrix){
        System.out.println("特征值分解：");
        // 定义一个矩阵
        Matrix A = new Matrix(matrix);
        EigenvalueDecomposition Eig = A.eig();
        return Eig;
    }

    /**
     * 求特征值矩阵
     *
     * @param EigenvalueDecomposition 协方差矩阵
     *
     * @return result 向量的特征值二维数组矩阵
     */
    public double[][] getEigenValueMatrix(EigenvalueDecomposition Eig) {
        System.out.println("特征值矩阵…");
        // 由特征值组成的对角矩阵,eig()获取特征值
        double[][] result = Eig.getD().getArray();
        return result;
    }

    /**
     * 特征向量矩阵
     *
     * @param  EigenvalueDecomposition 特征值矩阵
     *
     * @return result 标准化后的二维数组矩阵
     */
    public double[][] getEigenVectorMatrix(EigenvalueDecomposition Eig) {
        System.out.println("特征向量矩阵：");
        // 每一列对应一个特征向量
        double[][] result = Eig.getV().getArray();
        return result;
    }

    /**
     * 寻找主成分
     *
     * @param eigenvalue 	特征值二维数组
     *
     * @param eigenVectors  特征向量二维数组
     *
     * @return principalMatrix 主成分矩阵
     */
    public Matrix getPrincipalComponent(double[][] eigenvalue, double[][] eigenVectors) {
        // 定义一个特征向量矩阵
        Matrix A = new Matrix(eigenVectors);
        // 特征向量转置
        double[][] tEigenVectors = A.transpose().getArray();
        // key=主成分特征值λ，value=该特征值对应的特征向量v
        Map<Integer, double[]> principalMap = new HashMap<Integer, double[]>();
        // key=特征值，value=对应的特征向量；初始化为翻转排序，使map按key值降序排列
        TreeMap<Double, double[]> eigenMap = new TreeMap<Double, double[]>(Collections.reverseOrder());

        // 把特征值矩阵对角线上的元素放到数组eigenvalueArray里
        int index = 0, n = eigenvalue.length; // 特征向量个数：n=1764
        double[] eigenvalueArray = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j)
                    eigenvalueArray[index] = eigenvalue[i][j];
            }
            index++;
        }

        // 将每个特征向量（tEigenVectors每行）与特征值（eigenvalueArray）对应存入eigenMap
        for (int i = 0; i < tEigenVectors.length; i++) {
            double[] value = new double[tEigenVectors[0].length];
            value = tEigenVectors[i];
            eigenMap.put(eigenvalueArray[i], value);
        }

        // 求特征总和
        double total = 0;	// 存储特征值总和
        for (int i = 0; i < n; i++) {
            total += eigenvalueArray[i];
        }

        // 选出前几个主成分
        double temp = 0;
        int principalComponentNum = 0;// 主成分数
        List<Double> plist = new ArrayList<Double>();// 主成分特征值
        for (double key : eigenMap.keySet()) {	//key→特征值
            if (temp / total <= threshold) {
                temp += key;
                plist.add(key);
                principalComponentNum++;
            }
        }
        System.out.println("\n" + "当前阈值: " + threshold);
        System.out.println("取得的主成分数: " + principalComponentNum + "\n");

        // 往主成分map里输入数据（1:v1,2:v2……）
        for (int i = 0; i < plist.size(); i++) {
            if (eigenMap.containsKey(plist.get(i))) {	// containsKey()方法可检测数据是否存在
                principalMap.put(i, eigenMap.get(plist.get(i)));
            }
        }

        // 把principalMap里的值（选出的主成分的特征向量）存到二维数组里
        double[][] principalArray = new double[principalMap.size()][];
        Iterator<Entry<Integer, double[]>> it = principalMap.entrySet().iterator();
        for (int i = 0; it.hasNext(); i++) {
            principalArray[i] = it.next().getValue();
        }

        Matrix principalMatrix = new Matrix(principalArray);
        //System.out.println("行数:"+principalMatrix.getRowDimension());
        //System.out.println("列数:"+principalMatrix.getColumnDimension());
        return principalMatrix;
    }

    /**
     * 矩阵相乘
     *
     * @param primary 原始二维数组  59*1764
     *
     * @param matrix  主成分矩阵      p*1764（选了p个主成分）
     *
     * @return result 结果矩阵
     */
    public Matrix getResult(ArrayList<ArrayList> primary, Matrix matrix) {
        int n = primary.size();
        int m = primary.get(0).size()-1;	// **建议后期修改
        double[][] temp = new double[n][m];
        // ArrayList<ArrayList> 转 double[][]
        for(int i = 0; i < n; i++){
            for(int j=0; j < m ; j++){
                temp[i][j] = (double)primary.get(i).get(j);
            }
        }

        //System.out.println("temp大小:"+temp.length+","+temp[0].length);

        Matrix primaryMatrix = new Matrix(temp);
        Matrix result = primaryMatrix.times(matrix.transpose()); // transpose()转置，times()矩阵乘法
        return result;
    }
}