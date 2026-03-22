const base = {
    get() {
        return {
            url : "http://localhost:8080/recipe-sharing-platform/",
            name: "recipe-sharing-platform",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/recipe-sharing-platform/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "小食堂记"
        } 
    }
}
export default base
