package com.example.pes.sort;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class Bayes {

    public String predictClass(ArrayList<ArrayList> trainList,ArrayList testSample){
        Map<Double, ArrayList<ArrayList>> resultMap = dataSet(trainList);
        double mMax = Double.NEGATIVE_INFINITY;

        String finalResult  = null;
        for(int i = 0;i < resultMap.size();i++){
            double mCurrent = 0.0;
            double key = 0.0;
            if(i == 0){
                key = 1.0;	//1类
            }else{
                key = 2.0;	//2类
            }
            ArrayList<ArrayList> temp = resultMap.get(key);	//	1类/2类训练集
            mCurrent = culCofV(temp.size(),trainList.size());	// P(y=1) P(y=2)
            mCurrent = Math.log10(mCurrent);
            for(int j = 0;j < testSample.size();j++){		// 遍历该条测试样本所有特征
                double pv = culPofV(temp,(double)testSample.get(j),j);
                //System.out.println("概率"+j+":"+pv);
                mCurrent += Math.log10(pv);
                //mCurrent *= pv;
            }
            //System.out.println("i="+i+"时，概率为"+mCurrent);
            if(mMax <= mCurrent){
                if(i == 0){
                    //System.out.println("1.0");
                    finalResult = "DK961";
                }else{
                    //System.out.println("2.0");
                    finalResult = "LangDon";
                }
                mMax = mCurrent;
            }
        }
        return finalResult;
    }

    /**
     * 计算先验概率P(y)
     * @param ySize
     * @param nSize
     * @return
     */
    public double culCofV(int ySize,int nSize){
        return DecimalCalculate.div(ySize, nSize);
    }

    /**
     * 分类
     * @param list
     * @return
     */
    public Map<Double, ArrayList<ArrayList>> dataSet(List<ArrayList> list){
        Map<Double, ArrayList<ArrayList>> culMap = new HashMap<Double, ArrayList<ArrayList>>();
        ArrayList<ArrayList> mIsList = new ArrayList<ArrayList>();
        ArrayList<ArrayList> mNoList = new ArrayList<ArrayList>();
        for(int i = 0;i < list.size();i++){
            ArrayList temp = new ArrayList();
            temp = list.get(i);
            double mResult = (double)temp.get(temp.size()-1);//获取最后一项
            if(mResult == 1.0){
                //System.out.println("11111111111111");
                mIsList.add(temp);
            }else{
                //System.out.println("22222222222222");
                mNoList.add(temp);
            }
        }
        culMap.put(1.0, mIsList);
        culMap.put(2.0, mNoList);
        return culMap;
    }

    /**
     * 计算各种条件概率-高斯朴素贝叶斯
     * @param mList:训练集
     * @param mStr:该条测试样本的一个特征
     * @param index:该特征的序号
     */
    public double culPofV(ArrayList<ArrayList> mList,double mStr,int index){
        double Mean;
        double Sdev;
        double probi = 1.0;
        Mean = getMean(mList,index);
        Sdev = getSdev(mList,index);
        if(Sdev != 0){
            probi *= (1/(Math.sqrt(2*Math.PI)*Sdev)) * (Math.exp(-(mStr-Mean)*(mStr-Mean)/(2*Sdev*Sdev)));
        }
        //System.out.println("概率"+probi);
        return probi;
    }

    public double culPofV1(ArrayList<ArrayList> mList,double mStr,int index){
        int count = 0;
        for(int i = 0;i < mList.size();i++){
            if(mStr == (double)mList.get(i).get(index)){
                count++;
            }
        }
        return DecimalCalculate.div(count, mList.size(), 3);
    }

    /**
     * 计算均值
     */
    public double getMean(ArrayList<ArrayList> elements,int index){
        double sum = 0.0;
        double Mean;
        for(ArrayList element:elements){
            sum += (double)element.get(index);
        }
        Mean = sum/(double)elements.size();
        //System.out.println("Mean:"+Mean);
        return Mean;
    }

    /**
     * 计算方差
     */
    public double getSdev(ArrayList<ArrayList> elements,int index){
        double dev = 0.0;
        double Mean;
        Mean = getMean(elements,index);
        for(ArrayList element:elements){
            dev += Math.pow(((double)element.get(index)-Mean), 2);
        }
        dev = Math.sqrt(dev/(double)elements.size());
        //System.out.println("Dev:"+dev);
        return dev;
    }
}