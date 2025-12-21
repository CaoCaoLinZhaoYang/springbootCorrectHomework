<template>
  <el-dialog title="导入学生名单" :visible.sync="dialogVisible" width="400px">
    <el-input
        type="textarea"
        :rows="10"
        placeholder="请输入学生名单，每行一个或多个学生，支持带干扰字符"
        v-model="inputText"
        style="width: 100%;">
    </el-input>
    <span slot="footer" class="dialog-footer">
      <el-button @click="$emit('update:visible', false)">取 消</el-button>
      <el-button type="primary" @click="handleProcess">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'ImportStudentsDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    text: {
      type: String,
      default: ''
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      }
    },
    inputText: {
      get() {
        return this.text
      },
      set(val) {
        this.$emit('update:text', val)
      }
    }
  },
  methods: {
    handleProcess() {
      this.$emit('process')
    }
  }
}
</script>