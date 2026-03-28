<template>
  <div class="addEdit-block">
    <el-form class="detail-form-content" ref="ruleForm" :model="ruleForm" :rules="rules" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="标题" prop="title">
            <el-input v-model="ruleForm.title" placeholder="标题" clearable></el-input>
          </el-form-item>
          <div v-else>
            <el-form-item class="input" label="标题" prop="title"><el-input v-model="ruleForm.title" placeholder="标题" readonly></el-input></el-form-item>
          </div>
        </el-col>
        <el-col :span="24">
          <el-form-item class="upload" v-if="type!='info'" label="图片" prop="picture">
            <file-upload tip="点击上传图片" action="file/upload" :limit="3" :multiple="true" :fileUrls="ruleForm.picture?ruleForm.picture:''" @change="pictureUploadChange"></file-upload>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row><el-col :span="24"><el-form-item class="textarea" v-if="type!='info'" label="简介" prop="introduction"><el-input type="textarea" :rows="8" placeholder="简介" v-model="ruleForm.introduction"></el-input></el-form-item></el-col></el-row>
      <el-row><el-col :span="24"><el-form-item v-if="type!='info'" label="内容" prop="content"><editor v-model="ruleForm.content" class="editor" action="file/upload"></editor></el-form-item></el-col></el-row>
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
      ruleForm: { title: '', introduction: '', picture: '', content: '' },
      rules: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        picture: [{ required: true, message: '图片不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '内容不能为空', trigger: 'blur' }],
      }
    };
  },
  props: ["parent"],
  methods: {
    init(id,type) { this.id = id || ''; this.type = type || ''; if (id) this.info(id); },
    info(id) {
      this.$http({ url: `forum-post/info/${id}`, method: "get" }).then(({ data }) => {
        if (data && data.code === 0) this.ruleForm = data.data;
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
