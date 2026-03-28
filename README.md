# 食谱分享平台（recipe-sharing）

Spring Boot 后端 + Vue 管理端 + 用户前台静态页。  
**前端源码只在 `frontend/`，后端只在 `backend/`，不在 `backend` 里再放一份 admin/front。**

## 目录结构

| 路径 | 说明 |
|------|------|
| `backend/` | 后端（Maven + Spring Boot） |
| `frontend/admin/` | 管理端 Vue 源码 |
| `frontend/front/` | 用户前台静态页源码 |
| `backend/src/main/resources/static/upload/` | 上传文件目录（运行时生成/业务使用） |
| `db/` | 数据库脚本（如有） |

## 环境要求

- JDK 8
- Maven（或使用 `backend/mvnw`）
- MySQL，库名与账号见 `backend/src/main/resources/application.yml`
- Node.js + npm（跑管理端 `npm run serve` 时需要）

## 1. 启动后端（端口 8080）

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

- 端口：`8080`
- 上下文路径：`/recipe-sharing-platform`
- 接口基地址：`http://localhost:8080/recipe-sharing-platform`

## 2. 启动管理端（端口 8081）

另开终端：

```powershell
cd frontend/admin
npm install
npm run serve
```

默认 **8081**，`vue.config.js` 会把 `/recipe-sharing-platform` 代理到本机 8080，**须先启动后端**。

浏览器打开：**http://localhost:8081**

## 3. 用户前台（源码在 frontend/front）

不在后端 `static` 里放副本时，请在本机起一个静态服务，例如：

```powershell
cd frontend/front
npx http-server -p 8082
```

浏览器打开：**http://localhost:8082/index.html**

（端口可改成你喜欢的；前台里的接口地址需指向上面的后端 8080。）

## 生产部署说明

管理端：`cd frontend/admin && npm run build`，将 `dist` 部署到 Nginx 或 CDN。  
用户前台：将 `frontend/front` 整目录部署到 Nginx 静态站点。  
后端：单独部署 `backend` 打成的 jar/war。  
**不必**再把整站拷贝进 `backend/src/main/resources/static/admin|front`，除非你想做成单端口一体化部署。

## IntelliJ IDEA

- 建议用 **Open** 打开 `backend` 目录。
- Spring Boot 运行配置里 **Working directory** 设为 `backend` 的绝对路径。

## 常见问题

- **数据源未配置**：确认从 `backend` 启动，且能读到 `application.yml`。
- **管理端接口 404**：先确认后端已启动，且代理前缀为 `/recipe-sharing-platform`。
