package com.shenp.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.shenp.mapper.PmsBrandMapper;
import com.shenp.entity.PmsBrand;
import com.shenp.service.PmsBrandService;

import java.util.List;

@Service
public class PmsBrandServiceImpl implements PmsBrandService {

//  @Resource
  private PmsBrandMapper pmsBrandMapper;

  public PmsBrandMapper getPmsBrandMapper() {
    return pmsBrandMapper;
  }

  @Resource
  public void setPmsBrandMapper(PmsBrandMapper pmsBrandMapper) {
    this.pmsBrandMapper = pmsBrandMapper;
  }

  @Override
  public List<PmsBrand> listAllBrand() {
    return pmsBrandMapper.listAllBrand();
  }
}
