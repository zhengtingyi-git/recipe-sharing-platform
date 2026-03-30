package com.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.annotation.IgnoreAuth;

import com.entity.DailyRecommendationEntity;
import com.service.ChineseRecipeService;
import com.service.ForeignRecipeService;
import com.utils.PageUtils;
import com.utils.R;
import com.service.UserInteractionsService;
import com.service.TokenService;
import com.entity.UserInteractionsEntity;
import com.entity.TokenEntity;
import com.entity.ChineseRecipeEntity;
import com.entity.ForeignRecipeEntity;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import com.utils.RecipeAuditStatus;

/**
 * 姣忔棩鎺ㄨ崘锛氬睍绀哄叏閮ㄤ腑寮忕編椋?澶栧浗缇庨锛堝鏍搁€氳繃锛夛紝鏀寔鎸夋渶鏂?鏈€澶氱偣璧?鏈€澶氭敹钘?鎺ㄨ崘鎺掑簭
 */
@RestController
@RequestMapping("/daily-recommendations")
public class DailyRecommendationController {
    private static final String SORT_RECOMMEND = "recommend";
    private static final String SORT_ADDTIME = "addtime";
    private static final String SORT_THUMBSUP = "thumbsupnum";
    private static final String SORT_FAVORITE = "userInteractionsNum";

    @Autowired
    private UserInteractionsService userInteractionsService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ChineseRecipeService chinese_recipeService;

    @Autowired
    private ForeignRecipeService foreign_recipeService;

    /**
     * 前端列表：展示全部中式与外国美食（仅 audit_status=1 或历史「是」），支持排序：addtime、thumbsupnum、userInteractionsNum。
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, DailyRecommendationEntity remencaipin,
                  HttpServletRequest request) {
        class DishInfo {
            Long id;
            String name;
            String image;
            String type;
            String cuisine;
            Integer thumbsupnum;
            Integer userInteractionsNum;
            Date addtime;

            DishInfo(Long id, String name, String image, String type, String cuisine, Integer thumbsupnum, Integer userInteractionsNum, Date addtime) {
                this.id = id;
                this.name = name;
                this.image = image;
                this.type = type;
                this.cuisine = cuisine;
                this.thumbsupnum = thumbsupnum != null ? thumbsupnum : 0;
                this.userInteractionsNum = userInteractionsNum != null ? userInteractionsNum : 0;
                this.addtime = addtime;
            }
        }

        List<DishInfo> allDishes = new ArrayList<>();
        String sort = params.get("sort") != null ? params.get("sort").toString() : SORT_ADDTIME;
        String biaotiParam = params.get("biaoti") != null ? params.get("biaoti").toString().trim() : null;
        final String keyword = (biaotiParam != null && !biaotiParam.isEmpty()) ? biaotiParam.replace("%", "").trim() : null;
        String searchTypeParam = params.get("searchType") != null ? params.get("searchType").toString().trim() : "all";
        if (searchTypeParam.isEmpty()) searchTypeParam = "all";
        final boolean searchAllFields = "all".equalsIgnoreCase(searchTypeParam);

        EntityWrapper<ChineseRecipeEntity> zhongshiEw = new EntityWrapper<>();
        zhongshiEw.in("audit_status", Arrays.asList(RecipeAuditStatus.APPROVED, "是"));
        zhongshiEw.eq("source_type", "chinese_recipe");
        List<ChineseRecipeEntity> zhongshiList = chinese_recipeService.selectList(zhongshiEw);
        Map<Long, Integer> zhongshiUserInteractionsMap = new HashMap<>();
        Map<Long, Integer> zhongshiThumbsMap = new HashMap<>();
        if (!zhongshiList.isEmpty()) {
            List<Long> zhongshiIds = zhongshiList.stream().map(ChineseRecipeEntity::getId).collect(Collectors.toList());
            EntityWrapper<UserInteractionsEntity> userInteractionsWrapper1 = new EntityWrapper<>();
            userInteractionsWrapper1.in("resource_id", zhongshiIds);
            userInteractionsWrapper1.eq("interaction_type", "1");
            List<UserInteractionsEntity> userInteractionsList = userInteractionsService.selectList(userInteractionsWrapper1);
            for (UserInteractionsEntity userInteraction : userInteractionsList) {
                zhongshiUserInteractionsMap.put(userInteraction.getResourceId(), zhongshiUserInteractionsMap.getOrDefault(userInteraction.getResourceId(), 0) + 1);
            }
            EntityWrapper<UserInteractionsEntity> userInteractionsWrapper21 = new EntityWrapper<>();
            userInteractionsWrapper21.in("resource_id", zhongshiIds);
            userInteractionsWrapper21.in("interaction_type", Arrays.asList("0", "21"));
            List<UserInteractionsEntity> thumbsList = userInteractionsService.selectList(userInteractionsWrapper21);
            for (UserInteractionsEntity thumbs : thumbsList) {
                zhongshiThumbsMap.put(thumbs.getResourceId(), zhongshiThumbsMap.getOrDefault(thumbs.getResourceId(), 0) + 1);
            }
        }
        for (ChineseRecipeEntity dish : zhongshiList) {
            if (keyword != null && !keyword.isEmpty()) {
                boolean match;
                if (searchAllFields) {
                    match = (dish.getCaipinmingcheng() != null && dish.getCaipinmingcheng().contains(keyword))
                            || (dish.getCailiao() != null && dish.getCailiao().contains(keyword))
                            || (dish.getCaixi() != null && dish.getCaixi().contains(keyword));
                } else {
                    String fieldVal = "caipinmingcheng".equals(searchTypeParam) ? dish.getCaipinmingcheng()
                            : "cailiao".equals(searchTypeParam) ? dish.getCailiao()
                            : "caixi".equals(searchTypeParam) ? dish.getCaixi() : dish.getCaipinmingcheng();
                    match = fieldVal != null && fieldVal.contains(keyword);
                }
                if (!match) continue;
            }
            allDishes.add(new DishInfo(
                    dish.getId(),
                    dish.getCaipinmingcheng(),
                    dish.getTupian(),
                    "chinese_recipe",
                    dish.getCaixi(),
                    zhongshiThumbsMap.getOrDefault(dish.getId(), 0),
                    zhongshiUserInteractionsMap.getOrDefault(dish.getId(), 0),
                    dish.getCreatedAt()
            ));
        }

        EntityWrapper<ForeignRecipeEntity> waiguoEw = new EntityWrapper<>();
        waiguoEw.in("audit_status", Arrays.asList(RecipeAuditStatus.APPROVED, "是"));
        waiguoEw.eq("source_type", "foreign_recipe");
        List<ForeignRecipeEntity> waiguoList = foreign_recipeService.selectList(waiguoEw);
        Map<Long, Integer> waiguoUserInteractionsMap = new HashMap<>();
        Map<Long, Integer> waiguoThumbsMap = new HashMap<>();
        if (!waiguoList.isEmpty()) {
            List<Long> waiguoIds = waiguoList.stream().map(ForeignRecipeEntity::getId).collect(Collectors.toList());
            EntityWrapper<UserInteractionsEntity> userInteractionsWrapper1 = new EntityWrapper<>();
            userInteractionsWrapper1.in("resource_id", waiguoIds);
            userInteractionsWrapper1.eq("interaction_type", "1");
            List<UserInteractionsEntity> userInteractionsList = userInteractionsService.selectList(userInteractionsWrapper1);
            for (UserInteractionsEntity userInteraction : userInteractionsList) {
                waiguoUserInteractionsMap.put(userInteraction.getResourceId(), waiguoUserInteractionsMap.getOrDefault(userInteraction.getResourceId(), 0) + 1);
            }
            EntityWrapper<UserInteractionsEntity> userInteractionsWrapper21 = new EntityWrapper<>();
            userInteractionsWrapper21.in("resource_id", waiguoIds);
            userInteractionsWrapper21.in("interaction_type", Arrays.asList("0", "21"));
            List<UserInteractionsEntity> thumbsList = userInteractionsService.selectList(userInteractionsWrapper21);
            for (UserInteractionsEntity thumbs : thumbsList) {
                waiguoThumbsMap.put(thumbs.getResourceId(), waiguoThumbsMap.getOrDefault(thumbs.getResourceId(), 0) + 1);
            }
        }
        for (ForeignRecipeEntity dish : waiguoList) {
            if (keyword != null && !keyword.isEmpty()) {
                boolean match;
                if (searchAllFields) {
                    match = (dish.getCaipinmingcheng() != null && dish.getCaipinmingcheng().contains(keyword))
                            || (dish.getCailiao() != null && dish.getCailiao().contains(keyword))
                            || (dish.getCaixi() != null && dish.getCaixi().contains(keyword));
                } else {
                    String fieldVal = "caipinmingcheng".equals(searchTypeParam) ? dish.getCaipinmingcheng()
                            : "cailiao".equals(searchTypeParam) ? dish.getCailiao()
                            : "caixi".equals(searchTypeParam) ? dish.getCaixi() : dish.getCaipinmingcheng();
                    match = fieldVal != null && fieldVal.contains(keyword);
                }
                if (!match) continue;
            }
            allDishes.add(new DishInfo(
                    dish.getId(),
                    dish.getCaipinmingcheng(),
                    dish.getTupian(),
                    "foreign_recipe",
                    dish.getCaixi(),
                    waiguoThumbsMap.getOrDefault(dish.getId(), 0),
                    waiguoUserInteractionsMap.getOrDefault(dish.getId(), 0),
                    dish.getCreatedAt()
            ));
        }

        if (SORT_THUMBSUP.equals(sort)) {
            allDishes.sort(Comparator.comparing((DishInfo d) -> d.thumbsupnum).reversed());
        } else if (SORT_FAVORITE.equals(sort)) {
            allDishes.sort(Comparator.comparing((DishInfo d) -> d.userInteractionsNum).reversed());
        } else if (SORT_RECOMMEND.equals(sort)) {
            // 推荐排序：已互动优先、偏好菜系优先、其余按最新
            Set<String> userActionKeys = new HashSet<>();
            Set<String> preferredCuisines = new HashSet<>();
            Map<String, DishInfo> dishMap = allDishes.stream()
                    .collect(Collectors.toMap(d -> d.type + "_" + d.id, d -> d, (a, b) -> a));

            String token = request.getHeader("Token");
            if (token != null && !token.trim().isEmpty()) {
                TokenEntity te = tokenService.getTokenEntity(token.trim());
                if (te != null && te.getUserId() != null) {
                    EntityWrapper<UserInteractionsEntity> userEw = new EntityWrapper<>();
                    userEw.eq("user_id", te.getUserId());
                    userEw.in(true, "interaction_type", Arrays.asList("1", "0", "21"));
                    for (UserInteractionsEntity s : userInteractionsService.selectList(userEw)) {
                        String resolvedType = resolveRecipeType(s.getResourceId(), dishMap);
                        if (resolvedType == null) {
                            continue;
                        }
                        String key = resolvedType + "_" + s.getResourceId();
                        userActionKeys.add(key);
                        DishInfo di = dishMap.get(key);
                        if (di != null && di.cuisine != null && !di.cuisine.trim().isEmpty()) {
                            preferredCuisines.add(di.cuisine.trim());
                        }
                    }
                }
            }

            final Set<String> actionKeys = userActionKeys;
            final Set<String> recommendCuisines = preferredCuisines;
            allDishes.sort(Comparator
                    .comparing((DishInfo d) -> actionKeys.contains(d.type + "_" + d.id) ? 0 : 1)
                    .thenComparing((DishInfo d) -> {
                        if (actionKeys.contains(d.type + "_" + d.id)) return 0;
                        if (recommendCuisines.isEmpty()) return 1;
                        String c = d.cuisine != null ? d.cuisine.trim() : "";
                        return recommendCuisines.contains(c) ? 0 : 1;
                    })
                    .thenComparing((DishInfo d) -> d.addtime != null ? d.addtime.getTime() : 0L, Comparator.reverseOrder()));
        } else {
            allDishes.sort(Comparator.comparing((DishInfo d) -> d.addtime != null ? d.addtime.getTime() : 0L).reversed());
        }

        int pageNum = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int limitNum = params.get("limit") != null ? Integer.parseInt(params.get("limit").toString()) : 12;
        int from = (pageNum - 1) * limitNum;
        int to = Math.min(from + limitNum, allDishes.size());
        List<DishInfo> pageDishes = from < allDishes.size() ? allDishes.subList(from, to) : new ArrayList<>();

        List<DailyRecommendationEntity> resultList = new ArrayList<>();
        for (DishInfo dishInfo : pageDishes) {
            DailyRecommendationEntity e = new DailyRecommendationEntity();
            e.setId(dishInfo.id);
            e.setBiaoti(dishInfo.name);
			e.setCaixi(dishInfo.cuisine);
            e.setFengmian(dishInfo.image);
            e.setThumbsupnum(dishInfo.thumbsupnum);
            e.setSourceType(dishInfo.type);
            resultList.add(e);
        }

        PageUtils page = new PageUtils(resultList, allDishes.size(), limitNum, pageNum);
        return R.ok().put("data", page);
    }

    private String resolveRecipeType(Long refId, Map<String, ?> dishMap) {
        if (refId == null) {
            return null;
        }
        if (dishMap.containsKey("chinese_recipe_" + refId)) {
            return "chinese_recipe";
        }
        if (dishMap.containsKey("foreign_recipe_" + refId)) {
            return "foreign_recipe";
        }
        return null;
    }
}


