package com.otn.calculationroad.controller;

import com.alibaba.fastjson.JSONObject;
import com.otn.calculationroad.constant.CacuConstant;
import com.otn.calculationroad.en.StateCode;
import com.otn.calculationroad.resp.Result;
import com.otn.calculationroad.utils.DataHandleUtils;
import com.otn.calculationroad.vo.CacuParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataUnit;
import org.springframework.web.bind.annotation.*;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-11-19 16:56
 * @Description: 算路controller
 */
@Slf4j
@RequestMapping("/cacuRoute")
@RestController
public class CacuController {

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${file.delay.location}")
    private String delayFileName;

    @Value("${file.ac.location}")
    private String acFileName;

    @Value("${file.node.location}")
    private String nodeFileName;

    /**
     * 算路器内部最优路径集合
     *
     * @param param
     * @throws Exception
     */
    @RequestMapping(value = "/cacuRouteList", method = RequestMethod.POST)
    public Result cacuRouteList(@RequestBody CacuParam param) throws Exception {
        if (ObjectUtils.isEmpty(param) || StringUtils.isEmpty(param.getSouNodeId()) || StringUtils.isEmpty(param.getDstNodeId()) || StringUtils.isEmpty(param.getCacuType())) {
            return Result.me().response(StateCode.FAIL.getCode(), CacuConstant.PARAM_NULL);
        }
        JSONObject json = null;
        try {
            String fileName = null;
            if (CacuConstant.DELAY.equals(param.getCacuType())) {
                fileName = delayFileName;
            } else if (CacuConstant.NODE.equals(param.getCacuType())) {
                fileName = acFileName;
            } else {
                fileName = nodeFileName;
            }
            json = DataHandleUtils.bestRoad(param, serviceName,fileName);
            log.info("serviceName is {},json is {}",serviceName,json.toString());
        } catch (Exception e) {
            return Result.me().response(StateCode.FAIL.getCode(), e.getMessage());
        }
        return Result.me().response(StateCode.SUCCESS.getCode(), StateCode.SUCCESS.getMsg(), json);
    }
}