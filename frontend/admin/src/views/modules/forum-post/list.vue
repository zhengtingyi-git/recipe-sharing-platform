<template>
  <div class="main-content">
    <div v-if="showFlag">
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt" :style="{justifyContent:contents.searchBoxPosition=='1'?'flex-start':contents.searchBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item :label="contents.inputTitle == 1 ? '标题' : ''">
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 1" prefix-icon="el-icon-search" v-model="searchForm.title" placeholder="标题" clearable></el-input>
            <el-input v-if="contents.inputIcon == 1 && contents.inputIconPosition == 2" suffix-icon="el-icon-search" v-model="searchForm.title" placeholder="标题" clearable></el-input>
            <el-input v-if="contents.inputIcon == 0" v-model="searchForm.title" placeholder="标题" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 1" icon="el-icon-search" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}</el-button>
            <el-button v-if="contents.searchBtnIcon == 1 && contents.searchBtnIconPosition == 2" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}<i class="el-icon-search el-icon--right"/></el-button>
            <el-button v-if="contents.searchBtnIcon == 0" type="success" @click="search()">{{ contents.searchBtnFont == 1?'查询':'' }}</el-button>
          </el-form-item>
        </el-row>
        <el-row class="ad" :style="{justifyContent:contents.btnAdAllBoxPosition=='1'?'flex-start':contents.btnAdAllBoxPosition=='2'?'center':'flex-end'}">
          <el-form-item>
            <el-button v-if="isAuth('forum_post','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()">{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button v-if="isAuth('forum_post','新增') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2" type="success" @click="addOrUpdateHandler()">{{ contents.btnAdAllFont == 1?'新增':'' }}<i class="el-icon-plus el-icon--right" /></el-button>
            <el-button v-if="isAuth('forum_post','新增') && contents.btnAdAllIcon == 0" type="success" @click="addOrUpdateHandler()">{{ contents.btnAdAllFont == 1?'新增':'' }}</el-button>
            <el-button v-if="isAuth('forum_post','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 1 && contents.tableSelection" :disabled="dataListSelections.length <= 0" type="danger" icon="el-icon-delete" @click="deleteHandler()">{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>
            <el-button v-if="isAuth('forum_post','删除') && contents.btnAdAllIcon == 1 && contents.btnAdAllIconPosition == 2 && contents.tableSelection" :disabled="dataListSelections.length <= 0" type="danger" @click="deleteHandler()">{{ contents.btnAdAllFont == 1?'删除':'' }}<i class="el-icon-delete el-icon--right" /></el-button>
            <el-button v-if="isAuth('forum_post','删除') && contents.btnAdAllIcon == 0 && contents.tableSelection" :disabled="dataListSelections.length <= 0" type="danger" @click="deleteHandler()">{{ contents.btnAdAllFont == 1?'删除':'' }}</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <div class="table-content">
        <el-table class="tables" :size="contents.tableSize" :show-header="contents.tableShowHeader" :header-row-style="headerRowStyle" :header-cell-style="headerCellStyle" :border="contents.tableBorder" :fit="contents.tableFit" :stripe="contents.tableStripe" :style="{width: '100%',fontSize:contents.tableContentFontSize,color:contents.tableContentFontColor}" v-if="isAuth('forum_post','查看')" :data="dataList" v-loading="dataListLoading" @selection-change="selectionChangeHandler">
          <el-table-column v-if="contents.tableSelection" type="selection" :header-align="contents.tableAlign" align="center" width="50"></el-table-column>
          <el-table-column label="索引" :align="contents.tableAlign" v-if="contents.tableIndex" type="index" width="50" />
          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="title" :header-align="contents.tableAlign" label="标题">
            <template slot-scope="scope">{{scope.row.title}}</template>
          </el-table-column>
          <el-table-column :sortable="contents.tableSortable" :align="contents.tableAlign" prop="picture" :header-align="contents.tableAlign" width="200" label="图片">
            <template slot-scope="scope">
              <div v-if="scope.row.picture"><img :src="$base.url+scope.row.picture.split(',')[0]" width="100" height="100"></div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>
          <el-table-column width="300" :align="contents.tableAlign" :header-align="contents.tableAlign" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('forum_post','查看')" type="success" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('forum_post','修改')" type="primary" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('forum_post','删除')" type="danger" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination clsss="pages" :layout="layouts" @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex" :page-sizes="[10, 20, 50, 100]" :page-size="Number(contents.pageEachNum)" :total="totalPage" :small="contents.pageStyle" class="pagination-content" :background="contents.pageBtnBG" :style="{textAlign:contents.pagePosition==1?'left':contents.pagePosition==2?'center':'right'}"></el-pagination>
      </div>
    </div>
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: { key: "" },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag: false,
      contents: { pageEachNum: 10, tableSelection: true, tableIndex: true, tableAlign: "center", tableSortable: true, tableSize: "small", tableShowHeader: true, tableBorder: true, tableFit: true, tableStripe: true, tableContentFontSize: "14px", tableContentFontColor: "#333", tableBtnFont: "1", searchBtnFont: "1", searchBtnIcon: "1", searchBtnIconPosition: "1", inputIcon: "1", inputIconPosition: "1", inputTitle: "1", searchBoxPosition: "1", btnAdAllIcon: "1", btnAdAllIconPosition: "1", btnAdAllBoxPosition: "1", pagePosition: "1", pageBtnBG: true, pageStyle: false, pageTotal: true, pageSizes: true, pagePrevNext: true, pagePager: true, pageJumper: true },
      layouts: ''
    };
  },
  created() {
    this.getDataList();
    this.contentPageStyleChange();
  },
  components: { AddOrUpdate },
  methods: {
    headerRowStyle(){ return {} },
    headerCellStyle(){ return {} },
    contentPageStyleChange(){
      let arr = [];
      if(this.contents.pageTotal) arr.push('total');
      if(this.contents.pageSizes) arr.push('sizes');
      if(this.contents.pagePrevNext){ arr.push('prev'); if(this.contents.pagePager) arr.push('pager'); arr.push('next'); }
      if(this.contents.pageJumper) arr.push('jumper');
      this.layouts = arr.join();
    },
    search() { this.pageIndex = 1; this.getDataList(); },
    getDataList() {
      this.dataListLoading = true;
      let params = { page: this.pageIndex, limit: this.pageSize, sort: 'id' };
      if (this.searchForm.title) params.title = '%' + this.searchForm.title + '%';
      this.$http({ url: "forum-post/page", method: "get", params }).then(({ data }) => {
        if (data && data.code === 0) { this.dataList = data.data.list; this.totalPage = data.data.total; }
        else { this.dataList = []; this.totalPage = 0; }
        this.dataListLoading = false;
      });
    },
    sizeChangeHandle(val) { this.pageSize = val; this.pageIndex = 1; this.getDataList(); },
    currentChangeHandle(val) { this.pageIndex = val; this.getDataList(); },
    selectionChangeHandler(val) { this.dataListSelections = val; },
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.$nextTick(() => { this.$refs.addOrUpdate.init(id, type!='info'?'else':type); });
    },
    deleteHandler(id) {
      const ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", { confirmButtonText: "确定", cancelButtonText: "取消", type: "warning" })
      .then(() => this.$http({ url: "forum-post/delete", method: "post", data: ids }))
      .then(({ data }) => { if (data && data.code === 0) this.search(); else this.$message.error(data.msg); });
    },
  }
};
</script>
