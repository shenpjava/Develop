package com.shenp.mapper;

import com.shenp.entity.PmsBrand;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PmsBrandMapper {
  List<PmsBrand> listAllBrand();
}