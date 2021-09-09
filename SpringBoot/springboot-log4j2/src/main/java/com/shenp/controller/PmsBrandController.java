package com.shenp.controller;

import com.shenp.entity.PmsBrand;
import com.shenp.service.PmsBrandService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "PmsBrandController", description = "商品品牌管理")
@Controller
@RequestMapping("/brand")
public class PmsBrandController {
  private PmsBrandService demoService;

  public PmsBrandService getDemoService() {
    return demoService;
  }

  @Resource
  public void setDemoService(PmsBrandService demoService) {
    this.demoService = demoService;
  }

  private static final Logger log = LoggerFactory.getLogger(PmsBrandController.class);

  @ApiOperation("获取所有品牌列表")
  @RequestMapping(value = "listAll", method = RequestMethod.GET)
  @ResponseBody
  public List<PmsBrand> getBrandList() {
    return demoService.listAllBrand();
  }
}
