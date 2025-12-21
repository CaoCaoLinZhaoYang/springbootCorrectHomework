<template>
  <el-dialog :visible.sync="dialogVisible" class="student-history-dialog" :title="'彻查作业 - ' + studentName">
    <div slot="title">
      <span>彻查作业 - {{ studentName }}</span>
      <el-button size="mini" type="primary" @click="$emit('export')" style="margin-left: 15px;">导出数据</el-button>
    </div>
    <div style="margin-bottom: 10px;">
      <el-button
          @click="$emit('sort', 'date', 'asc')"
          :type="sortField === 'date' && sortOrder === 'asc' ? 'primary' : ''">
        日期升序
      </el-button>
      <el-button
          @click="$emit('sort', 'date', 'desc')"
          :type="sortField === 'date' && sortOrder === 'desc' ? 'primary' : ''">
        日期降序
      </el-button>
      <el-button
          @click="$emit('sort', 'typeName', 'asc')"
          :type="sortField === 'typeName' && sortOrder === 'asc' ? 'primary' : ''">
        作业类型升序
      </el-button>
      <el-button
          @click="$emit('sort', 'typeName', 'desc')"
          :type="sortField === 'typeName' && sortOrder === 'desc' ? 'primary' : ''">
        作业类型降序
      </el-button>
    </div>
    <el-table :data="records" style="width: 100%" :row-class-name="tableRowClassName">
      <el-table-column prop="date" label="日期" width="120">
        <template slot-scope="scope">
          <el-button 
            size="mini" 
            :type="scope.row.markedAsFinished ? 'info' : 'danger'"
            @click="$emit('mark', scope.row)"
            :key="scope.row.id + '-' + scope.row.markedAsFinished"
            style="width: 100%; font-size: 14px; padding: 7px 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.2); border-radius: 10px;">
            {{ formatDate(scope.row.date) }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column prop="typeName" label="作业类型" width="120">
        <template slot-scope="scope">
          <span :class="{ 'marked-as-finished': !!scope.row.markedAsFinished }">{{ scope.row.typeName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip>
        <template slot-scope="scope">
          <span :class="{ 'marked-as-finished': !!scope.row.markedAsFinished }">{{ scope.row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="功能" width="200">
        <template slot-scope="scope">
          <div style="display: flex; gap: 5px;">
            <el-button
                size="mini"
                type="primary"
                @click="$emit('edit', scope.row)"
                :disabled="!!scope.row.markedAsFinished"
                style="border-radius: 10px;">
              修改内容
            </el-button>
            <el-button
                size="mini"
                type="success"
                @click="$emit('locate', scope.row)"
                :disabled="!!scope.row.markedAsFinished"
                style="border-radius: 10px;">
              定位
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
export default {
  name: 'StudentHistoryDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    studentName: {
      type: String,
      default: ''
    },
    records: {
      type: Array,
      default: () => []
    },
    types: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      sortField: 'typeName',
      sortOrder: 'asc'
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
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    tableRowClassName({row, rowIndex}) {
      // 根据当前排序字段确定分组键
      let groupKey
      if (this.sortField === 'date') {
        groupKey = row.date
      } else if (this.sortField === 'typeName') {
        groupKey = row.typeName
      }
      
      // 如果有分组键，则为相同分组的行添加相同的背景色类
      if (groupKey !== undefined) {
        // 计算该分组键在所有分组中的索引，用于确定背景色
        const uniqueGroups = []
        for (let i = 0; i < this.records.length; i++) {
          let key
          if (this.sortField === 'date') {
            key = this.records[i].date
          } else if (this.sortField === 'typeName') {
            key = this.records[i].typeName
          }
          
          if (!uniqueGroups.includes(key)) {
            uniqueGroups.push(key)
          }
        }
        
        const groupIndex = uniqueGroups.indexOf(groupKey)
        // 使用不同的背景色类
        if (groupIndex % 2 === 0) {
          return 'group-row-light'
        } else {
          return 'group-row-dark'
        }
      }
      return ''
    }
  }
}
</script>

<style scoped>
/* 弹窗样式优化 */
.el-dialog {
  border-radius: 15px !important;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(90deg, #409EFF, #64b5f6);
  color: white;
  padding: 20px;
  transition: all 0.3s ease;
}

/* 弹窗关闭按钮样式优化 */
.el-dialog__headerbtn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 30px;
  height: 30px;
  font-size: 24px;
  color: white;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.el-dialog__headerbtn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

/* 语文主题弹窗头部 */
body.chinese-theme .el-dialog__header {
  background: linear-gradient(90deg, #d2691e, #ff8c00);
}

/* 数学主题弹窗头部 */
body.math-theme .el-dialog__header {
  background: linear-gradient(90deg, #1e90ff, #4169e1);
}

/* 英语主题弹窗头部 */
body.english-theme .el-dialog__header {
  background: linear-gradient(90deg, #ff69b4, #ff1493);
}

.el-dialog__title {
  color: white;
  font-weight: bold;
}

.el-dialog__body {
  padding: 20px;
}

.el-dialog__footer {
  padding: 15px 20px;
  background-color: #f5f7fa;
  transition: all 0.3s ease;
}

/* 语文主题弹窗底部 */
body.chinese-theme .el-dialog__footer {
  background-color: #fff8dc;
}

/* 数学主题弹窗底部 */
body.math-theme .el-dialog__footer {
  background-color: #f0f8ff;
}

/* 英语主题弹窗底部 */
body.english-theme .el-dialog__footer {
  background-color: #fff0f5;
}

/* 按钮样式优化 */
.el-button {
  border-radius: 10px;
  font-weight: bold;
}

.el-button--primary:not(:disabled) {
  background: linear-gradient(90deg, #409EFF, #64b5f6);
  border: none;
}

/* 语文主题主要按钮 */
body.chinese-theme .el-button--primary:not(:disabled) {
  background: linear-gradient(90deg, #d2691e, #ff8c00);
}

/* 数学主题主要按钮 */
body.math-theme .el-button--primary:not(:disabled) {
  background: linear-gradient(90deg, #1e90ff, #4169e1);
}

/* 英语主题主要按钮 */
body.english-theme .el-button--primary:not(:disabled) {
  background: linear-gradient(90deg, #ff69b4, #ff1493);
}

.el-button--success:not(:disabled) {
  background: linear-gradient(90deg, #67C23A, #95d475);
  border: none;
  border-radius: 10px;
}

.el-button--warning:not(:disabled) {
  background: linear-gradient(90deg, #E6A23C, #f3d19e);
  border: none;
  border-radius: 10px;
}

.el-button--danger:not(:disabled) {
  background: linear-gradient(90deg, #F56C6C, #ff8e8e);
  border: none;
  border-radius: 10px;
}

/* 修复标记完成按钮悬浮时闪烁白色的问题 */
.el-button--danger:not(:disabled):hover {
  background: linear-gradient(90deg, #ff6b6b, #ff7b7b) !important;
  border: none !important;
  color: white !important;
}

/* 学生历史欠交记录弹窗中的标记完成按钮特殊样式 */
.student-history-dialog .el-button--danger:not(:disabled):hover {
  background: linear-gradient(90deg, #ff6b6b, #ff7b7b) !important;
  border: none !important;
  color: white !important;
}

/* 表格样式优化 */
.el-table {
  border-radius: 10px;
  overflow: hidden;
  /* 允许在表格中选择文字 */
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
}

.el-table th {
  background-color: #ecf5ff;
}

/* 语文主题表格头部 */
body.chinese-theme .el-table th {
  background-color: #fffacd;
}

/* 数学主题表格头部 */
body.math-theme .el-table th {
  background-color: #e0ffff;
}

/* 英语主题表格头部 */
body.english-theme .el-table th {
  background-color: #ffe4e1;
}

/* 表格中的按钮样式优化 */
.el-table .el-button--danger:not(:disabled) {
  background: linear-gradient(90deg, #F56C6C, #ff8e8e) !important;
  border: none !important;
  color: white !important;
  border-radius: 10px !important;
}

.el-table .el-button--danger:not(:disabled):hover {
  background: linear-gradient(90deg, #ff6b6b, #ff7b7b) !important;
  border: none !important;
  color: white !important;
  transform: scale(1.05);
  transition: all 0.2s ease;
}

.el-table .el-button--success:not(:disabled) {
  background: linear-gradient(90deg, #67C23A, #95d475) !important;
  border: none !important;
  color: white !important;
  border-radius: 10px !important;
}

.el-table .el-button--success:not(:disabled):hover {
  background: linear-gradient(90deg, #5cb831, #7cd94d) !important;
  border: none !important;
  color: white !important;
  transform: scale(1.05);
  transition: all 0.2s ease;
}

/* 已标记完成行的样式 */
.marked-as-finished {
  text-decoration: line-through;
  color: #C0C4CC;
}

/* 表格中已标记完成的按钮样式 */
.el-table .el-button:disabled {
  background-color: #DCDFE6 !important;
  border-color: #DCDFE6 !important;
  cursor: not-allowed;
}

/* 禁用按钮内文字保持原色 */
.el-table .el-button--primary:disabled {
  color: #FFFFFF !important;
}

.el-table .el-button--success:disabled {
  color: #FFFFFF !important;
}

.el-table .el-button--danger:disabled {
  color: #FFFFFF !important;
}

/* 提高禁用按钮样式的优先级，防止被其他样式覆盖 */
.el-table .el-button:disabled,
.el-table .el-button:disabled:hover,
.el-table .el-button:disabled:focus,
.el-table .el-button[disabled],
.el-table .el-button[disabled]:hover,
.el-table .el-button[disabled]:focus {
  background-color: #DCDFE6 !important;
  border-color: #DCDFE6 !important;
  color: #C0C4CC !important;
  cursor: not-allowed !important;
  opacity: 1 !important;
  transform: none !important;
  box-shadow: none !important;
}

/* 特定类型按钮禁用状态下的文字颜色 */
.el-table .el-button--primary:disabled,
.el-table .el-button--primary:disabled:hover,
.el-table .el-button--primary:disabled:focus,
.el-table .el-button--primary[disabled],
.el-table .el-button--primary[disabled]:hover,
.el-table .el-button--primary[disabled]:focus {
  color: #FFFFFF !important;
  background-color: #DCDFE6 !important;
  border-color: #DCDFE6 !important;
}

.el-table .el-button--success:disabled,
.el-table .el-button--success:disabled:hover,
.el-table .el-button--success:disabled:focus,
.el-table .el-button--success[disabled],
.el-table .el-button--success[disabled]:hover,
.el-table .el-button--success[disabled]:focus {
  color: #FFFFFF !important;
  background-color: #DCDFE6 !important;
  border-color: #DCDFE6 !important;
}

.el-table .el-button--danger:disabled,
.el-table .el-button--danger:disabled:hover,
.el-table .el-button--danger:disabled:focus,
.el-table .el-button--danger[disabled],
.el-table .el-button--danger[disabled]:hover,
.el-table .el-button--danger[disabled]:focus {
  color: #FFFFFF !important;
  background-color: #DCDFE6 !important;
  border-color: #DCDFE6 !important;
}

/* 表格行背景色样式 */
.group-row-light {
  background-color: #f0f9eb !important;
}

.group-row-dark {
  background-color: #e0e0e0 !important;
}

/* 禁用表格行hover效果 */
.el-table--enable-row-hover .el-table__body tr:hover > td {
  background-color: transparent !important;
}

/* 弹窗中表格的特殊样式 */
.student-history-dialog .el-table .el-button--danger:not(:disabled),
.unfinished-students-dialog .el-table .el-button--danger:not(:disabled) {
  background: linear-gradient(90deg, #F56C6C, #ff8e8e) !important;
  border: none !important;
  color: white !important;
}

.student-history-dialog .el-table .el-button--danger:not(:disabled):hover,
.unfinished-students-dialog .el-table .el-button--danger:not(:disabled):hover {
  background: linear-gradient(90deg, #ff6b6b, #ff7b7b) !important;
  border: none !important;
  color: white !important;
  transform: scale(1.05);
  transition: all 0.2s ease;
}