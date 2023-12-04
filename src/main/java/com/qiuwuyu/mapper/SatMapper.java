package com.qiuwuyu.mapper;

import com.qiuwuyu.entity.Sat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author paralog
 * @date 2022/10/7 15:22
 */
@Mapper
@Repository
public interface SatMapper {

    @Select("select * from stas where `id` = #{id}")
    Sat querySatById(String id);

    @Select("select * from stas")
    List<Sat> queryAllSats();

    @Insert("insert into `stas`()")
    int insertSat();
}
