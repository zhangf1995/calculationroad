package com.otn.calculationroad.vo;

import com.otn.calculationroad.anno.ValidateVariable;
import lombok.Data;



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
    @ValidateVariable(field = "souNodeId")
    private String souNodeId;
    @ValidateVariable(field = "dstNodeId")
    private String dstNodeId;
    @ValidateVariable(field = "cacuType")
    private String cacuType;
    @ValidateVariable(field = "srcAreaId")
    private String srcAreaId;
    @ValidateVariable(field = "dstAreaId")
    private String dstAreaId;
    @ValidateVariable(field = "delayPro")
    private Double delayPro;
    @ValidateVariable(field = "ratioPro")
    private Double ratioPro;
    @ValidateVariable(field = "routePro")
    private Double routePro;
    @ValidateVariable(field = "metricPro")
    private Double metricPro;
    @ValidateVariable(field = "isSingle")
    private Boolean isSingle;
    @ValidateVariable(field = "slaType")
    private String slaType;
}