package com.otn.calculationroad.utils.vo;

import lombok.Data;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-18 15:35
 * @Description: 第一个节点到这个节点距离
 */
@Data
public class Vertex implements Cloneable{
    private String nodeId;
    private Double dist;
    private Boolean konw;
    private String parentNodeId;
    private String srcAreaId;
    private String dstAreaId;
    private String srcInterfaceIp;
    private String dstInterfaceIp;

    private final static Double infinite_dis = Double.MAX_VALUE;

    public Vertex(String nodeId) {
        this.nodeId = nodeId;
        this.dist = infinite_dis;
        this.konw = false;
        this.parentNodeId = null;
    }

    public void resotre(){
        this.dist = infinite_dis;
        this.konw = false;
        this.parentNodeId = null;
    }

    public Vertex() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}