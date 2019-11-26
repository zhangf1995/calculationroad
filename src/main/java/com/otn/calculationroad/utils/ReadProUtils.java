package com.otn.calculationroad.utils;

import com.alibaba.fastjson.JSONObject;
import com.otn.calculationroad.constant.CacuConstant;
import com.otn.calculationroad.utils.vo.CacuRoute;
import com.otn.calculationroad.utils.vo.Edge;
import com.otn.calculationroad.utils.vo.Vertex;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-19 14:34
 * @Description: 文件读取及处理
 */
@Slf4j
public class ReadProUtils {


    //图的顶节点集
    public static Map<String,Vertex> verMap = new HashMap<>();
    //图的每个顶点对应的有向边
    public static Map<String,List<Edge>> edgeMap = new HashMap<>();

    public static CacuRoute readFile(String cacuType,String fileName) throws Exception{
        FileReader fileReader = null;
        InputStreamReader isr = null;
        File file = null;
        try {
            file = new File(fileName);
            fileReader = new FileReader(file);
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = isr.read()) != -1) {
                StringBuffer append = sb.append((char)ch);
            }
            String jsonStr = sb.toString();
            log.info("jsonStr is {}", jsonStr);
            CacuRoute cacuRoute = JSONObject.parseObject(jsonStr, CacuRoute.class);
            if(CacuConstant.DELAY.equals(cacuType)){
                cacuRoute.setCommonEdges(cacuRoute.getDelayEdges());
            }else if(CacuConstant.NODE.equals(cacuType)){
                cacuRoute.setCommonEdges(cacuRoute.getNodeEdges());
            }else{
                cacuRoute.setCommonEdges(cacuRoute.getAcEdges());
            }
            return cacuRoute;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }finally {
            try {
                fileReader.close();
                isr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception{
/*        CacuRoute cacuRoute = readFile("minimum-delay", "F:\\test\\delay_ggw.json");
        List<Edge> commonEdges = cacuRoute.getCommonEdges();
        Map<String, Map<String, List<Edge>>> collect = commonEdges.stream().collect(Collectors.groupingBy(Edge::getSouNodeId, Collectors.groupingBy(Edge::getDstNodeId)));
        collect.keySet().stream().forEach(key ->{
            Map<String, List<Edge>> inMap = collect.get(key);
            List<Edge> list = new ArrayList<>();
            inMap.keySet().forEach(inKey ->{
                List<Edge> edgeList = inMap.get(inKey);
                Edge edge = edgeList.stream().min(Comparator.comparingDouble((edge1) -> edge1.getWeight())).get();
                list.add(edge);
            });
            DataHandleUtils.verMap.put(key,new Vertex(key));
            DataHandleUtils.verEdgeMap.put(key,list);
        });
        JSONObject a = DataHandleUtils.bestRoad("192.168.10.4", "192.168.10.15", "a");
        log.info("a {}",a.toString());
        log.info("edges is {}",cacuRoute.getDelayEdges());*/
    }

}