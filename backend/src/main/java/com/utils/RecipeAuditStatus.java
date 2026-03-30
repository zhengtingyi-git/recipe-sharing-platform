package com.utils;

/**
 * 菜品审核：0 待审核、1 通过、2 不通过。兼容历史库中的「是」「否」。
 */
public final class RecipeAuditStatus {

    public static final String PENDING = "0";
    public static final String APPROVED = "1";
    public static final String REJECTED = "2";

    private RecipeAuditStatus() {
    }

    /** 前台公开列表/他人可见详情 */
    public static boolean isApproved(String s) {
        return APPROVED.equals(s) || "是".equals(s);
    }
}
