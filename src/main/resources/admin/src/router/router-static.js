import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import UpdatePassword from '@/views/update-password'
import pay from '@/views/pay'
import register from '@/views/register'
import center from '@/views/center'
    import forumPost from '@/views/modules/forum-post/list'
    import zhongshimeishi from '@/views/modules/zhongshimeishi/list'
    import waiguomeishi from '@/views/modules/waiguomeishi/list'
    import yonghu from '@/views/modules/user/list'
    import discusszhongshimeishi from '@/views/modules/discusszhongshimeishi/list'
    import discusswaiguomeishi from '@/views/modules/discusswaiguomeishi/list'
    import config from '@/views/modules/config/list'


//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/updatePassword',
      name: '修改密码',
      component: UpdatePassword,
      meta: {icon:'', title:'updatePassword'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }
      ,{
	path: '/forum-post',
        name: '美食论坛',
        component: forumPost
      }
      ,{
	path: '/forum_post',
        name: '美食论坛',
        component: forumPost
      }
      ,{
	path: '/zhongshimeishi',
        name: '中式美食',
        component: zhongshimeishi
      }
      ,{
	path: '/waiguomeishi',
        name: '外国美食',
        component: waiguomeishi
      }
      ,{
	path: '/user',
        name: '用户',
        component: yonghu
      }
      ,{
	path: '/discusszhongshimeishi',
        name: '中式美食评论',
        component: discusszhongshimeishi
      }
      ,{
	path: '/discusswaiguomeishi',
        name: '外国美食评论',
        component: discusswaiguomeishi
      }
      ,{
	path: '/config',
        name: '轮播图管理',
        component: config
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/register',
    name: 'register',
    component: register,
    meta: {icon:'', title:'register'}
  },
  {
    path: '/',
    name: '首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

// 解析 storage 存的值（login 时用 JSON.stringify 写入的）
function parseStorage(key) {
  const raw = localStorage.getItem(key);
  if (raw == null || raw === '') return '';
  try {
    const parsed = JSON.parse(raw);
    return parsed != null ? String(parsed) : '';
  } catch (e) {
    return raw;
  }
}

// 路由守卫：确保只有管理员可以访问后台管理页面
router.beforeEach((to, from, next) => {
  // 登录页面和注册页面允许访问
  if (to.path === '/login' || to.path === '/register') {
    next();
    return;
  }
  
  // 检查是否有Token（storage 用 JSON.stringify 存的，需解析）
  const token = parseStorage('Token');
  if (!token) {
    next('/login');
    return;
  }
  
  // 检查用户角色（同上，需按解析后的值比较）
  const role = parseStorage('role');
  const sessionTable = parseStorage('sessionTable');
  
  if (sessionTable !== 'admin' || role !== '管理员') {
    localStorage.clear();
    next('/login');
    return;
  }
  
  next();
});

export default router;
