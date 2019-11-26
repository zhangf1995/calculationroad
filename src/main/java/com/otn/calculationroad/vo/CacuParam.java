package com.otn.calculationroad.vo;

import com.otn.calculationroad.utils.vo.Edge;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-19 18:31
 * @Description: 算路参数
 */
@Data
public class CacuParam {

/*    private List<String> souNodes;
    private List<String> dstNodes;*/

    private String nodes;
    private String souNodeId;
    private String dstNodeId;
    private String cacuType;
    private String srcAreaId;
    private String dstAreaId;
    private Double delayPro;
    private Double ratioPro;
    private Double routePro;
    private Double metricPro;
    private Boolean isSingle;
    private String slaType;
}