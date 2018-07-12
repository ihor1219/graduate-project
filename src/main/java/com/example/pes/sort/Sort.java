package com.example.pes.sort;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import org.apache.poi.hssf.usermodel.*;

import Jama.Matrix;
import Jama.EigenvalueDecomposition;

public class Sort {

    public ArrayList function1(){

        // 1、读取excel训练集数据（已经减去基线，且比例均衡）
        ArrayList <ArrayList> trainList = new ArrayList<ArrayList>();	// 训练集
        // 创建excel文件对象
        File file = new File("C:\\Users\\ihor\\Desktop\\trainData.xls");
        FileInputStream in = null;
        try{
            // 创建对Excel工作簿文件的引用
            in = new FileInputStream(file);
            // 创建一个新的HSSFWorkbook对象
            HSSFWorkbook wb = new HSSFWorkbook(in);
            // 工作表对象
            HSSFSheet sheet = wb.getSheetAt(0);
            // 读取工作表数据
            HSSFRow row = sheet.getRow(0);
            HSSFCell cell = null;
            int rowNum = sheet.getLastRowNum();	//总行数
            int colNum = row.getLastCellNum();	//总列数
            //System.out.println(rowNum);
            //System.out.println(colNum);

            // addList()
            //addList(rowNum, colNum, sheet, row, cell, trainList);
            for (int i = 0; i < colNum; i++) {
                ArrayList columnList = new ArrayList();    //该列特征数组

                for (int j = 1; j <= rowNum; j++) {
                    row = sheet.getRow(j);    //取得第j行
                    cell = row.getCell(i);    //取得j行的第i列
                    double cellValue = cell.getNumericCellValue();
                    columnList.add(cellValue);
                }
                trainList.add(columnList);
            }
            in.close();
            System.out.println("训练集大小："+ trainList.size());
            //System.out.println("First done!");
        }catch (Exception e){
            e.printStackTrace();
        }

        // 读取测试集
        ArrayList <ArrayList> testList = new ArrayList<ArrayList>();	// 训练集
        // 创建excel文件对象
        file = new File("C:\\Users\\ihor\\Desktop\\testData.xls");
        in = null;
        try{
            // 创建对Excel工作簿文件的引用
            in = new FileInputStream(file);
            // 创建一个新的HSSFWorkbook对象
            HSSFWorkbook wb = new HSSFWorkbook(in);
            // 工作表对象
            HSSFSheet sheet = wb.getSheetAt(0);
            // 读取工作表数据
            HSSFRow row = sheet.getRow(0);
            HSSFCell cell = null;
            int rowNum = sheet.getLastRowNum();	//总行数
            int colNum = row.getLastCellNum();	//总列数
            //System.out.println(rowNum);
            //System.out.println(colNum);

            // addList()
            for (int i = 0; i < colNum; i++) {
                ArrayList columnList = new ArrayList();    //该列特征数组

                for (int j = 1; j <= rowNum; j++) {
                    row = sheet.getRow(j);    //取得第j行
                    cell = row.getCell(i);    //取得j行的第i列
                    double cellValue = cell.getNumericCellValue();
                    columnList.add(cellValue);
                }
                testList.add(columnList);
            }
            in.close();
            System.out.println("测试集大小："+ testList.size());
            //System.out.println("First done!");
        }catch (Exception e){
            e.printStackTrace();
        }

        // 2、PCA降维
        PCA pca = new PCA();
        // 1）均值化
        double[][] averageArray = pca.changeAverageToZero(trainList);	// **此处传入trainList带有类别
        // 2）协方差矩阵
        double[][] varMatrix = pca.getVarianceMatrix(averageArray);
        // 3.1） 计算Eig
        EigenvalueDecomposition Eig = pca.getEig(varMatrix);
        // 3）特征值矩阵
        double[][] eigenValueMatrix = pca.getEigenValueMatrix(Eig);
        // 4）特征向量矩阵
        double[][] eigenVectorMatrix = pca.getEigenVectorMatrix(Eig);
        // 5）主成分矩阵
        Matrix principalMatrix = pca.getPrincipalComponent(eigenValueMatrix, eigenVectorMatrix);
        // 6）降维后的矩阵
        Matrix trainMatrix = pca.getResult(trainList, principalMatrix);	// 降维后训练集 **此处传入trainList带有类别
        Matrix testMatrix = pca.getResult(testList, principalMatrix);	// 降维后测试集 **此处传入testList带有类别

        // 降维后训练集行列数
        int row = trainMatrix.getRowDimension();	// 样本数
        int column = trainMatrix.getColumnDimension();	// 特征数
        //System.out.println(row+"，"+column);

        ArrayList <ArrayList> newTrainList = new ArrayList<ArrayList>();	// 降维后训练集特征
        // Matrix 转 ArrayList<ArrayList>
        for(int i = 0; i < row; i++){
            ArrayList columnList = new ArrayList();	//该列特征数组
            for(int j = 0; j < column; j++){
                columnList.add(trainMatrix.get(i, j));
            }
            // 加上类别，
            /**
             * trainList.get(0)→取出一个样本，
             * trainList.get(0).size()→该样本列数，
             * trainList.get(0).size()-1→类别所在列号
             *
             * trainList.get(i)→取出该样本
             * trainList.get(i).get(trainList.get(0).size()-1)→取出该样本的类别
             * */
            columnList.add(trainList.get(i).get(trainList.get(0).size()-1));
            newTrainList.add(columnList);
        }

        // 降维后特征集行列数
        row = testMatrix.getRowDimension();	// 样本数
        column = testMatrix.getColumnDimension();	// 特征数
        //System.out.println(row+"，"+column);
        ArrayList <ArrayList> newTestList = new ArrayList<ArrayList>();	// 降维后测试集特征
        // Matrix 转 ArrayList<ArrayList>
        for(int i = 0; i < row; i++){
            ArrayList columnList = new ArrayList();	//该列特征数组
            for(int j = 0; j < column; j++){
                columnList.add(testMatrix.get(i, j));
            }
            newTestList.add(columnList);
        }

        //System.out.println(newTrainList);
        //System.out.println(newTestList);

        // 3、朴素贝叶斯分类器
        /**
         * newTestList 为降维后不带类别的
         * testList 为原始数据，带类别
         * */
        String finalResult = null;
        //int wrong_number = 0;
        ArrayList resultList = new ArrayList();
        Bayes bayes = new Bayes();
        for( int i = 0; i < newTestList.size(); i++){
            //遍历每个测试样本
            ArrayList tmp = new ArrayList();
            //ArrayList tmp1 = new ArrayList();
            //ArrayList tmpResult = new ArrayList();
            tmp = newTestList.get(i);	// 取出1个测试样本，不带类别
            //tmp1 = testList.get(i);		// 该样本带类别
            //double label = (double) tmp1.get(tmp1.size()-1);	// 类别
            //tmp.remove(tmp.size()-1);	//去除类别
            finalResult = bayes.predictClass(newTrainList, tmp);	 // **出错，这里传入的newTrainList不带类别，但是最开始写的是带类别的
            System.out.println("预测分类："+finalResult);
            //System.out.println("正确类别："+label+";   预测分类："+finalResult);
            resultList.add(finalResult);
        }
        /*
        DecimalFormat df = new DecimalFormat(".00");
        double accuracy = (double)(newTestList.size()-wrong_number)/(double)newTestList.size(); //  正确率
        String acc = df.format(accuracy*100);
        ArrayList result = new ArrayList();
        result.add(wrong_number);
        result.add(acc);
        resultList.add(result);
        System.out.println(resultList);
        System.out.println("错误个数："+wrong_number+"    正确率："+acc+"%");
        */
        return resultList;
    }

    public void addList(int rowNum, int colNum, HSSFSheet sheet, HSSFRow row, HSSFCell cell, ArrayList<ArrayList> list) {
        for (int i = 0; i < colNum; i++) {
            ArrayList columnList = new ArrayList();    //该列特征数组

            for (int j = 1; j <= rowNum; j++) {
                row = sheet.getRow(j);    //取得第j行
                cell = row.getCell(i);    //取得j行的第i列
                double cellValue = cell.getNumericCellValue();
                columnList.add(cellValue);
            }
            list.add(columnList);
        }
    }
}