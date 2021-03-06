package com.movision.mybatis.rewarded.service;

import com.movision.mybatis.rewarded.entity.Rewarded;
import com.movision.mybatis.rewarded.entity.RewardedVo;
import com.movision.mybatis.rewarded.mapper.RewardedMapper;
import com.movision.utils.L;
import com.movision.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author zhurui
 * @Date 2017/1/24 9:26
 */
@Service
@Transactional
public class RewardedService {

    private Logger log = LoggerFactory.getLogger(RewardedService.class);

    @Autowired
    RewardedMapper rewardedMapper;

    public int insertRewarded(Rewarded rewarded) {
        try {
            log.info("添加打赏记录");
            return rewardedMapper.insertSelective(rewarded);
        } catch (Exception e) {
            log.error("添加打赏记录异常", e);
            throw e;
        }
    }


    /**
     * 帖子打赏
     *
     * @param
     * @param pager
     * @return
     */
    public List<RewardedVo> queryPostAward(Map map, Paging<RewardedVo> pager) {
        try {
            log.info("查看帖子打赏列表");
            return rewardedMapper.findAllqueryPostAward(map, pager.getRowBounds());
        } catch (Exception e) {
            log.error("帖子打赏列表加载失败", e);
            throw e;
        }
    }




}
