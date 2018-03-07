package com.nix;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class AppTest1{
    /** 请完成下面这个函数，实现题目要求的功能 **/
    /** 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^  **/
    public static void main(String[] args) {

        List<Integer> order = new ArrayList<Integer>();
        Map<String, List<Integer>> boms = new HashMap<String, List<Integer>>();

        Scanner in = new Scanner(System.in);
        String line = in.nextLine();

        Integer n = Integer.parseInt(line.split(",")[0]);
        Integer m = Integer.parseInt(line.split(",")[1]);

        line = in.nextLine();
        String[] itemCnt = line.split(",");
        for(int i = 0; i < n ; i++){
            order.add(Integer.parseInt(itemCnt[i]));
        }

        for(int i = 0; i < m; i++){
            line = in.nextLine();
            String[] bomInput = line.split(",");
            List<Integer> bomDetail = new ArrayList<Integer>();

            for(int j = 1; j <= n; j++ ){
                bomDetail.add(Integer.parseInt(bomInput[j]));
            }
            boms.put(bomInput[0], bomDetail);
        }
        in.close();
        Map<String, Integer> res = resolve(order, boms);

        System.out.println("match result:");
        for(String key : res.keySet()){
            System.out.println(key+"*"+res.get(key));
        }
    }

    // write your code here
    public static Map<String, Integer> resolve(List<Integer> order, Map<String, List<Integer>> boms) {
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Integer>> results = new ArrayList();
        result(order,boms,result,results);
        return getBestResult(results,boms,order);
    }

    /**
     * 核心运算方法
     * */
    public static boolean result(List<Integer> order, Map<String, List<Integer>> boms,Map<String, Integer> result,List<Map<String, Integer>> results) {
        Map<String, List<Integer>> nextBom = new HashMap<>();
        nextBom.putAll(boms);
        Map<String, List<Integer>> map = getOneMatch(order,boms);
        for (Map.Entry<String, List<Integer>> entry:map.entrySet()) {
            for (int i = 0;i < entry.getValue().size();i ++) {
                Map<String, Integer> newResult = new HashMap<>();
                newResult.putAll(result);
                ArrayList<Integer> list = new ArrayList<>();
                for (int j = 0;j < order.size();j ++) {
                    list.add(entry.getValue().get(i) * boms.get(entry.getKey()).get(j));
                }
                newResult.put(entry.getKey(),entry.getValue().get(i));
                List<Integer> newOrder = new ArrayList<>();
                newOrder.addAll(order);
                getNewOrder(newOrder,list);
                if (checkOrderIsOK(newOrder,newResult)) {
                    results.add(newResult);
                    newResult = new HashMap<>();
                    newResult.putAll(result);
                    newResult.put(entry.getKey(),entry.getValue().get(i));
                }
                nextBom.remove(entry.getKey());
                result(newOrder,nextBom,newResult,results);
            }
            break;
        }
        return false;
    }

    /**
     * 填充结果
     * */
    private static void fillResult(List<Map<String, Integer>> results,Map<String, List<Integer>> boms) {
        for (Map<String, Integer> reslut:results) {
            for (String key : boms.keySet()) {
                if (!reslut.containsKey(key)) {
                    reslut.put(key,0);
                }
            }
        }
    }

    /**
     * 获取最佳组合
     * */
    private static Map<String, Integer> getBestResult(List<Map<String, Integer>> results,Map<String, List<Integer>> boms,List<Integer> order) {
        fillResult(results,boms);
        deleteRepeat(results);
        results = sort(results,boms,order);
        Map<Map<String, Integer>,Integer> mapIntegerMap = new HashMap<>();
        for (Map<String, Integer> result:results) {
            mapIntegerMap.put(result,getInt(result));
        }
        mapIntegerMap = sortMapByValue(mapIntegerMap);
        Integer min = null;
        results.clear();
        for (Map<String, Integer> key:mapIntegerMap.keySet()) {
            min = min == null ? mapIntegerMap.get(key) : min;
            if (mapIntegerMap.get(key) == min) {
                results.add(key);
            }
        }
        return results.get(0);
    }

    /**
     * 删除重复方案
     * */
    private static void deleteRepeat(List<Map<String, Integer>> results) {
        for (int i = 0;i < results.size();i ++) {
            for (int j = i + 1;j < results.size();j ++) {
                if (results.get(i).toString().equals(results.get(j).toString())) {
                    results.remove(j);
                }
            }
        }
    }

    /**
     * 获取组合方案的组合种类
     * */
    private static Integer getInt(Map<String, Integer> result) {
        int sum = 0;
        for (String key:result.keySet()) {
            sum += result.get(key) == 0 ? 0 : 1;
        }
        return sum;
    }


    /**
     * 对每种方案剩余商品排序
     * */
    private static List<Map<String, Integer>> sort(List<Map<String, Integer>> results,Map<String, List<Integer>> boms,List<Integer> order) {
        Map<Map<String, Integer>,Integer> s = new HashMap<>();
        for (int i = 0;i < results.size();i ++) {
            s.put(results.get(i),getRemainingNum(results.get(i),boms,order));
        }
        s = sortMapByValue(s);
        List<Map<String, Integer>> returnResult = new ArrayList<>();
        Integer min = null;
        for (Map<String, Integer> key:s.keySet()) {
            min = min == null ? s.get(key) : min;
            if (s.get(key) == min) {
                returnResult.add(key);
            }
        }
        return returnResult;
    }
    /**
     * 计算组合剩余商品种类数
     * */
    private static Integer getRemainingNum(Map<String, Integer> result,Map<String, List<Integer>> boms,List<Integer> order) {
        int sum = 0;
        for (int i = 0;i < order.size();i ++) {
            int num = 0;
            for (String key : result.keySet()) {
                num += result.get(key) * boms.get(key).get(i);
            }
            if (order.get(i) > num) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap
     * @return
     */
    public static Map<Map<String, Integer>, Integer> sortMapByValue(Map<Map<String, Integer>, Integer> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<Map<String, Integer>, Integer> sortedMap = new LinkedHashMap();
        List<Map.Entry<Map<String, Integer>, Integer>> entryList = new ArrayList<Map.Entry<Map<String, Integer>, Integer>>(
                oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<Map<String, Integer>, Integer>> iter = entryList.iterator();
        Map.Entry<Map<String, Integer>, Integer> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
    static class MapValueComparator implements Comparator<Map.Entry<Map<String, Integer>, Integer>> {
        @Override
        public int compare(Map.Entry<Map<String, Integer>, Integer> o1, Map.Entry<Map<String, Integer>, Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    }

    /**
     * 校验组装是否完成
     * @param order 剩余订单
     * @return
     * */
    private static boolean checkOrderIsOK(List<Integer> order,Map<String, Integer> result) {
        int sum = 0;
        for (String key:result.keySet()) {
            sum += result.get(key);
        }
        if (sum == 0) {
            return false;
        }
        for (Integer num:order) {
            if (num < 0) {
                return false;
            }
        }
        return true;
    }
    /**
     * 分组后获取新的订单剩余
     * @param oldOrder 旧订单内容
     * @param subtractionNum 已经添加的部分
     * */
    private static void getNewOrder(List<Integer> oldOrder,List<Integer> subtractionNum) {
        for (int i = 0;i < oldOrder.size();i ++) {
            oldOrder.set(i,oldOrder.get(i) - subtractionNum.get(i));
        }
    }


    /**
     * 获取每组bom的合法数目
     * */
    private static Map<String, List<Integer>> getOneMatch(List<Integer> order,Map<String, List<Integer>> boms) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry:boms.entrySet()) {
            List<Integer> list = new ArrayList<>();
            all:for (int i = 0;;i++) {
                int j = 0;
                for (;j < order.size();j++) {
                    if (entry.getValue().get(j) * i > order.get(j)) {
                        break all;
                    }
                }
                if (j == order.size()) {
                    list.add(new Integer(i));
                }
            }
            map.put(entry.getKey(),list);
        }
        return map;
    }
}

/*
3,3
2,3,1
bom1,2,1,1
bom2,1,1,0
bom3,0,1,1

3,5
6,4,8
bom1,2,1,1
bom2,1,0,1
bom3,0,1,2
bom4,0,1,5
bom5,0,1,0
* */