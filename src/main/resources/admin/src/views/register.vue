<template>
  <div>
        <div class="container">
      <div class="login-form">
        <h1 class="h1">小食堂记管理员注册</h1>
		<el-form ref="rgsForm" class="rgs-form" :model="rgsForm">
			<!-- 管理员(admin)注册表单 -->
			<el-form-item label="管理员账号" class="input" v-if="tableName==='admin'">
			  <el-input v-model="ruleForm.username" autocomplete="off" placeholder="管理员账号"  />
			</el-form-item>
			<el-form-item label="密码" class="input" v-if="tableName==='admin'">
			  <el-input v-model="ruleForm.password" autocomplete="off" placeholder="密码" type="password" />
			</el-form-item>
			<el-form-item label="确认密码" class="input" v-if="tableName==='admin'">
			  <el-input v-model="ruleForm.password2" autocomplete="off" placeholder="确认密码" type="password"/>
			</el-form-item>
			<el-form-item label="手机号" class="input" v-if="tableName==='admin'">
			  <el-input v-model="ruleForm.phone" autocomplete="off" placeholder="手机号" />
			</el-form-item>
			<!-- 用户(user)注册表单 -->
			<el-form-item label="用户账号" class="input" v-if="tableName==='user'">
			  <el-input v-model="ruleForm.yonghuzhanghao" autocomplete="off" placeholder="用户账号"  />
			</el-form-item>
			<el-form-item label="用户昵称" class="input" v-if="tableName==='user'">
			  <el-input v-model="ruleForm.yonghuxingming" autocomplete="off" placeholder="用户昵称"  />
			</el-form-item>
			<el-form-item label="密码" class="input" v-if="tableName==='user'">
			  <el-input v-model="ruleForm.mima" autocomplete="off" placeholder="密码" type="password" />
			</el-form-item>
			<el-form-item label="确认密码" class="input" v-if="tableName==='user'">
			  <el-input v-model="ruleForm.mima2" autocomplete="off" placeholder="确认密码" type="password"/>
			</el-form-item>
			<el-form-item label="联系方式" class="input" v-if="tableName==='user'">
			  <el-input v-model="ruleForm.phone" autocomplete="off" placeholder="联系方式"  />
			</el-form-item>
			<div style="display: flex;flex-wrap: wrap;width: 100%;justify-content: center;">
				<el-button class="btn" type="primary" @click="login()">注册</el-button>
				<el-button class="btn close" type="primary" @click="close()">取消</el-button>
			</div>
		</el-form>
      </div>
      <!-- <div class="nk-navigation">
        <a href="#">
          <div @click="login()">注册</div>
        </a>
      </div> -->
    </div>
  </div>
</template>
<script>


export default {
  data() {
    return {
      ruleForm: {
      },
      tableName:"",
      rules: {},
    };
  },
  mounted(){
    let table = this.$storage.get("loginTable");
    this.tableName = table;
      },
  created() {
    
  },
  methods: {
    // 获取uuid
    getUUID () {
      return new Date().getTime();
    },
    close(){
	this.$router.push({ path: "/login" });
    },
    // 注册
    login() {
	var url=this.tableName+"/register";
      // 管理员(admin)注册校验
      if(this.tableName === 'admin'){
        if(!this.ruleForm.username){
          this.$message.error(`管理员账号不能为空`);
          return;
        }
        if(!this.ruleForm.password){
          this.$message.error(`密码不能为空`);
          return;
        }
        if(!this.ruleForm.password2){
          this.$message.error(`确认密码不能为空`);
          return;
        }
        if(this.ruleForm.password !== this.ruleForm.password2){
          this.$message.error(`两次密码输入不一致`);
          return;
        }
        if(!this.ruleForm.phone){
          this.$message.error(`手机号不能为空`);
          return;
        }
        if(!this.$validate.isMobile(this.ruleForm.phone)){
          this.$message.error(`手机号应输入手机格式`);
          return;
        }
      }
      // 用户(user)注册校验
      if((!this.ruleForm.yonghuzhanghao) && this.tableName === 'user'){
        this.$message.error(`用户账号不能为空`);
        return
      }
      if((!this.ruleForm.mima) && this.tableName === 'user'){
        this.$message.error(`密码不能为空`);
        return
      }
      if((!this.ruleForm.mima2) && this.tableName === 'user'){
        this.$message.error(`确认密码不能为空`);
        return
      }
      if((this.ruleForm.mima !== this.ruleForm.mima2) && this.tableName === 'user'){
	    this.$message.error(`两次密码输入不一致`);
	    return
      }
      if(this.tableName === 'user' && this.ruleForm.phone && (!this.$validate.isMobile(this.ruleForm.phone))){
        this.$message.error(`联系方式应输入手机格式`);
        return
      }
      // 提交数据：管理员注册必须带 role='管理员'
      let postData = this.ruleForm;
      if(this.tableName === 'admin'){
        postData = { username: this.ruleForm.username, password: this.ruleForm.password, phone: this.ruleForm.phone, role: '管理员' };
      }
      this.$http({
        url: url,
        method: "post",
        data: postData
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "注册成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.$router.replace({ path: "/login" });
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
	.el-radio__input.is-checked .el-radio__inner {
		border-color: #00c292;
		background: #00c292;
	}

	.el-radio__input.is-checked .el-radio__inner {
		border-color: #00c292;
		background: #00c292;
	}

	.el-radio__input.is-checked .el-radio__inner {
		border-color: #00c292;
		background: #00c292;
	}

	.el-radio__input.is-checked+.el-radio__label {
		color: #00c292;
	}

	.el-radio__input.is-checked+.el-radio__label {
		color: #00c292;
	}

	.el-radio__input.is-checked+.el-radio__label {
		color: #00c292;
	}

	.h1 {
		margin-top: 10px;
	}

	body {
		padding: 0;
		margin: 0;
	}

	// .container {
 //    min-height: 100vh;
 //    text-align: center;
 //    // background-color: #00c292;
 //    padding-top: 20vh;
 //    background-image: url(../assets/img/bg.jpg);
 //    background-size: 100% 100%;
 //    opacity: 0.9;
 //  }

	// .login-form:before {
	// 	vertical-align: middle;
	// 	display: inline-block;
	// }

	// .login-form {
	// 	max-width: 500px;
	// 	padding: 20px 0;
	// 	width: 80%;
	// 	position: relative;
	// 	margin: 0 auto;

	// 	.label {
	// 		min-width: 60px;
	// 	}

	// 	.input-group {
	// 		max-width: 500px;
	// 		padding: 20px 0;
	// 		width: 80%;
	// 		position: relative;
	// 		margin: 0 auto;
	// 		display: flex;
	// 		align-items: center;

	// 		.input-container {
	// 			display: inline-block;
	// 			width: 100%;
	// 			text-align: left;
	// 			margin-left: 10px;
	// 		}

	// 		.icon {
	// 			width: 30px;
	// 			height: 30px;
	// 		}

	// 		.input {
	// 			position: relative;
	// 			z-index: 2;
	// 			float: left;
	// 			width: 100%;
	// 			margin-bottom: 0;
	// 			box-shadow: none;
	// 			border-top: 0px solid #ccc;
	// 			border-left: 0px solid #ccc;
	// 			border-right: 0px solid #ccc;
	// 			border-bottom: 1px solid #ccc;
	// 			padding: 0px;
	// 			resize: none;
	// 			border-radius: 0px;
	// 			display: block;
	// 			width: 100%;
	// 			height: 34px;
	// 			padding: 6px 12px;
	// 			font-size: 14px;
	// 			line-height: 1.42857143;
	// 			color: #555;
	// 			background-color: #fff;
	// 		}

	// 	}
	// }

	.nk-navigation {
		margin-top: 15px;

		a {
			display: inline-block;
			color: #fff;
			background: rgba(255, 255, 255, .2);
			width: 100px;
			height: 50px;
			border-radius: 30px;
			text-align: center;
			display: flex;
			align-items: center;
			margin: 0 auto;
			justify-content: center;
			padding: 0 20px;
		}

		.icon {
			margin-left: 10px;
			width: 30px;
			height: 30px;
		}
	}

	.register-container {
		margin-top: 10px;

		a {
			display: inline-block;
			color: #fff;
			max-width: 500px;
			height: 50px;
			border-radius: 30px;
			text-align: center;
			display: flex;
			align-items: center;
			margin: 0 auto;
			justify-content: center;
			padding: 0 20px;

			div {
				margin-left: 10px;
			}
		}
	}

	.container {
		height: 100vh;
		background-position: center center;
		background-size: cover;
		background-repeat: no-repeat;
		background-image: url('../../../front/xznstatic/img/qiantaidenglu.png');
		    
		.login-form {
			right: 50%;
			top: 50%;
			transform: translate3d(50%, -50%, 0);
			border-radius: 10px;
			background-color: rgba(255, 253, 208, 0.92);
			font-size: 14px;
			font-weight: 500;
      box-sizing: border-box;

			width: 360px;
			height: auto;
			padding: 15px;
			margin: 0 auto;
			border-radius: 15px;
			border-width: 0;
			border-style: solid;
			border-color: rgba(255,0,0,0);
			background-color: rgba(255, 253, 208, 0.92);
			box-shadow: 0 0 6px rgba(255,0,0,.1);

			.h1 {
				width: 100%;
				height: auto;
				line-height: normal;
				color: rgba(64, 158, 255, 1);
				font-size: 28px;
				padding: 0;
				margin: 0 auto;
				border-radius: 0;
				border-width: 0;
				border-style: solid;
				border-color: rgba(255,0,0,0);
				background-color: rgba(255,0,0,0);
				box-shadow: 0 0 6px rgba(255,0,0,0);
				text-align: center;
			}

			.rgs-form {
				display: flex;
				flex-direction: column;
				justify-content: center;
				align-items: center;

        .el-form-item {
          width: 100%;
          display: flex;

          ::v-deep .el-form-item__content {
            flex: 1;
            display: flex;
          }
        }

				.input {
          width: 100%;
          height:auto;
          padding: 0;
          margin: 0 0 12px 0;
          border-radius: 0;
          border-width: 0;
          border-style: solid;
          border-color: rgba(255,0,0,0);
          background-color: rgba(255,0,0,0);
          box-shadow: 0 0 6px rgba(255,0,0,0);

					::v-deep .el-form-item__label {
            width: 84px;
            line-height: 44px;
            color: #606266;
            font-size: 14px;
            padding: 0 10px 0 0;
            margin: 0;
            border-radius: 0;
            border-width: 0;
            border-style: solid;
            border-color: rgba(255,0,0,0);
            background-color: rgba(255,0,0,0);
            box-shadow: 0 0 6px rgba(255,0,0,0);
					}

					::v-deep .el-input__inner {
            width: 100%;
            height: 40px;
            line-height:40px;
            color: #606266;
            font-size: 14px;
            padding: 0 12px;
            margin: 0;
            border-radius: 4px;
            border-width: 1px;
            border-style: solid;
            border-color: #606266;
            background-color: #fff;
            box-shadow: 0 0 6px rgba(255,0,0,0);
            text-align: left;
					}
				}

        .send-code {
          ::v-deep .el-input__inner {
            width: 180px;
            height: 44px;
            line-height:44px;
            color: #606266;
            font-size: 14px;
            padding: 0 12px;
            margin: 0;
            border-radius: 0;
            border-width: 1px;
            border-style: solid;
            border-color: #606266;
            background-color: #fff;
            box-shadow: 0 0 6px rgba(255,0,0,0);
            text-align: left;
          }

          .register-code {
            margin: 0;
            padding: 0;
            width: 86px;
            height: 44px;
            line-height:44px;
            color: #fff;
            font-size: 14px;
            border-width: 0;
            border-style: solid;
            border-color: rgba(255,0,0,0);
            border-radius: 0;
            background-color: rgb(64, 158, 255);
            box-shadow: 0 0 6px rgba(255,0,0,0);
          }
        }

				.btn {
					margin: 0 10px;
          padding: 0;
					width: 88px;
					height: 44px;
          line-height:44px;
					color: #fff;
					font-size: 14px;
					border-width: 1px;
					border-style: solid;
					border-color: #409EFF;
					border-radius: 4px;
					background-color: #409EFF;
          box-shadow: 0 0 6px rgba(255,0,0,0);
				}

				.close {
          margin: 0 10px;
          padding: 0;
          width: 88px;
          height: 44px;
          line-height:44px;
          color: #409EFF;
          font-size: 14px;
          border-width: 1px;
          border-style: solid;
          border-color: #409EFF;
          border-radius: 5px;
          background-color: #FFF;
          box-shadow: 0 0 6px rgba(255,0,0,0);
				}

			}
		}
	}
</style>
