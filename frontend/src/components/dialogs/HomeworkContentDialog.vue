<template>
  <el-dialog title="输入作业内容" :visible.sync="dialogVisible" width="350px" @opened="focusHomeworkContent">
    <el-input
        placeholder="请输入作业内容"
        v-model="inputContent"
        ref="homeworkContentInput"
        style="width: 300px;"
        :maxlength="200"
        @keyup.enter.native="handleEnterKey">
    </el-input>
    <span slot="footer" class="dialog-footer">
        <el-button @click="$emit('close')">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'HomeworkContentDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    content: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      inputContent: ''
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
    }
  },
  watch: {
    content(newVal) {
      this.inputContent = newVal
    },
    visible(newVal) {
      if (newVal) {
        this.inputContent = this.content
      }
    }
  },
  methods: {
    focusHomeworkContent() {
      // 在弹窗打开后聚焦到文本输入框
      this.$nextTick(() => {
        if (this.$refs.homeworkContentInput) {
          this.$refs.homeworkContentInput.focus()
        }
      })
    },
    handleEnterKey() {
      this.handleSave()
    },
    handleSave() {
      this.$emit('save')
    }
  }
}
</script>