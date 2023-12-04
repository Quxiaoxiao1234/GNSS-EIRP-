package com.qiuwuyu.satellite;

import com.qiuwuyu.entity.Sat;
import com.qiuwuyu.mapper.SatMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SatelliteApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private SatMapper satMapper;



    @Test
    public void testQuerySatById(){
        System.out.println(satMapper.querySatById("1"));
    }

    @Test
    public void testQueryAllSats(){
        List<Sat> sats = satMapper.queryAllSats();
        for(Sat sat : sats){
            System.out.println(sat);
        }
    }
}
