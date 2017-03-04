package com.movision.facade.boss;

import com.movision.common.Response;
import com.movision.common.util.ShiroUtil;
import com.movision.mybatis.accusation.service.AccusationService;
import com.movision.mybatis.activePart.entity.ActivePart;
import com.movision.mybatis.activePart.entity.ActivePartList;
import com.movision.mybatis.activePart.service.ActivePartService;
import com.movision.mybatis.circle.entity.Circle;
import com.movision.mybatis.circle.entity.CircleIndexList;
import com.movision.mybatis.circle.service.CircleService;
import com.movision.mybatis.comment.entity.Comment;
import com.movision.mybatis.comment.entity.CommentVo;
import com.movision.mybatis.comment.service.CommentService;
import com.movision.mybatis.goods.entity.GoodsVo;
import com.movision.mybatis.goods.service.GoodsService;
import com.movision.mybatis.period.entity.Period;
import com.movision.mybatis.period.service.PeriodService;
import com.movision.mybatis.post.entity.*;
import com.movision.mybatis.post.service.PostService;
import com.movision.mybatis.rewarded.entity.Rewarded;
import com.movision.mybatis.rewarded.entity.RewardedVo;
import com.movision.mybatis.rewarded.service.RewardedService;
import com.movision.mybatis.share.entity.SharesVo;
import com.movision.mybatis.share.service.SharesService;
import com.movision.mybatis.user.entity.User;
import com.movision.mybatis.user.entity.UserLike;
import com.movision.mybatis.user.service.UserService;
import com.movision.mybatis.video.entity.Video;
import com.movision.mybatis.video.service.VideoService;
import com.movision.utils.IntegerUtil;
import com.movision.utils.ListUtil;
import com.movision.utils.pagination.model.Paging;
import com.movision.utils.pagination.util.StringUtils;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author zhurui
 * @Date 2017/2/7 9:16
 */
@Service
public class PostFacade {
    @Value("#{configProperties['img.domain']}")
    private String imgdomain;

    @Value("#{configProperties['vidfeld.domain']}")
    private String viddomain;

    @Value("#{configProperties['vidbnner.domain']}")
    private String vidbannerdomain;

    @Autowired
    private PostService postService;

    @Autowired
    private CircleService circleService;

    @Autowired
    private UserService userService;

    @Autowired
    private SharesService sharesService;

    @Autowired
    private RewardedService rewardedService;

    @Autowired
    private ActivePartService activePartService;

    @Autowired
    private AccusationService accusationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private GoodsService goodsService;

    private static Logger log = LoggerFactory.getLogger(PostFacade.class);


    /**
     * 后台管理-查询帖子列表
     *
     * @param pager
     * @return
     */
    public List<PostList> queryPostByList(Paging<PostList> pager) {
        List<PostList> list = postService.queryPostByList(pager);
        return list;
    }

    /**
     * 后台管理-查询活动列表（草稿箱）
     *
     * @param pager
     * @return
     */
    public List<PostList> queryPostActiveByList(Paging<PostList> pager) {
        List<PostList> list = postService.queryPostActiveByList(pager);
        List<PostList> rewardeds = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            PostList postList = new PostList();
            Integer circleid = list.get(i).getCircleid();//获取到圈子id
            String nickname = userService.queryUserByNickname(circleid);//获取发帖人
            postList.setId(list.get(i).getId());
            postList.setTitle(list.get(i).getTitle());
            postList.setNickname(nickname);
            postList.setIntime(list.get(i).getIntime());
            rewardeds.add(postList);
        }
        return rewardeds;
    }

    /**
     * 后台管理-查询活动列表
     *
     * @param pager
     * @return
     */
    public List<PostActiveList> queryPostActiveToByList(Paging<PostActiveList> pager) {
        List<PostActiveList> list = postService.queryPostActiveToByList(pager);
        List<PostActiveList> rewardeds = new ArrayList<>();
        Date date = new Date();
        long str = date.getTime();
        for (int i = 0; i < list.size(); i++) {
            PostActiveList postList = new PostActiveList();
            Integer postid = list.get(i).getId();//获取到帖子id
            Integer persum = postService.queryPostPerson(postid);
            String nickname = userService.queryUserByNicknameBy(postid);
            Period periods = periodService.queryPostPeriod(postid);
            Double activefee = list.get(i).getActivefee();
            Double sumfree = 0.0;
            if (activefee != null) {
                sumfree = persum * activefee;
            }

            Date begintime = periods.getBegintime();
            Date endtime = periods.getEndtime();
            postList.setId(list.get(i).getId());
            postList.setTitle(list.get(i).getTitle());//主题
            postList.setNickname(nickname);//昵称
            postList.setActivetype(list.get(i).getActivetype());//活动类型
            postList.setActivefee(activefee);//活动费用
            postList.setEssencedate(list.get(i).getEssencedate());//精选日期
            postList.setOrderid(list.get(i).getOrderid());//精选排序
            postList.setBegintime(begintime);//开始时间
            postList.setEndtime(endtime);//结束时间
            postList.setPersum(persum);//报名人数
            postList.setSumfree(sumfree);//总费用
            String activeStatue = "";
            if (begintime != null && endtime != null) {
                long begin = begintime.getTime();
                long end = endtime.getTime();
                if (str > begin && str < end) {
                    activeStatue = "报名中";
                } else if (str < begin) {
                    activeStatue = "未开始";
                } else if (str > end) {
                    activeStatue = "已结束";
                }
            }
            postList.setActivestatue(activeStatue);//活动状态
            postList.setZansum(list.get(i).getZansum());
            postList.setCollectsum(list.get(i).getCollectsum());
            postList.setCommentsum(list.get(i).getCommentsum());
            postList.setForwardsum(list.get(i).getForwardsum());
            postList.setIntime(list.get(i).getIntime());
            rewardeds.add(postList);
        }
        return rewardeds;
    }

    /**
     * 后台管理-查询报名列表记录
     *
     * @param pager
     * @return
     */
    public List<ActivePartList> queyPostCallActive(Paging<ActivePartList> pager) {
        List<ActivePartList> list = activePartService.queryPostCallActive(pager);
        List<ActivePartList> rewardeds = new ArrayList<>();
        Date date = new Date();
        long str = date.getTime();
        for (int i = 0; i < list.size(); i++) {
            ActivePartList postActiveList = new ActivePartList();
            Integer postid = list.get(i).getPostid();//获取到帖子id
            Double activefee = postService.queryPostActiveFee(postid);
            Period periods = periodService.queryPostPeriod(postid);
            postActiveList.setActivefee(activefee);//活动单价
            Integer id = list.get(i).getUserid();//用户id
            User user = userService.queryUserB(id);
            Date begintime = periods.getBegintime();
            Date endtime = periods.getEndtime();
            postActiveList.setBegintime(begintime);
            postActiveList.setEndtime(endtime);
            long begin = begintime.getTime();
            long end = endtime.getTime();
            String activeStatue = "";
            if (str > begin && str < end) {
                activeStatue = "报名中";
            } else if (str < begin) {
                activeStatue = "未开始";
            } else if (str > end) {
                activeStatue = "已结束";
            }
            postActiveList.setActivestatue(activeStatue);//活动状态
            postActiveList.setIntime(list.get(i).getIntime());//报名时间
            postActiveList.setNickname(user.getNickname());//用户名
            postActiveList.setPhone(user.getPhone());//联系方式
            postActiveList.setPayStatue(list.get(i).getPayStatue());//支付方式
            postActiveList.setMoneypay(list.get(i).getMoneypay());//实付金额
            postActiveList.setMoneyying(list.get(i).getMoneyying());//应付金额
            rewardeds.add(postActiveList);
        }
        return rewardeds;
    }

    /**
     * 后台管理-帖子列表-发帖人信息
     *
     * @param postid
     * @return
     */
    public User queryPostByPosted(String postid) {
        Integer circleid = postService.queryPostByCircleid(postid);
        String phone = circleService.queryCircleByPhone(circleid);//获取圈子中的用户手机号
        User user = userService.queryUser(phone);
        return user;
    }

    /**
     * 后台管理-帖子列表-删除帖子
     *
     * @param postid
     * @return
     */
    public Map<String, Integer> deletePost(String postid) {
        int resault = postService.deletePost(Integer.parseInt(postid));
        Map<String, Integer> map = new HashedMap();
        map.put("resault", resault);
        return map;
    }


    /**
     * 后台管理-帖子列表-查看评论
     *
     * @param postid
     * @param pager
     * @return
     */
    public List<CommentVo> queryPostAppraise(String postid, String type, Paging<CommentVo> pager) {
        Map map = new HashedMap();
        map.put("postid", postid);
        map.put("type", type);
        return commentService.queryCommentsByLsit(pager, map);
    }

    /**
     * 后台管理-帖子列表-帖子评论列表-帖子详情
     *
     * @param commentid
     * @param pager
     * @return
     */
    public List<CommentVo> queryPostByCommentParticulars(String commentid, String postid, Paging<CommentVo> pager) {
        Map<String, Integer> map = new HashedMap();
        List<CommentVo> resautl = new ArrayList<>();
        map.put("commentid", Integer.parseInt(commentid));
        map.put("postid", Integer.parseInt(postid));
        CommentVo com = commentService.queryCommentById(map);
        List<CommentVo> list = commentService.queryPostByCommentParticulars(map, pager);
        com.setSoncomment(list);
        resautl.add(com);
        return resautl;
    }

    /**
     * 后台管理-帖子列表-帖子评论列表-添加评论
     *
     * @param postid
     * @param userid
     * @param content
     * @return
     */
    public Map<String, Integer> addPostAppraise(String postid, String userid, String content) {
        CommentVo comm = new CommentVo();
        comm.setPostid(Integer.parseInt(postid));
        comm.setUserid(Integer.parseInt(userid));
        comm.setIntime(new Date());
        comm.setContent(content);
        Map<String, Integer> map = new HashedMap();
        int status;
        int c = commentService.insertComment(comm);
        if (c == 1) {
            postService.updatePostBycommentsum(Integer.parseInt(postid));//更新帖子的评论数
            status = 2;
            map.put("status", status);
        }
        return map;
    }

    /**
     * 后台管理-帖子列表-删除帖子评论
     *
     * @param id
     * @return
     */
    public int deletePostAppraise(String id) {
        return commentService.deletePostAppraise(Integer.parseInt(id));
    }

    /**
     * 后台管理-帖子列表-评论列表-编辑评论
     *
     * @param commentid
     * @param content
     * @param userid
     * @return
     */
    public Map updatePostComment(String commentid, String content, String userid) {
        Map chuan = new HashedMap();
        Map map = new HashedMap();
        chuan.put("commentid", commentid);
        chuan.put("content", content);
        chuan.put("userid", userid);
        int i = commentService.updatePostComment(chuan);
        map.put("resault", i);
        return map;
    }

    /**
     * 回复帖子评论
     *
     * @param pid
     * @param content
     * @param userid
     * @return
     */
    public Map replyPostComment(String pid, String content, String postid, String userid) {
        Map chuan = new HashedMap();
        Map map = new HashedMap();
        chuan.put("pid", pid);
        chuan.put("content", content);
        chuan.put("userid", userid);
        chuan.put("postid", postid);
        chuan.put("intime", new Date());
        int i = commentService.replyPostComment(chuan);
        map.put("resault", i);
        return map;
    }

    /**
     * 后台管理-帖子列表-帖子打赏列表
     *
     * @param postid
     * @param pager
     * @return
     */
    public List<RewardedVo> queryPostAward(String postid, Paging<RewardedVo> pager) {
        return rewardedService.queryPostAward(Integer.parseInt(postid), pager);//分页返回帖子打赏列表
    }

    /**
     * 后台管理-帖子列表-帖子预览
     *
     * @param postid
     * @return
     */
    public PostList queryPostParticulars(String postid) {
        PostList postList = postService.queryPostParticulars(Integer.parseInt(postid));
        return postList;
    }

    /**
     * 后台管理*-活动预览
     *
     * @param postid
     * @return
     */
    public PostList queryPostActiveQ(String postid) {
        PostList postList = postService.queryPostParticulars(Integer.parseInt(postid));
        Integer id = postList.getId();
        Date date = new Date();
        long str = date.getTime();
        Integer share = sharesService.querysum(id);
        Period periods = periodService.queryPostPeriod(Integer.parseInt(postid));
        String nickname = userService.queryUserByNicknameBy(Integer.parseInt(postid));//获取发帖人
        postList.setNickname(nickname);
        postList.setShare(share);//分享次数
        postList.setTitle(postList.getTitle());//主标题
        postList.setSubtitle(postList.getSubtitle());//副标题
        postList.setEssencedate(postList.getEssencedate());//精选日期
        postList.setZansum(postList.getZansum());//赞
        postList.setCommentsum(postList.getCommentsum());//评论
        postList.setCollectsum(postList.getCollectsum());//收藏
        postList.setActivetype(postList.getActivetype());//活动类型
        Date begintime = periods.getBegintime();
        Date endtime = periods.getEndtime();
        long begin = begintime.getTime();
        long end = endtime.getTime();
        String activeStatue = "";
        if (str > begin && str < end) {
            activeStatue = "报名中";
        } else if (str < begin) {
            activeStatue = "未开始";
        } else if (str > end) {
            activeStatue = "已结束";
        }
        postList.setActivestatue(activeStatue);//活动状态
        postList.setActivefee(postList.getActivefee());//活动单价
        Integer persum = postService.queryPostPerson(Integer.parseInt(postid));
        postList.setPersum(persum);//报名人数
        postList.setPostcontent(postList.getPostcontent());//活动内容
        postList.setIntime(postList.getIntime());//发布时间
        return postList;
    }

    /**
     * 添加帖子
     *
     * @param title
     * @param subtitle
     * @param type
     * @param circleid
     * @param coverimg
     * @param postcontent
     * @param isessence
     * @param time
     * @return
     */
    public Map<String, Integer> addPost(String title, String subtitle, String type, String circleid,
                                        String userid, String coverimg, String vid, String bannerimgurl,
                                        String postcontent, String isessence, String ishot, String orderid, String time, String goodsid) {
        PostTo post = new PostTo();
        Map<String, Integer> map = new HashedMap();
            post.setTitle(title);//帖子标题
            post.setSubtitle(subtitle);//帖子副标题
            post.setType(type);//帖子类型
            post.setCircleid(circleid);//圈子id
            post.setCoverimg(bannerimgurl);//添加帖子封面
            post.setIsactive("0");//设置状态为帖子
            post.setPostcontent(postcontent);//帖子内容
            post.setIntime(new Date());//插入时间
            if (ishot != null) {
                post.setIshot(ishot);//是否为圈子精选
            }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d = null;
            if (isessence != null) {
                if (isessence != "0") {//判断是否为加精
                    post.setIsessence(isessence);//是否为首页精选
                    if (!orderid.isEmpty()) {
                        post.setOrderid(orderid);
                    } else {
                        post.setOrderid("0");
                    }
                    if (time != null) {
                        try {
                            d = format.parse(time);
                            post.setEssencedate(d);
                        } catch (ParseException e) {
                            log.error("时间插入异常");
                        }
                    }

                }
            }
        post.setCoverimg(coverimg);//插入图片地址
            post.setUserid(userid);
            int result = postService.addPost(post);//添加帖子
            Integer pid = post.getId();//获取到刚刚添加的帖子id
            Video vide = new Video();
            vide.setPostid(pid);
            vide.setVideourl(vid);
        vide.setBannerimgurl(bannerimgurl);
            vide.setIntime(new Date());
            Integer in = videoService.insertVideoById(vide);//添加视频表
            if (goodsid != null) {//添加商品
                String[] lg = goodsid.split(",");//以逗号分隔
                for (int i = 0; i < lg.length; i++) {
                    Map addgoods = new HashedMap();
                    addgoods.put("postid", pid);
                    addgoods.put("goodsid", lg[i]);
                    postService.insertGoods(addgoods);
                }
            }
            map.put("result", result);
        return map;

    }

    /**
     * 后台管理--增加活动
     *
     * @param title
     * @param subtitle
     * @param
     * @param
     * @param coverimg
     * @param postcontent
     * @param isessence
     * @param orderid
     * @param time
     * @param begintime
     * @param endtime
     * @param userid
     * @return
     */
    public Map<String, Integer> addPostActive(String title, String subtitle, String activetype, String activefee,
                                              String coverimg, String postcontent, String isessence, String orderid, String time, String begintime, String endtime, String userid) {
        PostTo post = new PostTo();
        Map<String, Integer> map = new HashedMap();

            post.setTitle(title);//帖子标题
            post.setSubtitle(subtitle);//帖子副标题
        Integer typee = Integer.parseInt(activetype);
        post.setActivetype(activetype);
            if (typee == 0) {
                post.setActivefee(Double.parseDouble(activefee));//金额
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            post.setCoverimg(coverimg);
            post.setPostcontent(postcontent);//帖子内容
            if (!isessence.isEmpty() || isessence != null) {
                if (Integer.parseInt(isessence) != 0) {//判断是否为加精
                    post.setIsessence(isessence);//是否为首页精选
                    if (!orderid.isEmpty()) {
                        post.setOrderid(orderid);
                    } else {
                        post.setOrderid("0");
                    }
                    Date d = null;
                    if (time != null || time != "") {
                        try {
                            d = format.parse(time);
                            post.setEssencedate(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }

            post.setIntime(new Date());
        if (orderid != null) {
                post.setOrderid(orderid);//排序精选
            }
            post.setUserid(userid);//发帖人
            post.setIsactive("1");
        int result = postService.addPostActiveList(post);//添加帖子
            Period period = new Period();
        Date begin = null;//开始时间
        if (begintime != null) {
            try {
                begin = format.parse(begintime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date end = null;
        if (endtime != null) {
            try {
                end = format.parse(endtime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
            period.setBegintime(begin);
            period.setEndtime(end);
            Integer id = post.getId();
            period.setPostid(id);
            int r = postService.addPostPeriod(period);
            map.put("result", result);
            map.put("result", r);

        return map;
    }

    /**
     * 帖子加精/取消加精
     *
     * @param postid
     * @return
     */
    public Map<String, Integer> addPostChoiceness(String postid, String subtitle, String essencedate, String orderid) {
        Map<String, Integer> map = new HashedMap();
        PostTo p = new PostTo();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date esdate = null;
        if (Integer.parseInt(orderid) > 0) {//加精动作
            p.setId(Integer.parseInt(postid));
            if (essencedate != null) {
                try {
                    esdate = format.parse(essencedate);
                    p.setEssencedate(esdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            p.setOrderid(orderid);
            p.setSubtitle(subtitle);
            Integer result = postService.addPostChoiceness(p);
            map.put("result", result);
            return map;
        } else {//取消加精
            int i = postService.deletePostChoiceness(Integer.parseInt(postid));
            if (i == 1) {
                Integer t = 2;
                map.put("result", t);
            }
            return map;
        }
    }

    Logger logger = LoggerFactory.getLogger(PostFacade.class);

    /**
     * 查询加精排序
     *
     * @return
     */
    public PostChoiceness queryPostChoiceness(String postid, String essencedate) {
        Map<String, List> map = new HashedMap();
        PostChoiceness postChoiceness = new PostChoiceness();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date esdate = null;
        if (essencedate != null) {
            try {
                esdate = format.parse(essencedate);
            } catch (ParseException e) {
                logger.error("时间格式转换异常");
            }
        }
        List<Post> posts = postService.queryPostChoicenesslist(esdate);//返回加精日期内有几条加精
        if (postid != null) {
            postChoiceness = postService.queryPostChoiceness(Integer.parseInt(postid));
        }
        List<Integer> lou = new ArrayList();
        for (int e = 1; e < 6; e++) {//赋值一个的集合，用于返回排序
            lou.add(e);
        }
        for (int i = 0; i < posts.size(); i++) {
            for (int j = 0; j < lou.size(); j++) {
                if (posts.get(i).getOrderid() == lou.get(j)) {
                    if (postid != null) {
                        if (lou.get(j) != postChoiceness.getOrderid()) {
                            lou.remove(j);
                        }
                    } else {
                        lou.remove(j);
                    }
                }
            }
        }
        map.put("result", lou);
        postChoiceness.setOrderids(map);
        return postChoiceness;
    }


    /**
     * 查询帖子分享列表
     *
     * @param postid
     * @param pager
     * @return
     */
    public List<SharesVo> queryPostShareList(String postid, String type, Paging<SharesVo> pager) {
        Map map = new HashedMap();
        map.put("postid", postid);
        map.put("type", type);
        return sharesService.queryPostShareList(pager, map);
    }

    /**
     * 查询名称
     *
     * @param name
     * @param pager
     * @return
     */
    public List<UserLike> likeQueryPostByNickname(String name, Paging<UserLike> pager) {
        return userService.likeQueryPostByNickname(name, pager);
    }

    /**
     * 查询圈子名称二级菜单列表
     *
     * @return
     */
    public Map<String, Object> queryListByCircleType() {
        Map<String, Object> map = new HashedMap();
        List<CircleIndexList> list = circleService.queryListByCircleCategory();//查询圈子所有的所属分类
        List<List<Circle>> circlename = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {//根据圈子的所属分类添加二级菜单
            List<Circle> circle = circleService.queryListByCircleList(list.get(i).getCategory());//用于查询圈子名称
            circlename.add(circle);
        }
        Integer num = circleService.queryCircleByNum();
        map.put("resault", circlename);
        map.put("num", num);
        return map;
    }

    /**
     * 帖子编辑数据回显
     *
     * @param postid
     * @return
     */
    public PostCompile queryPostByIdEcho(String postid) {
        return postService.queryPostByIdEcho(Integer.parseInt(postid));
    }

    /**
     * 编辑活动帖子
     *
     * @param
     * @param id
     * @param title
     * @param subtitle
     * @param activetype
     * @param activefee
     * @param userid
     * @param coverimg
     * @param begintime
     * @param endtime
     * @param isessence
     * @param orderid
     * @param postcontent
     * @param essencedate
     * @return
     */
    public Map<String, Integer> updateActivePostById(String id, String title, String subtitle, String activetype, String activefee, String userid, String coverimg,
                                                     String begintime, String endtime, String isessence, String orderid, String postcontent, String essencedate) {
        PostActiveList postActiveList = new PostActiveList();
        Map<String, Integer> map = new HashedMap();
        try {
            postActiveList.setId(Integer.parseInt(id));//帖子id
            postActiveList.setTitle(title);//帖子标题
            postActiveList.setSubtitle(subtitle);//帖子副标题
            if (activetype != null) {
                postActiveList.setActivetype(Integer.parseInt(activetype));
            }
            postActiveList.setCoverimg(coverimg);//帖子封面
            if (activefee != null) {
                postActiveList.setActivefee(Double.parseDouble(activefee));//费用
            }
            postActiveList.setUserid(Integer.parseInt(userid));
            postActiveList.setPostcontent(postcontent);
            if (isessence != null) {
                postActiveList.setIsessence(Integer.parseInt(isessence));//是否为首页精选
            }
            if (orderid != null) {
                postActiveList.setOrderid(Integer.parseInt(orderid));
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date estime = null;
            if (essencedate != null) {
                try {
                    estime = format.parse(essencedate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                postActiveList.setEssencedate(estime);
            }
            int result = postService.updateActivePostById(postActiveList);//编辑帖子
            Period period = new Period();
            Date bstime = null;
            if (begintime != null) {
                try {
                    bstime = format.parse(begintime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                period.setBegintime(bstime);
            }
            Date enstime = null;
            if (endtime != null) {
                try {
                    enstime = format.parse(endtime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                period.setEndtime(enstime);
            }
            int res = postService.updateActivePostPerById(period);
            map.put("result", result);
            map.put("res", res);
        } catch (Exception e) {
            log.error("帖子添加异常", e);
        }

        return map;
    }
    /**
     * 编辑帖子
     *
     * @param
     * @param title
     * @param subtitle
     * @param type
     * @param userid
     * @param circleid
     * @param vid
     * @param coverimg
     * @param postcontent
     * @param isessence
     * @param orderid
     * @param time
     * @return
     */
    public Map<String, Integer> updatePostById(String id, String title, String subtitle, String type,
                                               String userid, String circleid, String vid, String bannerimgurl,
                                               String coverimg, String postcontent, String isessence, String ishot, String orderid, String time) {
        PostTo post = new PostTo();
        Map<String, Integer> map = new HashedMap();
        try {
            post.setId(Integer.parseInt(id));//帖子id
            post.setTitle(title);//帖子标题
            post.setSubtitle(subtitle);//帖子副标题
            if (type != null) {
                post.setType(type);//帖子类型
            }
            if (circleid != null) {
                post.setCircleid(circleid);//圈子id
            }

            Video vide = new Video();
            if (id != null) {
                vide.setPostid(Integer.parseInt(id));
            }
            vide.setVideourl(vid);
            vide.setBannerimgurl(bannerimgurl);
            vide.setIntime(new Date());
            Integer in = videoService.updateVideoById(vide);
            post.setCoverimg(coverimg);//添加帖子封面
            post.setIsactive("0");//设置状态为帖子
            post.setPostcontent(postcontent);//帖子内容
            if (isessence != null) {
                post.setIsessence(isessence);//是否为首页精选
            }
            if (ishot != null) {
                post.setIshot(ishot);//是否为圈子精选
            }
            post.setIntime(new Date());
            if (orderid != null) {
                post.setOrderid(orderid);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date estime = null;
            if (time != null) {
                try {
                    estime = format.parse(time);
                    post.setEssencedate(estime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            post.setUserid(userid);
            int result = postService.updatePostById(post);//编辑帖子
            map.put("result", result);
        } catch (Exception e) {
            log.error("帖子添加异常", e);
        }
        return map;
    }


    /**
     * 帖子按条件查询
     *
     * @param title
     * @param circleid
     * @param postcontent
     * @param endtime
     * @param begintime
     * @param essencedate
     * @param pager
     * @return
     */
    public List<PostList> postSearch(String title, String circleid,
                                     String userid, String postcontent, String endtime,
                                     String begintime, String pai, String essencedate, Paging<PostList> pager) {
        PostSpread postSpread = new PostSpread();
        if (title != null) {
            postSpread.setTitle(title);//帖子标题
        }
        if (circleid != null) {
            postSpread.setCircleid(circleid);//圈子id
        }
        if (userid != null) {
            postSpread.setUserid(userid);//发帖人id
        }
        if (postcontent != null) {
            postSpread.setPostcontent(postcontent);//帖子内容
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date end = null;
        Date beg = null;
        Date ess = null;
        //结束时间
        if (endtime != null) {
            try {
                end = format.parse(endtime);
                postSpread.setEndtime(end);
            } catch (ParseException e) {
                log.error("结束时间转换异常");
            }
        }
        //开始时间
        if (begintime != null) {
            try {
                beg = format.parse(begintime);
                postSpread.setBegintime(beg);
            } catch (ParseException e) {
                log.error("开始时间格式转换异常");
            }
        }
        if (pai != null) {
            postSpread.setPai(pai);
        }
        //精选时间
        if (essencedate != null) {
            try {
                ess = format.parse(essencedate);
                postSpread.setEssencedate(ess);
            } catch (ParseException e) {
                log.error("精选时间格式转换异常");
            }
        }
        List<PostList> list = postService.postSearch(postSpread, pager);
        return list;
        }


    /**
     * 评论列表根据点赞人气排序
     *
     * @param postid
     * @return
     */
    public List<CommentVo> commentZanSork(String postid, Paging<CommentVo> pager) {
        return commentService.commentZanSork(Integer.parseInt(postid), pager);
    }

    /**
     * 搜索含有敏感字的评论
     *
     * @param content
     * @param words
     * @param
     * @return
     */
    public List<CommentVo> queryCommentSensitiveWords(String content, String words, String begintime, String endtime, Paging<CommentVo> pager) {
        String beg = null;
        String end = null;
        if (begintime != null && endtime != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long l = new Long(begintime);
            Long o = new Long(endtime);
            beg = format.format(l);
            end = format.format(o);
        }
        Map map = new HashedMap();
        map.put("content", content);
        map.put("words", words);
        map.put("begintime", beg);
        map.put("endtime", end);
        List<CommentVo> list = commentService.queryCommentSensitiveWords(map, pager);
        return list;
    }

    /**
     * 活动条件查询
     *
     * @param title
     * @param name
     * @param content
     * @param mintime
     * @param maxtime
     * @param statue
     * @param pager
     * @return
     */
    public List<PostList> queryAllActivePostCondition(String title, String name, String content, String mintime, String maxtime, String statue, Paging<PostList> pager) {
        Map<String, Object> map = new HashedMap();
        if (title != null) {
            map.put("title", title);
        }
        if (name != null) {
            map.put("name", name);
        }
        if (content != null) {
            map.put("content", content);
        }

        if (statue != null) {
            map.put("statue", statue);
        }
        Date isessencetime = null;//开始时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (mintime != null) {
            try {
                isessencetime = format.parse(mintime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put("mintime", isessencetime);
        Date max = null;//最大时间
        if (maxtime != null) {
            try {
                max = format.parse(maxtime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        map.put("maxtime", max);

        map.put("statue", statue);
        return postService.queryAllActivePostCondition(map, pager);
    }


    /**
     * 查询商品列表
     *
     * @param pager
     * @return
     */
    public List<GoodsVo> queryPostByGoodsList(Paging<GoodsVo> pager) {
        List<GoodsVo> goodsVos = goodsService.queryPostByGoodsList(pager);
        return goodsVos;
    }

    /**
     * 用于条件查询商品列表（帖子使用）
     *
     * @param name
     * @param brandname
     * @param protype
     * @param pager
     * @return
     */
    public List<GoodsVo> findAllQueryLikeGoods(String name, String brandname, String protype, Paging<GoodsVo> pager) {
        Map map = new HashedMap();
        map.put("name", name);
        map.put("brandname", brandname);
        map.put("protype", protype);
        return goodsService.findAllQueryLikeGoods(map, pager);
    }

    public List<Post> findAllMyCollectPostList(Paging<Post> paging, int userid) {
        Map map = new HashedMap();
        map.put("userid", userid);
        return postService.findAllMyCollectPost(paging, map);
    }

    /**
     * 获取我的申请vip的准备条件的统计
     *
     * @return
     */
    public Map getMyVipApplyStatistics() {
        Map map = new HashedMap();
        map.put("userid", ShiroUtil.getAppUserID());
        Map result = new HashedMap();

        List<Post> postList = postService.queryMyPostList(map);
        if (ListUtil.isNotEmpty(postList)) {
            //1 发帖次数大于50
            result.put("publist_count_flag", postList.size() >= 50);

            //2 首页精选次数达到10次, 3 被赞次数达到500次， 4 分享总数达到500次
            int selectedCount = 0, supportCount = 0, shareCount = 0;
            for (Post post : postList) {

                if (null != post.getIsessence() && post.getIsessence() == 1) {
                    selectedCount++;
                }
                supportCount += null == post.getZansum() ? 0 : post.getZansum();
                shareCount += null == post.getForwardsum() ? 0 : post.getForwardsum();
            }
            result.put("selected_count_flag", selectedCount >= 10);
            result.put("support_count_flag", supportCount >= 500);
            result.put("share_count_flag", shareCount >= 500);

        } else {
            result.put("publist_count_flag", false);
            result.put("selected_count_flag", false);
            result.put("support_count_flag", false);
            result.put("share_count_flag", false);
        }
        return result;
    }

    /**
     * 根机id查询出活动
     *
     * @param id
     * @return
     */
    public PostActiveList queryActiveById(Integer id) {
        return postService.queryActiveById(id);
    }
}
