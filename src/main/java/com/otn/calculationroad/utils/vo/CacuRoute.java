package com.otn.calculationroad.utils.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-19 15:16
 * @Description: 算路json
 */
@Data
public class CacuRoute {

    //时延
    @JSONField(name = "delay")
    private List<Edge> delayEdges;
    @JSONField(name = "ac")
    private List<Edge> acEdges;
    @JSONField(name = "node")
    private List<Edge> nodeEdges;

    private List<Edge> commonEdges;
}