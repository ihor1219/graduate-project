package com.example.pes.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSort {

    private int minValue;	// 最小值
    private int maxValue;	// 最大值

    // 构造方法初始化变量
    public RandomSort(){
        this.minValue = 0;
        this.maxValue = 59;
    }

    // 带参数构造方法初始化变量
    public RandomSort(int minValue, int maxValue){
        this();
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    // 运用筛选法生成不重复的随机数序列
    public Integer[] proceduresSort(int len){
        int numLength = this.maxValue - this.minValue + 1;	// 初始化列表长度
        List list = new ArrayList();
        // 循环依次获得整数
        for(int i = this.minValue; i <= this.maxValue; i++){
            list.add(new Integer(i));	// 在列表中添加整型数据
        }
        Random rd = new Random();	// 用于生成随机下标
        List result = new ArrayList();	//创建列表对象
        while(result.size() < len){
            int index = rd.nextInt(numLength);	// 生成在[0,numLength)范围内的下标
            result.add(list.get(index));	// 下标位index数字对象放入列表对象中
            list.remove(index);		// 移除下标为index的数字对象
            numLength--;			// 候选队列长度减去1
        }
        return (Integer[]) result.toArray(new Integer[0]);	// 将列表转换成整形数组返回
    }
}