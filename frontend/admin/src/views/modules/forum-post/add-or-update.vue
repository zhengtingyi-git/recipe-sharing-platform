<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="标题" prop="title">
            <el-input v-model="ruleForm.title" placeholder="标题" clearable></el-input>
          </el-form-item>
          <el-form-item v-else class="input" label="标题" prop="title">
            <el-input v-model="ruleForm.title" placeholder="标题" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col v-if="type=='info'" :span="12">
          <el-form-item v-if="ruleForm.userId != null && ruleForm.userId !== ''" label="发布者ID">
            <el-input :value="String(ruleForm.userId)" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col v-if="type=='info'" :span="12">
          <el-form-item v-if="ruleForm.yonghuzhanghao" label="用户账号">
            <el-input v-model="ruleForm.yonghuzhanghao" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col v-if="type=='info'" :span="12">
          <el-form-item v-if="ruleForm.yonghuxingming" label="用户昵称">
            <el-input v-model="ruleForm.yonghuxingming" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col v-if="type=='info'" :span="12">
          <el-form-item v-if="ruleForm.createdAt" label="发布时间">
            <el-input :value="formatTime(ruleForm.createdAt)" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item class="upload" v-if="type!='info'" label="图片" prop="picture">
            <file-upload tip="点击上传图片" action="file/upload" :limit="3" :multiple="true" :fileUrls="ruleForm.picture?ruleForm.picture:''" @change="pictureUploadChange"></file-upload>
          </el-form-item>
          <el-form-item v-else-if="ruleForm.picture" label="图片" prop="picture">
            <img
              v-for="(item, index) in pictureList"
              :key="index"
              :src="pictureSrc(item)"
              style="margin-right: 16px; margin-bottom: 8px; max-width: 200px; max-height: 200px; object-fit: cover; vertical-align: top;"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item class="textarea" v-if="type!='info'" label="简介" prop="introduction">
            <el-input type="textarea" :rows="8" placeholder="简介" v-model="ruleForm.introduction"></el-input>
          </el-form-item>
          <el-form-item v-else label="简介" prop="introduction">
            <el-input type="textarea" :rows="6" v-model="ruleForm.introduction" readonly></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="内容" prop="content">
            <editor v-model="ruleForm.content" class="editor" action="file/upload"></editor>
          </el-form-item>
          <el-form-item v-else label="内容" prop="content">
            <div class="forum-post-detail-html" v-html="ruleForm.content"></div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交</el-button>
        <el-button class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: '',
      type: '',
      ruleForm: {
        title: '',
        introduction: '',
        picture: '',
        content: '',
        userId: null,
        yonghuzhanghao: '',
        yonghuxingming: '',
        createdAt: null
      },
      rules: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        picture: [{ required: true, message: '图片不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
      }
    };
  },
  props: ["parent"],
  computed: {
    pictureList() {
      var p = this.ruleForm.picture;
      if (!p) return [];
      return String(p)
        .split(',')
        .map(function (s) {
          return s.trim();
        })
        .filter(Boolean);
    }
  },
  methods: {
    init(id,type) { this.id = id || ''; this.type = type || ''; if (id) this.info(id); },
    pictureSrc(path) {
      if (!path) return '';
      var s = String(path).trim();
      if (/^https?:\/\//i.test(s)) return s;
      var base = (this.$base && this.$base.url) ? String(this.$base.url).replace(/\/$/, '') : '';
      return base + (s.indexOf('/') === 0 ? s : '/' + s);
    },
    formatTime(val) {
      if (val == null || val === '') return '';
      if (typeof val === 'string') return val;
      if (val instanceof Date) {
        var d = val;
        var pad = function (n) { return n < 10 ? '0' + n : n; };
        return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
      }
      return String(val);
    },
    info(id) {
      this.$http({ url: `forum-post/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) this.ruleForm = Object.assign({}, this.ruleForm, data.data);
      });
    },
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (!valid) return;
        if (this.ruleForm.picture != null) this.ruleForm.picture = this.ruleForm.picture.replace(new RegExp(this.$base.url,"g"),"");
        this.$http({ url: `forum-post/${!this.ruleForm.id ? "save" : "update"}`, method: "post", data: this.ruleForm }).then(({ data }) => {
          if (data && data.code === 0) this.back();
          else this.$message.error(data.msg);
        });
      });
    },
    back() { this.parent.showFlag = true; this.parent.addOrUpdateFlag = false; this.parent.search(); },
    pictureUploadChange(fileUrls) { this.ruleForm.picture = fileUrls; },
  }
};
</script>
<style scoped>
.forum-post-detail-html {
  min-height: 120px;
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background: #fafafa;
  line-height: 1.6;
  word-break: break-word;
}
.forum-post-detail-html >>> img {
  max-width: 100%;
  height: auto;
}
</style>
