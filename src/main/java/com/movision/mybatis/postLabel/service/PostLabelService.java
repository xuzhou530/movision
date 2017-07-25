package com.movision.mybatis.postLabel.service;

import com.movision.mybatis.postLabel.entity.PostLabel;
import com.movision.mybatis.postLabel.entity.PostLabelCount;
import com.movision.mybatis.postLabel.entity.PostLabelDetails;
import com.movision.mybatis.postLabel.entity.PostLabelTz;
import com.movision.mybatis.postLabel.mapper.PostLabelMapper;
import com.movision.utils.pagination.model.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.peer.LabelPeer;
import java.util.List;
import java.util.Map;

/**
 * @Author zhanglei
 * @Date 2017/7/19 15:50
 */
@Service
public class PostLabelService {

    private static Logger log = LoggerFactory.getLogger(PostLabelService.class);
    @Autowired
    private PostLabelMapper postLabelMapper;

    public List<PostLabel> queryLabelName() {
        try {
            log.info("查询所有标签");
            return postLabelMapper.queryLableName();
        } catch (Exception e) {
            log.error("查询所有标签失败", e);
            throw e;
        }
    }

    public Integer updateLabelHeatValue(Map map) {
        try {
            log.info("根据标签修改热度");
            return postLabelMapper.updateLabelHeatValue(map);
        } catch (Exception e) {
            log.error("根据标签修改热度失败", e);
            throw e;
        }
    }

    public List<PostLabel> queryLabelHeatValue() {
        try {
            log.info("根据热度排序");
            return postLabelMapper.queryLabelHeatValue();
        } catch (Exception e) {
            log.error("根据热度排序失败", e);
            throw e;
        }
    }

    public PostLabelTz queryName(int labelid) {
        try {
            log.info("查询名称");
            return postLabelMapper.queryName(labelid);
        } catch (Exception e) {
            log.error("查询名称失败", e);
            throw e;
        }
    }

    /**
     * 查询帖子标签列表
     *
     * @param label
     * @param pag
     * @return
     */
    public List<PostLabelDetails> findAllQueryPostLabelList(PostLabel label, Paging<PostLabelDetails> pag) {
        try {
            log.info("查询帖子标签列表");
            return postLabelMapper.findAllQueryPostLabelList(label, pag.getRowBounds());
        } catch (Exception e) {
            log.error("查询帖子标签列表异常", e);
            throw e;
        }
    }

    public int batchInsert(List<PostLabel> postLabelList) {
        try {
            log.info("新增帖子标签");
            return postLabelMapper.batchInsert(postLabelList);
        } catch (Exception e) {
            log.error("新增帖子标签失败", e);
            throw e;
        }
    }

    /**
     * 新增标签
     *
     * @param label
     */
    public void insertPostLabel(PostLabel label) {
        try {
            log.info("新增标签");
            postLabelMapper.insertSelective(label);
        } catch (Exception e) {
            log.error("新增标签异常", e);
            throw e;
        }
    }

    /**
     * 查询标签详情
     *
     * @param label
     * @return
     */
    public PostLabelDetails queryPostLabelById(PostLabel label) {
        try {
            log.info("查询标签详情");
            return postLabelMapper.queryPostLabelById(label);
        } catch (Exception e) {
            log.error("查询标签详情异常", e);
            throw e;
        }
    }

    /**
     * 修改帖子标签
     *
     * @param label
     */
    public void updatePostLabel(PostLabel label) {
        try {
            log.info("修改帖子标签");
            postLabelMapper.updateByPrimaryKeySelective(label);
        } catch (Exception e) {
            log.error("修改帖子标签异常", e);
            throw e;
        }
    }

    /**
     * 删除帖子标签
     *
     * @param label
     */
    public void deletePostLabel(PostLabel label) {
        try {
            log.info("删除帖子标签");
            postLabelMapper.deletePostLabel(label);
        } catch (Exception e) {
            log.error("删除帖子标签异常", e);
            throw e;
        }
    }


    public List<PostLabelCount> queryCountLabelName(int labelid) {
        try {
            log.info("查询帖子标签列表");
            return postLabelMapper.queryCountLabelName(labelid);
        } catch (Exception e) {
            log.error("查询帖子标签列表异常", e);
            throw e;
        }
    }

}
