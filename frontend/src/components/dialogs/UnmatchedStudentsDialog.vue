<template>
  <el-dialog title="以下记录未能匹配" :visible.sync="dialogVisible" width="400px">
    <el-input
        type="textarea"
        :rows="10"
        v-model="inputText"
        readonly
        style="width: 100%;">
    </el-input>
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="$emit('update:visible', false)">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script>
export default {
  name: 'UnmatchedStudentsDialog',
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
  }
}
</script>