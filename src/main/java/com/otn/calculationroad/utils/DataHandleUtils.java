package com.otn.calculationroad.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.otn.calculationroad.constant.CacuConstant;
import com.otn.calculationroad.utils.vo.CacuRoute;
import com.otn.calculationroad.utils.vo.Edge;
import com.otn.calculationroad.utils.vo.Vertex;
import com.otn.calculationroad.vo.CacuParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-18 10:35
 * @Description: 数据处理（采集，最优路径）,采用dijkstra算法
 */
@Slf4j
public class DataHandleUtils {

    //此处用本地线程localthread也可以
    private static ReentrantLock lock = new ReentrantLock();

    //图的顶节点集
    public static Map<String, Vertex> verMap = new HashMap<>();
    //图的每个顶点对应的有向边
    public static Map<String, List<Edge>> verEdgeMap = new HashMap<>();


    /**
     * 算路
     *
     * @param param
     * @return
     * @throws Exception
     */
    public static JSONObject bestRoad(CacuParam param, String serviceName, String fileName) throws Exception {
        JSONObject returnJson = null;
        try {
            lock.lock();
            String caculateType = param.getCacuType();
            insertData(param, fileName, caculateType);

            switch (caculateType) {
                case CacuConstant.DELAY:
                case CacuConstant.AVAILABILITY:
                case CacuConstant.NODE:
                    if (CacuConstant.NODE.equals(caculateType)) {
                        //最少节点数,所有权重全为1
                        verEdgeMap.keySet().stream().forEach(key -> {
                            List<Edge> edgeList = verEdgeMap.get(key);
                            edgeList.forEach(edge -> {
                                edge.setWeight(Double.valueOf(1));
                            });
                        });
                    }
                    //最小时延
                    returnJson = beforeBestRoad(param, serviceName);
                    break;
                default:
                    //抛错，后期补上
                    throw new Exception("caculateType error");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
            //map清空
            verMap = new HashMap<>();
            verEdgeMap = new HashMap<>();
            lock.unlock();
        }
        return returnJson;
    }

    private static void insertData(CacuParam param, String fileName, String caculateType) throws Exception {
        Double delayPro = param.getDelayPro();
        Double ratioPro = param.getRatioPro();
        Double routePro = param.getRoutePro();
        Double metricPro = param.getMetricPro();
        CacuRoute cacuRoute = ReadProUtils.readFile(caculateType, fileName);
        List<Edge> commonEdges = cacuRoute.getCommonEdges();
        Map<String, Map<String, List<Edge>>> newMap = commonEdges.stream().collect(Collectors.groupingBy(Edge::getSouNodeId, Collectors.groupingBy(Edge::getDstNodeId)));

        newMap.keySet().stream().forEach(key -> {
            Map<String, List<Edge>> inMap = newMap.get(key);
            List<Edge> list = new ArrayList<>();
            inMap.keySet().forEach(inKey -> {
                List<Edge> edgeList = inMap.get(inKey);
                Edge edge = edgeList.stream().min(Comparator.comparingDouble((edge1) -> edge1.getWeight())).get();
                Double newWegiht = Double.valueOf(0);
                if (!ObjectUtils.isEmpty(delayPro)) {
                    newWegiht += (null == edge.getWeight() ? 0 : edge.getWeight()) * delayPro;
                }
                if (!ObjectUtils.isEmpty(ratioPro)) {
                    newWegiht += (null == edge.getRatio() ? 0 : edge.getRatio()) * ratioPro;
                }
                if (!ObjectUtils.isEmpty(routePro)) {
                    newWegiht += (null == edge.getRoute() ? 0 : edge.getRoute()) * routePro;
                }
                if (!ObjectUtils.isEmpty(metricPro)) {
                    newWegiht += (null == edge.getMetric() ? 0 : edge.getMetric()) * metricPro;
                }
                if (0 != newWegiht) {
                    String strWeight = String.format("%.2f", newWegiht);
                    edge.setWeight(Double.valueOf(strWeight));
                }
                list.add(edge);
            });
            verMap.put(key, new Vertex(key));
            verEdgeMap.put(key, list);
        });
    }

    public static JSONObject beforeBestRoad(CacuParam param, String serviceName) throws Exception {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = new JSONArray();
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Boolean flag = param.getIsSingle();
        String srcAreaId = param.getSrcAreaId();
        String dstAreaId = param.getDstAreaId();
        String slaType = param.getSlaType();
        List<Edge> newList = new ArrayList<>();
        if(false == flag){
            List<Edge> edgeList = JSONObject.parseArray(JSONObject.parseObject(param.getNodes()).getString(serviceName), Edge.class);
            Map<String, List<Edge>> collect = edgeList.stream().collect(Collectors.groupingBy(Edge::getSouNodeId));
            collect.keySet().forEach(key -> {
                List<Edge> edgeList1 = collect.get(key);
                Edge edge = edgeList1.stream().min(Comparator.comparingDouble((edge1) -> edge1.getWeight())).get();
                newList.add(edge);
            });
        }
        String souNodeId = param.getSouNodeId();
        String dstNodeId = param.getDstNodeId();
        if (serviceName.equals(srcAreaId)) {
            if (flag) {
                JSONObject json = bestRoad(souNodeId, dstNodeId, serviceName);
                if(!StringUtils.isEmpty(slaType) && slaType.equals("protected")){
                    json.put("type","primary");
                }
                returnArray.add(json);
            } else {
                newList.forEach(edge -> {
                    try {
                        //初始化vertex
                        remove();
                        JSONObject json = bestRoad(souNodeId, edge.getSouNodeId(), serviceName);
                        returnArray.add(json);
                    } catch (Exception e) {
                        //暂时这么抛，后期修改
                        throw new RuntimeException(e.getMessage());
                    }
                });
            }
        } else {
            if (serviceName.equals(dstAreaId)) {
                if (flag) {
                    JSONObject json = bestRoad(souNodeId, dstNodeId, serviceName);
                    returnArray.add(json);
                } else {
                    newList.forEach(edge -> {
                        try {
                            //初始化vertex
                            remove();
                            JSONObject json = bestRoad(edge.getSouNodeId(), dstNodeId, serviceName);
                            returnArray.add(json);
                        } catch (Exception e) {
                            //e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }
                    });
                }
            } else {
                if (flag) {
                    JSONObject json = bestRoad(souNodeId, dstNodeId, serviceName);
                    returnArray.add(json);
                } else {
                    newList.forEach(outEdge -> {
                        newList.forEach(inEdge -> {
                            try {
                                if (!outEdge.getSouNodeId().equals(inEdge.getSouNodeId())) {
                                    //初始化vertex
                                    remove();
                                    log.info("sou is {},dst is {}", outEdge.getSouNodeId(), inEdge.getSouNodeId());
                                    JSONObject json = bestRoad(outEdge.getSouNodeId(), inEdge.getSouNodeId(), serviceName);
                                    returnArray.add(json);
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }
                        });
                    });
                }
            }
        }


        if (atomicInteger.get() == 0 && flag && "protected".equals(slaType)) {
            secondRoute(returnArray, souNodeId, dstNodeId, serviceName);
        }

        log.info("returnArray is {}", returnArray.toString());
        returnJson.put("path", returnArray);
        return returnJson;
    }

    private static void secondRoute(JSONArray returnArray, String souNodeId, String dstNodeId, String serviceName) throws Exception {
        List<JSONObject> jsonList = JSONObject.parseArray(returnArray.toJSONString(), JSONObject.class);
        jsonList.forEach(inJson -> {
            JSONArray pathArray = inJson.getJSONArray("path");
            List<JSONObject> inList = JSONObject.parseArray(pathArray.toJSONString(), JSONObject.class);
            inList.forEach(innerJson -> {
                Vertex vertex = JSONObject.parseObject(innerJson.toJSONString(), Vertex.class);
                String parentNodeId = vertex.getParentNodeId();
                if (!parentNodeId.equals(souNodeId)) {
                    verMap.remove(parentNodeId);
                    if (verEdgeMap.containsKey(parentNodeId)) {
                        verEdgeMap.remove(parentNodeId);
                    }
                    verEdgeMap.keySet().stream().forEach(key -> {
                        List<Edge> edges = verEdgeMap.get(key);
                        List<Edge> edges1 = edges.stream().filter(edge -> edge.getDstNodeId().equals(parentNodeId)).collect(Collectors.toList());
                        edges1.forEach(inEdge -> {
                            edges.remove(inEdge);
                        });
                    });
                } else {
                    List<Edge> edgeList1 = verEdgeMap.get(parentNodeId);
                    List<Edge> edges1 = edgeList1.stream().filter(edge -> edge.getDstNodeId().equals(parentNodeId)).collect(Collectors.toList());
                    edges1.forEach(inEdge -> {
                        edges1.remove(inEdge);
                    });
                }
            });
        });
        remove();
        JSONObject jsonObject = bestRoad(souNodeId, dstNodeId, serviceName);
        jsonObject.put("type","secondary");
        returnArray.add(jsonObject);
    }

    /**
     * 最优算法
     *
     * @param souNodeId
     * @param dstNodeId
     * @param serviceName
     * @return
     */
    public static JSONObject bestRoad(String souNodeId, String dstNodeId, String serviceName) throws Exception {
        JSONObject returnJson = new JSONObject();
        JSONArray returnArray = new JSONArray();
        Vertex souVertex = verMap.get(souNodeId);
        if (ObjectUtils.isEmpty(souVertex)) {
            throw new Exception("souVertex is null");
        }
        souVertex.setDist(Double.valueOf(0));
        //更新领接表每个节点
        updateChildren(souVertex);
        Vertex dstVertex = verMap.get(dstNodeId);
        Vertex cloneDstVer = (Vertex) dstVertex.clone();
        modifyVertex(serviceName, cloneDstVer);

        returnArray.add(cloneDstVer);
        log.info("bestRoadOfDelay {}", dstVertex.getDist());
        while (!StringUtils.isEmpty(dstVertex.getParentNodeId()) && (!dstVertex.getNodeId().equals(souNodeId))) {
            dstVertex = verMap.get(dstVertex.getParentNodeId());
            if (!StringUtils.isEmpty(dstVertex.getParentNodeId()) && (!dstVertex.getNodeId().equals(souNodeId))) {
                Vertex sonVertex = returnArray.getObject(returnArray.size() - 1, Vertex.class);
                sonVertex.setDist(sonVertex.getDist() - dstVertex.getDist());
                //clone
                Vertex newSonVertex = (Vertex) sonVertex.clone();
                modifyVertex(serviceName, newSonVertex);
                returnArray.remove(sonVertex);
                returnArray.add(newSonVertex);
                Vertex cloneVer = (Vertex) dstVertex.clone();
                modifyVertex(serviceName, cloneVer);
                returnArray.add(cloneVer);
            }

        }
        List<Vertex> vertexList = JSONObject.parseArray(returnArray.toJSONString(), Vertex.class);
        //返回格式整理
        managerData(returnJson, vertexList, souNodeId, dstNodeId);
        returnJson.put("path", returnArray);
        log.info("bestRoadOfDelay,returnJson is {}", returnJson);
        return returnJson;
    }

    private static void modifyVertex(String serviceName, Vertex cloneDstVer) {
        cloneDstVer.setSrcAreaId(serviceName);
        cloneDstVer.setDstAreaId(serviceName);

        List<Edge> edgeList = verEdgeMap.get(cloneDstVer.getParentNodeId());
        edgeList.forEach(edge -> {
            if (cloneDstVer.getParentNodeId().equals(edge.getSouNodeId()) && cloneDstVer.getNodeId().equals(edge.getDstNodeId())) {
                cloneDstVer.setSrcInterfaceIp(edge.getSrcInterfaceIp());
                cloneDstVer.setDstInterfaceIp(edge.getDstInterfaceIp());
            }
        });
    }

    /**
     * 整理数据
     *
     * @param returnJson
     * @param souNodeId
     * @param dstNodeId
     */
    private static void managerData(JSONObject returnJson, List<Vertex> vertexList, String souNodeId, String dstNodeId) {
        double maxDist = vertexList.stream().mapToDouble(Vertex::getDist).sum();
        returnJson.put("outWeight", maxDist);
        returnJson.put("outSrcNodeId", souNodeId);
        returnJson.put("outDstNodeId", dstNodeId);
    }

    /**
     * 从初始节点开始递归更新领接表
     */
    public static void updateChildren(Vertex v) throws Exception {
        if (ObjectUtils.isEmpty(v) || ObjectUtils.isEmpty(verEdgeMap.get(v.getNodeId())) || verEdgeMap.get(v.getNodeId()).size() == 0) {
            return;
        }
        List<Vertex> childList = new ArrayList<>();
        List<Edge> edgeList = verEdgeMap.get(v.getNodeId());
        edgeList.forEach(edge -> {
            Vertex dstVertex = verMap.get(edge.getDstNodeId());
            if (false == dstVertex.getKonw()) {
                dstVertex.setKonw(true);
                dstVertex.setDist(v.getDist() + edge.getWeight());
                dstVertex.setParentNodeId(v.getNodeId());
                childList.add(dstVertex);
            } else {
                double nowDist = v.getDist() + edge.getWeight();
                if (nowDist < dstVertex.getDist()) {
                    dstVertex.setDist(nowDist);
                    dstVertex.setParentNodeId(edge.getSouNodeId());
                    childList.add(dstVertex);
                }
            }
        });

        for (Vertex vertex : childList) {
            updateChildren(vertex);
        }
      /*  childList.forEach(vertex -> {

        });*/
    }

    /**
     * 每次最优算法算完，初始化vertex
     */
    public static void remove() {
        verMap.keySet().stream().forEach(key -> {
            Vertex vertex = verMap.get(key);
            vertex.resotre();
        });
    }


    public static void secondRoute() {

    }
}
