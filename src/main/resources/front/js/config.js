
var projectName = '小食堂记';
/**
 * 轮播图配置
 */
var swiper = {
	// 设定轮播容器宽度，支持像素和百分比
	width: '100%',
	height: '300px',
	// hover（悬停显示）
	// always（始终显示）
	// none（始终不显示）
	arrow: 'none',
	// default（左右切换）
	// updown（上下切换）
	// fade（渐隐渐显切换）
	anim: 'default',
	// 自动切换的时间间隔
	// 默认3000
	interval: 2000,
	// 指示器位置
	// inside（容器内部）
	// outside（容器外部）
	// none（不显示）
	indicator: 'outside'
}

/**
 * 个人中心菜单
 */
var centerMenu = [{
	name: '个人中心',
	url: '../' + localStorage.getItem('userTable') + '/center.html'
}, {
	name: '我的发布',
	url: '../my-publish/list.html'
}, {
	name: '我的点赞',
	url: '../storeup/thumbsup.html'
}, {
	name: '我的收藏',
	url: '../storeup/list.html'
}, {
	name: '我的关注',
	url: '../user-follows/my-follow.html'
}, {
	name: '我的粉丝',
	url: '../user-follows/my-fans.html'
}, {
	name: '我的消息',
	url: '../notify/list.html'
}]


var indexNav = [

{
	name: '每日推荐',
	url: './pages/remencaipin/list.html'
}, 
{
	name: '外国美食',
	url: './pages/waiguomeishi/list.html'
}, 
{
	name: '中式美食',
	url: './pages/zhongshimeishi/list.html'
}, 
{
	name: '美食论坛',
	url: './pages/forum-post/list.html'
},
{
	name: '消息中心',
	url: './pages/notify/list.html'
}
]

var adminurl =  "http://localhost:8080/recipe-sharing-platform/admin/dist/index.html";

var cartFlag = false

var chatFlag = false




var menu = [{"backMenu":[{"child":[{"appFrontIcon":"cuIcon-discover","buttons":["新增","查看","修改","删除"],"menu":"用户","menuJump":"列表","tableName":"user"}],"menu":"用户管理"},{"child":[{"appFrontIcon":"cuIcon-phone","buttons":["新增","查看","修改","删除","审核","查看评论"],"menu":"外国美食","menuJump":"列表","tableName":"waiguomeishi"}],"menu":"外国美食管理"},{"child":[{"appFrontIcon":"cuIcon-clothes","buttons":["新增","查看","修改","删除","审核","查看评论"],"menu":"中式美食","menuJump":"列表","tableName":"zhongshimeishi"}],"menu":"中式美食管理"},{"child":[{"appFrontIcon":"cuIcon-favor","buttons":["新增","查看","修改","删除"],"menu":"我的收藏管理","tableName":"storeup"}],"menu":"我的收藏管理"},{"child":[{"appFrontIcon":"cuIcon-form","buttons":["新增","查看","修改","删除"],"menu":"轮播图管理","tableName":"config"},{"appFrontIcon":"cuIcon-form","buttons":["新增","查看","修改","删除"],"menu":"美食论坛","tableName":"forum_post"}],"menu":"系统管理"}],"frontMenu":[{"child":[{"appFrontIcon":"cuIcon-paint","buttons":["新增","查看","查看评论"],"menu":"外国美食列表","menuJump":"列表","tableName":"waiguomeishi"}],"menu":"外国美食模块"},{"child":[{"appFrontIcon":"cuIcon-list","buttons":["新增","查看","查看评论"],"menu":"中式美食列表","menuJump":"列表","tableName":"zhongshimeishi"}],"menu":"中式美食模块"},{"child":[{"appFrontIcon":"cuIcon-brand","buttons":["查看","查看评论"],"menu":"每日推荐列表","menuJump":"列表","tableName":"remencaipin"}],"menu":"每日推荐模块"}],"hasBackLogin":"是","hasBackRegister":"否","hasFrontLogin":"否","hasFrontRegister":"否","roleName":"管理员","tableName":"admin"},{"backMenu":[{"child":[{"appFrontIcon":"cuIcon-phone","buttons":["新增","查看","查看评论","删除"],"menu":"外国美食","menuJump":"列表","tableName":"waiguomeishi"}],"menu":"外国美食管理"},{"child":[{"appFrontIcon":"cuIcon-clothes","buttons":["新增","查看","删除","查看评论"],"menu":"中式美食","menuJump":"列表","tableName":"zhongshimeishi"}],"menu":"中式美食管理"},{"child":[{"appFrontIcon":"cuIcon-favor","buttons":["查看","删除"],"menu":"我的收藏管理","tableName":"storeup"}],"menu":"我的收藏管理"}],"frontMenu":[{"child":[{"appFrontIcon":"cuIcon-paint","buttons":["新增","查看","查看评论"],"menu":"外国美食列表","menuJump":"列表","tableName":"waiguomeishi"}],"menu":"外国美食模块"},{"child":[{"appFrontIcon":"cuIcon-list","buttons":["新增","查看","查看评论"],"menu":"中式美食列表","menuJump":"列表","tableName":"zhongshimeishi"}],"menu":"中式美食模块"},{"child":[{"appFrontIcon":"cuIcon-brand","buttons":["查看","查看评论"],"menu":"每日推荐列表","menuJump":"列表","tableName":"remencaipin"}],"menu":"每日推荐模块"}],"hasBackLogin":"是","hasBackRegister":"是","hasFrontLogin":"是","hasFrontRegister":"是","roleName":"用户","tableName":"user"}]


var isAuth = function (tableName,key) {
    let role = localStorage.getItem("userTable");
    let menus = menu;
    for(let i=0;i<menus.length;i++){
        if(menus[i].tableName==role){
            for(let j=0;j<menus[i].backMenu.length;j++){
                for(let k=0;k<menus[i].backMenu[j].child.length;k++){
                    if(tableName==menus[i].backMenu[j].child[k].tableName){
                        let buttons = menus[i].backMenu[j].child[k].buttons.join(',');
                        return buttons.indexOf(key) !== -1 || false
                    }
                }
            }
        }
    }
    return false;
}

var isFrontAuth = function (tableName,key) {
    let role = localStorage.getItem("userTable");
    let menus = menu;
    for(let i=0;i<menus.length;i++){
        if(menus[i].tableName==role){
            for(let j=0;j<menus[i].frontMenu.length;j++){
                for(let k=0;k<menus[i].frontMenu[j].child.length;k++){
                    if(tableName==menus[i].frontMenu[j].child[k].tableName){
                        let buttons = menus[i].frontMenu[j].child[k].buttons.join(',');
                        return buttons.indexOf(key) !== -1 || false
                    }
                }
            }
        }
    }
    return false;
}
