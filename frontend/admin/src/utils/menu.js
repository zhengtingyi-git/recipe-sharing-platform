const menu = {
    list() {
        return [
            {
                backMenu: [
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-discover",
                                buttons: ["新增", "查看", "修改", "删除"],
                                menu: "用户",
                                menuJump: "列表",
                                tableName: "user"
                            }
                        ],
                        menu: "用户管理"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-phone",
                                buttons: ["新增", "查看", "修改", "删除", "审核", "查看评论"],
                                menu: "外国美食",
                                menuJump: "列表",
                                tableName: "foreign_recipe"
                            }
                        ],
                        menu: "外国美食管理"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-clothes",
                                buttons: ["新增", "查看", "修改", "删除", "审核", "查看评论"],
                                menu: "中式美食",
                                menuJump: "列表",
                                tableName: "chinese_recipe"
                            }
                        ],
                        menu: "中式美食管理"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-form",
                                buttons: ["新增", "查看", "修改", "删除"],
                                menu: "美食论坛",
                                tableName: "forum_post",
                                menuJump: "列表",
                            }
                        ],
                        menu: "美食论坛管理"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-form",
                                buttons: ["新增", "查看", "修改", "删除"],
                                menu: "轮播图管理",
                                tableName: "config"
                            }
                        ],
                        menu: "系统管理"
                    }
                ],
                frontMenu: [
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-paint",
                                buttons: ["查看", "查看评论"],
                                menu: "外国美食列表",
                                menuJump: "列表",
                                tableName: "foreign_recipe"
                            }
                        ],
                        menu: "外国美食模块"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-list",
                                buttons: ["查看", "查看评论"],
                                menu: "中式美食列表",
                                menuJump: "列表",
                                tableName: "chinese_recipe"
                            }
                        ],
                        menu: "中式美食模块"
                    },
                    {
                        child: [
                            {
                                appFrontIcon: "cuIcon-brand",
                                buttons: ["查看", "查看评论"],
                                menu: "每日推荐列表",
                                menuJump: "列表",
                                tableName: "daily-recommendations"
                            }
                        ],
                        menu: "每日推荐模块"
                    }
                ],
                hasBackLogin: "是",
                hasBackRegister: "否",
                hasFrontLogin: "否",
                hasFrontRegister: "否",
                roleName: "管理员",
                tableName: "admin"
            }
        ];
    }
};

export default menu;

