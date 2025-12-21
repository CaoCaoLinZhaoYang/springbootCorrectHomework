<template>
  <div id="app" v-loading.fullscreen.lock="loading">
    <div class="header">
      <h1>五4班作业订正系统</h1>
      <div class="poem">
        百川东到海，何时复西归？<br>
        少壮不努力，老大徒伤悲。
      </div>
      <div class="current-time">{{ currentTime }}</div>
    </div>

    <div class="controls">
      <div class="control-group">
        <div style="display: flex; align-items: flex-start;">
          <div style="margin-right: 10px;">
            <div>
              <el-date-picker
                  ref="datePicker"
                  v-model="currentDate"
                  type="date"
                  placeholder="选择日期"
                  format="MM月dd日"
                  value-format="yyyy-MM-dd"
                  @change="loadData"
                  @focus="onDatePickerFocus"
                  :clearable="false"
                  style="width: 120px; cursor: pointer;"
                  :editable="false"
                  :picker-options="pickerOptions"
                  class="custom-date-picker">
              </el-date-picker>
            </div>
            <div>
              <el-select v-model="selectedType" @change="loadData" placeholder="请选择作业类型" style="width: 120px; margin-top: 5px;" class="center-select">
                <el-option
                    v-for="type in homeworkTypes"
                    :key="type.id"
                    :label="type.name"
                    :value="type.id">
                </el-option>
              </el-select>
            </div>
          </div>

          <div style="margin-right: 10px;">
            <div style="display: flex; margin-bottom: 5px;">
              <el-button @click="prevDay" style="width: 70px; text-align: center; margin-right: 5px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-weight: bold;">前一天</el-button>
              <el-button @click="nextDay" style="width: 70px; text-align: center; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-weight: bold;">后一天</el-button>
            </div>
            <div style="display: flex;">
              <el-button @click="prevAssignment" style="width: 70px; text-align: center; margin-right: 5px; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-weight: bold;">前一次</el-button>
              <el-button @click="nextAssignment" style="width: 70px; text-align: center; display: flex; align-items: center; justify-content: center; border-radius: 10px; font-weight: bold;">后一次</el-button>
            </div>
          </div>

          <div style="display: flex; align-items: flex-start;">
            <div style="margin-right: 10px; flex: 1;">
              <el-input
                  v-model="homeworkContent"
                  placeholder="请输入作业内容"
                  style="width: 100%;"
                  @focus="showHomeworkContentDialog"
                  readonly
                  class="homework-content-input el-input">
              </el-input>
              <div style="display: flex; margin-top: 10px;">
                <el-button @click="resetStatus" type="warning" style="margin-right: 10px; border-radius: 10px; font-weight: bold;">重置本页面订正状态</el-button>
                <el-button @click="setAllFinished" type="success" style="border-radius: 10px; font-weight: bold;">设置本页面全部订完</el-button>
              </div>
            </div>

            <el-button @click="showUnfinishedStudents" style="margin-right: 10px; align-self: flex-start; border-radius: 10px; font-weight: bold;">本作业欠交名单</el-button>
            <el-button @click="showImportStudentsDialog" style="margin-right: 10px; align-self: flex-start; border-radius: 10px; font-weight: bold;">导入学生名单</el-button>

            <span style="margin-right: 5px; font-weight: bold; display: flex; align-items: center; padding-top: 8px;">彻查学生作业：</span>
            <el-autocomplete
                v-model="searchKeyword"
                :fetch-suggestions="querySearch"
                placeholder="请输入学号或姓名"
                @select="handleSelectStudent"
                style="width: 150px;"
                :trigger-on-focus="true"
                class="custom-autocomplete">
              <template slot-scope="{ item }">
                <div class="name">{{ item.studentNumber }} - {{ item.name }}</div>
              </template>
            </el-autocomplete>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="stats">
        <div class="stat-item">
          <div class="stat-number finished">{{ finishedCount }}</div>
          <div>已订正</div>
        </div>
        <div class="stat-item">
          <div class="stat-number unfinished">{{ unfinishedCount }}</div>
          <div>未订正</div>
        </div>
        <div class="stat-item">
          <el-button
              @click="switchSubject"
              class="subject-toggle-btn"
              :class="subjectClass"
              style="width: 100%; height: 70px; font-size: 18px; font-weight: bold; border-radius: 15px; color: white; display: flex; align-items: center; justify-content: center;">
            {{ currentSubject }}
          </el-button>
        </div>
        <div class="stat-item">
          <el-button
              @click="toggleUnfinishedCount"
              :class="{'unfinished-count-btn': true, 'active': showUnfinishedCount}"
              style="width: 100%; height: 70px; font-size: 18px; font-weight: bold; border-radius: 15px; display: flex; align-items: center; justify-content: center;">
            欠交数量
          </el-button>
        </div>
      </div>

      <div class="students-section">
        <div class="students-grid">
          <el-button
              v-for="student in sortedStudents"
              :key="student.id"
              :class="getStudentButtonClass(student)"
              @click="toggleStudentStatus(student)"
              @mousedown.native="recordPressStart(student, $event)"
              @mouseup.native="recordPressEnd($event)"
              @contextmenu.native.prevent="showContextMenu($event, student)"
              class="student-btn"
              :style="getStudentButtonStyle()">
            <span style="text-align: center; width: 100%; display: inline-block;">{{ student.studentNumber }}.{{ student.name }}</span>
            <div v-if="showUnfinishedCount && studentUnfinishedCounts[student.id] && studentUnfinishedCounts[student.id] > 0"
                 class="unfinished-badge">
              {{ studentUnfinishedCounts[student.id] > 99 ? '99+' : studentUnfinishedCounts[student.id] }}
            </div>
          </el-button>
        </div>
      </div>

      <div class="spacer"></div>
    </div>

    <!-- 弹窗组件 -->
    <StudentHistoryDialog 
      :visible.sync="studentHistoryDialogVisible"
      :student-name="selectedStudentName"
      :records="studentHistoryRecords"
      :types="homeworkTypes"
      @sort="sortStudentHistory"
      @mark="markAsFinished"
      @edit="editHomeworkContent"
      @locate="locateToRecord"
      @export="exportStudentData" />

    <UnfinishedStudentsDialog
      :visible.sync="unfinishedStudentsDialogVisible"
      :students="unfinishedStudents"
      :type-name="currentTypeName"
      @sort="sortUnfinishedStudents"
      @mark="markUnfinishedStudentAsFinished"
      @edit="editHomeworkContent"
      @locate="locateToRecord" />

    <HomeworkContentDialog
      :visible.sync="homeworkContentDialogVisible"
      :content="tempHomeworkContent"
      @close="closeHomeworkContentDialog"
      @save="saveHomeworkContentFromDialog" />

    <ImportStudentsDialog
      :visible.sync="importStudentsDialogVisible"
      :text="importStudentsText"
      @update:text="val => importStudentsText = val"
      @process="processImportStudents" />

    <UnmatchedStudentsDialog
      :visible.sync="unmatchedStudentsDialogVisible"
      :text="unmatchedStudentsText" />

    <!-- 全局Popover用于长按和右键菜单 -->
    <el-popover
        placement="bottom"
        trigger="manual"
        v-model="contextMenuVisible"
        ref="globalPopover">
      <div style="padding: 5px; cursor: pointer;" @click.stop="checkStudentHistory()">彻查作业</div>
    </el-popover>
  </div>
</template>

<script>
import StudentHistoryDialog from './dialogs/StudentHistoryDialog.vue'
import UnfinishedStudentsDialog from './dialogs/UnfinishedStudentsDialog.vue'
import HomeworkContentDialog from './dialogs/HomeworkContentDialog.vue'
import ImportStudentsDialog from './dialogs/ImportStudentsDialog.vue'
import UnmatchedStudentsDialog from './dialogs/UnmatchedStudentsDialog.vue'
import Utils from '../utils/Utils'

export default {
  name: 'CorrectionSystem',
  components: {
    StudentHistoryDialog,
    UnfinishedStudentsDialog,
    HomeworkContentDialog,
    ImportStudentsDialog,
    UnmatchedStudentsDialog
  },
  data() {
    return {
      loading: true,
      currentTime: '',
      currentDate: this.getCurrentDate(),
      students: [],
      homeworkTypes: [],
      selectedType: 1,
      homeworkContent: '',
      tempHomeworkContent: '', // 用于弹窗中的临时作业内容
      correctionRecords: [],
      finishedCount: 0,
      unfinishedCount: 0,
      searchKeyword: '',
      studentSuggestions: [],
      studentHistoryDialogVisible: false,
      studentHistoryRecords: [],
      unfinishedStudentsDialogVisible: false,
      unfinishedStudents: [],
      homeworkContentDialogVisible: false, // 作业内容弹窗可见性
      selectedStudentName: '', // 当前选中学生的名字
      subjects: ['语文', '数学', '英语'],
      subjectIndex: 0,
      // 学生历史记录排序状态
      studentHistorySortField: 'typeName', // 默认排序字段
      studentHistorySortOrder: 'asc', // 默认排序顺序
      // 历史欠交学生排序状态
      unfinishedStudentsSortField: 'date', // 默认排序字段
      unfinishedStudentsSortOrder: 'asc', // 默认排序顺序
      // 欠交数量显示状态
      showUnfinishedCount: false,
      // 学生欠交数量数据
      studentUnfinishedCounts: {},
      // 长按相关状态
      longPressTimer: null,
      isLongPressTriggered: false, // 标志位，用于区分长按和短按
      selectedStudent: null,
      // popover相关
      contextMenuVisible: false, // 控制全局popover的显示
      mousePosition: { x: 0, y: 0 }, // 鼠标位置
      // 日期选择器选项
      pickerOptions: Utils.createDatepickerOptions(this),
      highlightedShortcut: '',
      // 编辑作业内容相关
      editingRow: null, // 当前正在编辑的行
      editingRowIndex: -1, // 当前正在编辑的行索引
      // 导入学生名单相关
      importStudentsDialogVisible: false,
      importStudentsText: '',
      unmatchedStudentsDialogVisible: false,
      unmatchedStudentsText: ''
    }
  },
  computed: {
    sortedStudents() {
      // 创建学生列表的副本并按学号排序
      return [...this.students].sort((a, b) => {
        return a.studentNumber.localeCompare(b.studentNumber, undefined, {
          numeric: true,
          sensitivity: 'base'
        })
      })
    },
    currentSubject() {
      return this.subjects[this.subjectIndex]
    },
    subjectClass() {
      // 根据当前科目返回相应的CSS类
      switch(this.subjectIndex) {
        case 0: // 语文 - 橙色
          return 'chinese-subject'
        case 1: // 数学 - 蓝色
          return 'math-subject'
        case 2: // 英语 - 红色
          return 'english-subject'
        default:
          return 'math-subject'
      }
    },
    currentTypeName() {
      // 获取当前选中作业类型的名称
      const currentType = this.homeworkTypes.find(type => type.id === this.selectedType)
      return currentType ? currentType.name : ''
    },
    
    // 计算当前日期相对于今天的偏移量
    currentDateOffset() {
      return Utils.calculateDateOffset(this.currentDate)
    }
  },
  watch: {
    currentDate(newVal) {
      sessionStorage.setItem('selectedDate', newVal)
      // 根据当前日期设置高亮的快捷按钮
      this.setHighlightedShortcut()
    },
    selectedType(newVal) {
      sessionStorage.setItem('selectedType', newVal)
    },
    subjectIndex(newVal) {
      localStorage.setItem('subjectIndex', newVal)  // 改为使用 localStorage
      // 切换主题
      this.applySubjectTheme()
    }
  },
  mounted() {
    // 绑定recordPressEndBound方法
    this.recordPressEndBound = this.recordPressEnd.bind(this)
    this.updateTime()
    setInterval(this.updateTime, 1000)
    this.loadStudents()
    
    // 从sessionStorage恢复保存的日期、作业类型和科目
    const savedDate = sessionStorage.getItem('selectedDate')
    const savedType = sessionStorage.getItem('selectedType')
    const savedSubjectIndex = localStorage.getItem('subjectIndex')  // 改为从 localStorage 读取
    
    if (savedDate) {
      this.currentDate = savedDate
    }
    
    if (savedSubjectIndex) {
      this.subjectIndex = parseInt(savedSubjectIndex)
    }
    
    // 应用主题
    this.applySubjectTheme()
    
    // 先加载作业类型，再加载数据
    this.loadHomeworkTypes().then(() => {
      // 恢复保存的作业类型（如果有）
      if (savedType) {
        const typeId = parseInt(savedType)
        // 确保该作业类型在当前科目下存在
        if (this.homeworkTypes.some(type => type.id === typeId)) {
          this.selectedType = typeId
        } else {
          // 如果不存在，则使用第一个作业类型
          if (this.homeworkTypes.length > 0) {
            this.selectedType = this.homeworkTypes[0].id
          }
        }
      } else {
        // 如果没有保存的类型，则使用第一个作业类型
        if (this.homeworkTypes.length > 0) {
          this.selectedType = this.homeworkTypes[0].id
        }
      }
      
      this.loadData()
    })
    
    // 初始化高亮状态
    this.$nextTick(() => {
      this.setHighlightedShortcut()
    })
    
    // 添加全局mouseup事件监听器
    document.addEventListener('mouseup', this.recordPressEndBound)
    
    // 监听窗口大小变化，确保日期选择器样式正确
    window.addEventListener('resize', () => {
      // 延迟执行以确保DOM已经更新
      setTimeout(() => {
        if (this.$refs.datePicker && this.$refs.datePicker.picker && this.$refs.datePicker.picker.visible) {
          this.setHighlightedShortcut()
        }
      }, 100)
    })
    
    // 阻止整个文档的右键菜单，但允许学生按钮上的右键菜单
    document.addEventListener('contextmenu', (event) => {
      // 检查右键点击是否发生在学生按钮上
      if (event.target.classList.contains('student-btn') || event.target.closest('.student-btn')) {
        // 如果是学生按钮，则不阻止默认行为，让Vue处理
        return
      }
      // 阻止其他地方的右键菜单
      event.preventDefault()
    })
  },
  methods: {
    getCurrentDate() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    formatDate(dateString) {
      const date = new Date(dateString)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    
    // 日期选择器获得焦点时
    onDatePickerFocus() {
      Utils.onDatePickerFocus(this)
    },
    
    // 根据当前日期设置高亮的快捷按钮
    setHighlightedShortcut() {
      Utils.setHighlightedShortcut(this)
    },
    
    // 强制更新日期选择器高亮状态
    forceUpdateDatepickerHighlight() {
      Utils.forceUpdateDatepickerHighlight(this)
    },
    tableRowClassName({row, rowIndex}) {
      // 为学生历史欠交记录表格添加行样式
      if (this.studentHistoryRecords && this.studentHistoryRecords.length > 0) {
        // 根据当前排序字段确定分组键
        let groupKey
        if (this.studentHistorySortField === 'date') {
          groupKey = row.date
        } else if (this.studentHistorySortField === 'typeName') {
          groupKey = row.typeName
        }
        
        // 如果有分组键，则为相同分组的行添加相同的背景色类
        if (groupKey !== undefined) {
          // 计算该分组键在所有分组中的索引，用于确定背景色
          const uniqueGroups = []
          for (let i = 0; i < this.studentHistoryRecords.length; i++) {
            let key
            if (this.studentHistorySortField === 'date') {
              key = this.studentHistoryRecords[i].date
            } else if (this.studentHistorySortField === 'typeName') {
              key = this.studentHistoryRecords[i].typeName
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
      }
      return ''
    },
    tableRowClassNameUnfinished({row, rowIndex}) {
      // 为历史欠交学生表格添加行样式
      if (this.unfinishedStudents && this.unfinishedStudents.length > 0) {
        // 根据当前排序字段确定分组键
        let groupKey
        if (this.unfinishedStudentsSortField === 'date') {
          groupKey = row.date
        } else if (this.unfinishedStudentsSortField === 'studentNumber') {
          groupKey = row.studentNumber
        }
        
        // 如果有分组键，则为相同分组的行添加相同的背景色类
        if (groupKey !== undefined) {
          // 计算该分组键在所有分组中的索引，用于确定背景色
          const uniqueGroups = []
          for (let i = 0; i < this.unfinishedStudents.length; i++) {
            let key
            if (this.unfinishedStudentsSortField === 'date') {
              key = this.unfinishedStudents[i].date
            } else if (this.unfinishedStudentsSortField === 'studentNumber') {
              key = this.unfinishedStudents[i].studentNumber
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
      }
      return ''
    },
    updateTime() {
      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const hours = String(now.getHours()).padStart(2, '0')
      const minutes = String(now.getMinutes()).padStart(2, '0')
      const seconds = String(now.getSeconds()).padStart(2, '0')
      this.currentTime = `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`
    },
    applySubjectTheme() {
      // 移除所有主题类
      document.body.classList.remove('chinese-theme', 'math-theme', 'english-theme')
      
      // 根据当前科目添加对应的主题类
      switch(this.subjectIndex) {
        case 0: // 语文 - 深黄色主题
          document.body.classList.add('chinese-theme')
          break
        case 1: // 数学 - 蓝色主题
          document.body.classList.add('math-theme')
          break
        case 2: // 英语 - 粉红色主题
          document.body.classList.add('english-theme')
          break
        default:
          document.body.classList.add('math-theme')
      }
    },
    prevDay() {
      const date = new Date(this.currentDate)
      date.setDate(date.getDate() - 1)
      this.currentDate = this.formatDate(date)
      this.loadData()
    },
    nextDay() {
      const date = new Date(this.currentDate)
      date.setDate(date.getDate() + 1)
      this.currentDate = this.formatDate(date)
      this.loadData()
    },
    prevAssignment() {
      // 获取前一次布置作业的日期
      this.$http.get(`/correctionRecords/previousAssignedDate?typeId=${this.selectedType}&currentDate=${this.currentDate}`)
        .then(response => {
          if (response.data) {
            this.currentDate = response.data
            this.loadData()
          } else {
            this.$message({
              type: 'info',
              message: '当前已是该类型作业最早的布置日期'
            })
          }
        })
        .catch(error => {
          console.error('获取前一次布置日期失败:', error)
          this.$message({
            type: 'error',
            message: '获取前一次布置日期失败'
          })
        })
    },
    nextAssignment() {
      // 获取后一次布置作业的日期
      this.$http.get(`/correctionRecords/nextAssignedDate?typeId=${this.selectedType}&currentDate=${this.currentDate}`)
        .then(response => {
          if (response.data) {
            this.currentDate = response.data
            this.loadData()
          } else {
            this.$message({
              type: 'info',
              message: '当前已是该类型作业最后的布置日期'
            })
          }
        })
        .catch(error => {
          console.error('获取后一次布置日期失败:', error)
          this.$message({
            type: 'error',
            message: '获取后一次布置日期失败'
          })
        })
    },
    loadStudents() {
      return this.$http.get('/students')
        .then(response => {
          this.students = response.data
        })
    },
    loadHomeworkTypes() {
      return this.$http.get(`/homeworkTypes?subjectId=${this.subjectIndex + 1}`)
        .then(response => {
          this.homeworkTypes = response.data
          // 如果当前选中的作业类型不在新的列表中，则重置为第一个类型
          if (this.homeworkTypes.length > 0 && !this.homeworkTypes.some(type => type.id === this.selectedType)) {
            this.selectedType = this.homeworkTypes[0].id
          }
        })
    },
    loadData() {
      // 加载订正记录
      this.$http.get(`/correctionRecords?date=${this.currentDate}&typeId=${this.selectedType}`)
        .then(response => {
          this.correctionRecords = response.data
          this.updateStats()
        })

      // 加载作业内容
      this.$http.get(`/homeworkContents?date=${this.currentDate}&typeId=${this.selectedType}`)
        .then(response => {
          // 检查响应状态码，204表示无内容
          if (response.status === 204) {
            this.homeworkContent = ''
          } else {
            this.homeworkContent = response.data ? response.data.content : ''
          }
        })
        .catch(() => {
          this.homeworkContent = ''
        })
      
      // 如果正在显示欠交数量，则重新加载欠交数量数据
      if (this.showUnfinishedCount) {
        this.loadUnfinishedCounts()
      }
    },
    updateStats() {
      this.$http.get(`/correctionRecords/statistics?date=${this.currentDate}&typeId=${this.selectedType}`)
        .then(response => {
          this.finishedCount = response.data.finished
          this.unfinishedCount = response.data.unfinished
          // 数据加载完成，隐藏loading
          this.loading = false
        })
    },
    getStudentButtonClass(student) {
      const record = this.correctionRecords.find(r => r.studentId === student.id)
      if (record && record.corrected) {
        return 'finished'
      }
      return 'unfinished'
    },
    getStudentButtonStyle() {
      // 不要在这里设置背景色和边框色，让CSS类来处理
      return {
        height: '70px',
        fontSize: '18px',
        fontWeight: 'bold',
        color: '#333',
        width: '100%',
        boxSizing: 'border-box',
        padding: '8px',
        textAlign: 'center',
        margin: '0',
        borderRadius: '15px',
        boxShadow: '0 3px 6px rgba(0,0,0,0.1)',
        transition: 'all 0.3s ease',
        position: 'relative',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }
    },
    toggleStudentStatus(student) {
      console.log('toggleStudentStatus 被调用')
      // 如果触发了长按，则不执行点击操作
      if (this.isLongPressTriggered) {
        console.log('长按状态下不执行点击操作')
        this.isLongPressTriggered = false // 重置标志
        return
      }
      
      console.log('执行点击操作')
      // 正常的点击逻辑
      let record = this.correctionRecords.find(r => r.studentId === student.id)

      if (!record) {
        // 创建新记录
        record = {
          date: this.currentDate,
          studentId: student.id,
          homeworkTypeId: this.selectedType,
          subjectId: this.getSubjectIdByHomeworkType(this.selectedType),
          corrected: true
        }

        this.$http.post('/correctionRecords', record)
          .then(response => {
            this.correctionRecords.push(response.data)
            this.updateStats()
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
      } else {
        // 更新现有记录
        record.corrected = !record.corrected
        this.$http.post('/correctionRecords', record)
          .then(response => {
            this.updateStats()
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
      }
    },
    saveHomeworkContent() {
      console.log('进入saveHomeworkContent方法')
      console.log('准备发送请求，参数: ', {
        date: this.currentDate,
        homeworkTypeId: this.selectedType,
        content: this.homeworkContent
      })
      
      this.$http.get(`/homeworkContents?date=${this.currentDate}&typeId=${this.selectedType}`)
        .then(response => {
          // 检查响应状态码，204表示无内容
          let content
          if (response.status === 204) {
            content = {}
          } else {
            content = response.data || {}
          }

          content.date = this.currentDate
          content.homeworkTypeId = this.selectedType
          content.content = this.homeworkContent

          return this.$http.post('/homeworkContents', content)
        })
        .then(response => {
          // 保存成功后的处理
          this.$message({
            type: 'success',
            message: '作业内容保存成功!'
          })
        })
        .catch(() => {
          // 新建
          const content = {
            date: this.currentDate,
            homeworkTypeId: this.selectedType,
            content: this.homeworkContent
          }
          return this.$http.post('/homeworkContents', content)
            .then(response => {
              // 保存成功后的处理
              this.$message({
                type: 'success',
                message: '作业内容保存成功!'
              })
            })
            .catch(error => {
              // 保存失败的处理
              this.$message({
                type: 'error',
                message: '作业内容保存失败: ' + (error.response?.data?.message || error.message || '未知错误')
              })
            })
        })
    },
    resetStatus() {
      this.$confirm('此操作将重置本页面订正状态, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.put(`/correctionRecords/reset?date=${this.currentDate}&typeId=${this.selectedType}`)
          .then(() => {
            this.loadData()
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
            this.$message({
              type: 'success',
              message: '重置成功!'
            })
          })
          .catch(error => {
            console.error('重置状态失败:', error)
            this.$message({
              type: 'error',
              message: '重置失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      }).catch(() => {
        // 用户取消操作
      })
    },
    setAllFinished() {
      this.$confirm('此操作将设置本页面所有学生为订正完成状态, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.put(`/correctionRecords/setAllFinished?date=${this.currentDate}&typeId=${this.selectedType}`)
          .then(() => {
            this.loadData()
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
            this.$message({
              type: 'success',
              message: '设置成功!'
            })
          })
          .catch(error => {
            console.error('设置全部完成失败:', error)
            this.$message({
              type: 'error',
              message: '设置失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      }).catch(() => {
        // 用户取消操作
      })
    },
    querySearch(queryString, cb) {
      // 当queryString为空时，显示所有学生（按学号排序）
      if (!queryString) {
        // 创建学生列表的副本并按学号排序
        const sortedStudents = [...this.students].sort((a, b) => {
          return a.studentNumber.localeCompare(b.studentNumber, undefined, {
            numeric: true,
            sensitivity: 'base'
          })
        })
        cb(sortedStudents)
        return
      }

      // 当有查询字符串时，按原有逻辑过滤学生
      this.$http.get(`/students/search?keyword=${queryString}`)
        .then(response => {
          cb(response.data)
        })
    },
    handleSelectStudent(item) {
      // 获取学生历史欠交记录
      this.$http.get(`/correctionRecords/studentUnfinishedAllTypes?studentId=${item.id}&subjectId=${this.subjectIndex + 1}`)
        .then(response => {
          // 过滤掉未来日期的记录
          const today = new Date()
          today.setHours(0, 0, 0, 0)

          let studentHistoryRecords = response.data
            .filter(record => {
              const recordDate = new Date(record.date)
              recordDate.setHours(0, 0, 0, 0)
              return recordDate <= today
            })
            .map(record => {
              const type = this.homeworkTypes.find(t => t.id === record.homeworkTypeId)
              return {
                ...record,
                typeName: type ? type.name : ''
              }
            })

          // 为每条记录获取对应的作业内容（每条记录单独获取，因为同一天可能有多种作业类型）
          const contentPromises = studentHistoryRecords.map(record => {
            // 确保日期格式为 yyyy-MM-dd
            let formattedDate = record.date
            if (typeof record.date === 'string' && record.date.includes('T')) {
              // 如果是ISO格式日期，转换为 yyyy-MM-dd
              const d = new Date(record.date)
              formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
            }
            
            return this.$http.get(`/homeworkContents?date=${formattedDate}&typeId=${record.homeworkTypeId}`)
              .then(response => {
                // 检查响应状态码，204表示无内容
                if (response.status === 204) {
                  return { data: null }
                } else {
                  return response
                }
              })
              .catch(() => ({ data: null })) // 处理没有作业内容的情况
          })

          return Promise.all(contentPromises)
            .then(results => {
              // 将作业内容添加到学生记录中
              this.studentHistoryRecords = studentHistoryRecords.map((record, index) => {
                let recordDate = record.date
                if (typeof recordDate === 'string' && recordDate.includes('T')) {
                  const d = new Date(recordDate)
                  recordDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`
                }
                
                // 从结果中获取对应的作业内容
                const result = results[index]
                const contentData = result && result.data
                const content = contentData ? contentData.content : ''
                
                return {
                  ...record,
                  content: content || ''
                }
              })

              if (this.studentHistoryRecords.length > 0) {
                // 设置选中学生的名字
                this.selectedStudentName = item.name
                // 默认按作业类型升序排序
                this.sortStudentHistory('typeName', 'asc')
                this.studentHistoryDialogVisible = true
              } else {
                this.$message({
                  type: 'info',
                  message: '该学生在所有作业类型下都没有欠交记录'
                })
              }
            })
        })
        .catch((e) => {
          console.error('获取学生历史欠交记录失败:', e)
          this.$message({
            type: 'error',
            message: '获取数据失败: ' + (e.response?.data?.message || e.message || '未知错误')
          })
        })
    },
    showUnfinishedStudents() {
      this.$http.get(`/correctionRecords/unfinishedStudents?typeId=${this.selectedType}`)
        .then(response => {
          // 先获取所有未完成学生的基本信息
          const unfinishedStudents = response.data.map(item => {
            // 格式化日期
            let formattedDate = item.date
            if (typeof item.date === 'string' && item.date.includes('T')) {
              // 如果是ISO格式日期，转换为 yyyy-MM-dd
              const date = new Date(item.date)
              formattedDate = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`
            }

            // 计算星期几
            const dateObj = new Date(formattedDate)
            const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
            const weekDay = weekDays[dateObj.getDay()]

            return {
              date: formattedDate,
              weekDay: weekDay,
              studentNumber: item.studentNumber,
              name: item.name,
              rawDate: formattedDate, // 保存格式化后的日期用于查询作业内容
              formattedDate: formattedDate // 保存格式化后的日期用于查询作业内容
            }
          })

          // 为每个日期获取作业内容
          const dates = [...new Set(unfinishedStudents.map(s => s.formattedDate))]
          const contentPromises = dates.map(date =>
            this.$http.get(`/homeworkContents?date=${date}&typeId=${this.selectedType}`)
              .then(response => {
                // 检查响应状态码，204表示无内容
                if (response.status === 204) {
                  return { data: null }
                } else {
                  return response
                }
              })
              .catch(() => ({ data: null })) // 处理没有作业内容的情况
          )

          return Promise.all(contentPromises)
            .then(results => {
              const contentMap = {}
              dates.forEach((date, index) => {
                // 检查results[index]是否存在再访问其data属性
                const result = results[index]
                const contentData = result && result.data
                contentMap[date] = contentData ? contentData.content : ''
              })

              // 将作业内容添加到学生记录中
              this.unfinishedStudents = unfinishedStudents.map(student => ({
                ...student,
                content: contentMap[student.formattedDate] || ''
              }))

              // 默认按日期升序排序
              this.sortUnfinishedStudents('date', 'asc')
              this.unfinishedStudentsDialogVisible = true
            })
        })
        .catch(error => {
          console.error('获取未完成学生数据失败:', error)
          this.$message({
            type: 'error',
            message: '获取数据失败: ' + (error.response?.data?.message || error.message || '未知错误')
          })
        })
    },
    sortUnfinishedStudents(field, order) {
      this.unfinishedStudentsSortField = field
      this.unfinishedStudentsSortOrder = order
      
      this.unfinishedStudents.sort((a, b) => {
        let result = 0

        if (field === 'studentNumber') {
          // 学号按数值大小排序
          const numA = parseInt(a.studentNumber)
          const numB = parseInt(b.studentNumber)
          result = numA - numB
        } else if (field === 'date') {
          // 日期排序
          result = new Date(a.date) - new Date(b.date)
          // 如果日期相同，按学号升序排列
          if (result === 0) {
            const numA = parseInt(a.studentNumber)
            const numB = parseInt(b.studentNumber)
            result = numA - numB
          }
        }

        // 如果是降序，反转结果
        if (order === 'desc') {
          result = -result
        }

        return result
      })
    },
    markAsFinished(row) {
      // 检查是否已经是完成状态，如果是则取消完成状态
      if (row.markedAsFinished) {
        // 取消完成状态
        row.corrected = false
        this.$http.post('/correctionRecords', row)
          .then(() => {
            this.$message({
              type: 'success',
              message: '已取消订正状态!'
            })

            // 取消标记该行已处理
            this.$delete(row, 'markedAsFinished')

            // 刷新主页面数据
            this.loadData()
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
          .catch(error => {
            row.corrected = true // 恢复原始状态
            this.$message({
              type: 'error',
              message: '取消订正状态失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      } else {
        // 标记为完成状态
        row.corrected = true
        this.$http.post('/correctionRecords', row)
          .then(() => {
            this.$message({
              type: 'success',
              message: '标记成功!'
            })

            // 标记该行已处理，但不从列表中移除
            this.$set(row, 'markedAsFinished', true)

            // 如果标记的是当前日期的记录，也需要更新主页面的数据
            if (row.date === this.currentDate) {
              this.loadData()
            }

            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
          .catch(error => {
            row.corrected = false // 恢复原始状态
            this.$message({
              type: 'error',
              message: '标记失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      }
    },
    sortStudentHistory(field, order) {
      this.studentHistorySortField = field
      this.studentHistorySortOrder = order
      
      this.studentHistoryRecords.sort((a, b) => {
        let result = 0

        if (field === 'date') {
          // 日期排序
          result = new Date(a.date) - new Date(b.date)
          // 如果日期相同，按作业类型ID升序排列
          if (result === 0) {
            result = a.homeworkTypeId - b.homeworkTypeId
          }
        } else if (field === 'typeName') {
          // 作业类型ID排序
          result = a.homeworkTypeId - b.homeworkTypeId
          // 如果作业类型ID相同，按日期升序排列
          if (result === 0) {
            result = new Date(a.date) - new Date(b.date)
          }
        }

        // 如果是降序，反转结果
        if (order === 'desc') {
          result = -result
        }

        return result
      })
    },
    markUnfinishedStudentAsFinished(row) {
      // 检查是否已经是完成状态，如果是则取消完成状态
      if (row.markedAsFinished) {
        // 首先需要找到该学生的订正记录ID
        this.$http.get(`/correctionRecords?date=${row.rawDate}&typeId=${this.selectedType}`)
          .then(response => {
            // 在返回的记录中找到对应学生的记录
            const student = this.students.find(s => s.studentNumber === row.studentNumber)
            if (student) {
              const record = response.data.find(r => r.studentId === student.id)
              if (record) {
                // 更新记录状态为未完成
                record.corrected = false
                return this.$http.post('/correctionRecords', record)
              }
            }
            return Promise.reject("未找到对应记录")
          })
          .then(() => {
            // 取消标记该行已处理
            this.$delete(row, 'markedAsFinished')

            // 如果标记的是当前日期的记录，也需要更新主页面的数据
            if (row.date === this.currentDate) {
              this.loadData()
            }

            this.$message({
              type: 'success',
              message: '已取消订正状态!'
            })
            
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
          .catch(error => {
            this.$message({
              type: 'error',
              message: '取消订正状态失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      } else {
        // 标记历史欠交学生为已完成
        // 首先需要找到该学生的订正记录ID
        this.$http.get(`/correctionRecords?date=${row.rawDate}&typeId=${this.selectedType}`)
          .then(response => {
            // 在返回的记录中找到对应学生的记录
            const student = this.students.find(s => s.studentNumber === row.studentNumber)
            if (student) {
              const record = response.data.find(r => r.studentId === student.id)
              if (record) {
                // 更新记录状态为已完成
                record.corrected = true
                return this.$http.post('/correctionRecords', record)
              }
            }
            return Promise.reject("未找到对应记录")
          })
          .then(() => {
            // 标记该行已处理，但不从列表中移除
            this.$set(row, 'markedAsFinished', true)

            // 如果标记的是当前日期的记录，也需要更新主页面的数据
            if (row.date === this.currentDate) {
              this.loadData()
            }

            this.$message({
              type: 'success',
              message: '标记成功!'
            })
            
            // 如果开启了欠交数量显示，则更新欠交数量
            if (this.showUnfinishedCount) {
              this.loadUnfinishedCounts()
            }
          })
          .catch(error => {
            this.$message({
              type: 'error',
              message: '标记失败: ' + (error.response?.data?.message || error.message || '未知错误')
            })
          })
      }
    },
    showHomeworkContentDialog() {
      // 将当前作业内容复制到临时变量
      this.tempHomeworkContent = this.homeworkContent
      // 显示作业内容输入弹窗
      this.homeworkContentDialogVisible = true
    },
    
    focusHomeworkContent() {
      // 在弹窗打开后聚焦到文本输入框
      this.$nextTick(() => {
        if (this.$refs.homeworkContentInput) {
          this.$refs.homeworkContentInput.focus()
        }
      })
    },

    handleEnterKey() {
      this.saveHomeworkContentFromDialog()
    },

    closeHomeworkContentDialog() {
      this.homeworkContentDialogVisible = false
    },
    
    switchSubject() {
      // 切换科目
      this.subjectIndex = (this.subjectIndex + 1) % this.subjects.length
      // 重新加载作业类型
      this.loadHomeworkTypes().then(() => {
        // 重新加载数据
        this.loadData()
        // 如果正在显示欠交数量，则重新加载欠交数量数据
        if (this.showUnfinishedCount) {
          this.loadUnfinishedCounts()
        }
      })
      // 弹出消息提示
      this.$message({
        message: '已切换到' + this.currentSubject,
        type: 'success',
        duration: 1000
      })
    },
    
    toggleUnfinishedCount() {
      this.showUnfinishedCount = !this.showUnfinishedCount
      if (this.showUnfinishedCount) {
        this.loadUnfinishedCounts()
      }
    },
    
    loadUnfinishedCounts() {
      // 获取所有学生在当前科目下的欠交数量（批量获取）
      const subjectId = this.subjectIndex + 1
      this.$http.get(`/correctionRecords/unfinishedCountBySubject?subjectId=${subjectId}`)
        .then(response => {
          const counts = {}
          response.data.forEach(item => {
            counts[item.studentId] = item.unfinishedCount
          })
          this.studentUnfinishedCounts = counts
        })
        .catch(error => {
          console.error('获取欠交数量失败:', error)
          this.$message({
            type: 'error',
            message: '获取欠交数量失败'
          })
        })
    },
    
    getSubjectIdByHomeworkType(homeworkTypeId) {
      // 根据作业类型ID获取科目ID
      const homeworkType = this.homeworkTypes.find(type => type.id === homeworkTypeId)
      return homeworkType ? homeworkType.subjectId : 1 // 默认为语文科目
    },
    // 记录按下开始时间并启动定时器
    recordPressStart(student, event) {
      // 清除之前的定时器
      this.cancelLongPress()
                  
      this.selectedStudent = student
      // 重置长按标志
      this.isLongPressTriggered = false
      
      // 保存鼠标位置
      this.mousePosition = {
        x: event.clientX || (event.touches && event.touches[0].clientX),
        y: event.clientY || (event.touches && event.touches[0].clientY)
      }
      
      // 设置1秒后触发长按事件的定时器
      this.longPressTimer = setTimeout(() => {
        this.isLongPressTriggered = true // 设置长按标志
        
        // 显示全局Popover
        this.showPopover()
      }, 1000)
    },
    // 取消长按事件
    cancelLongPress() {
      clearTimeout(this.longPressTimer)
      this.isLongPressTriggered = false
    },
    // 处理长按事件
    handleLongPress() {
      if (this.isLongPressTriggered) {
        this.contextMenuVisible = true
        this.globalClickListener = (event) => {
          if (!this.$refs.contextMenu.contains(event.target)) {
            this.contextMenuVisible = false
            document.removeEventListener('mousedown', this.globalClickListener)
          }
        }
        document.addEventListener('mousedown', this.globalClickListener)
      }
    },
    // 彻查学生作业历史
    checkStudentHistory() {
      this.contextMenuVisible = false
      // 移除全局点击监听器
      if (this.globalClickListener) {
        document.removeEventListener('mousedown', this.globalClickListener)
      }
      if (this.selectedStudent) {
        this.handleSelectStudent(this.selectedStudent)
      }
    },
        
    // 编辑学生历史欠交记录的作业内容
    editHomeworkContent(row) {
      // 保存当前编辑的行
      this.editingRow = row
      
      // 将当前行的作业内容复制到临时变量
      this.tempHomeworkContent = row.content || ''
      // 显示作业内容输入弹窗
      this.homeworkContentDialogVisible = true
    },
    
    // 编辑历史欠交学生的作业内容
    editUnfinishedStudentHomeworkContent(row) {
      // 保存当前编辑的行
      this.editingRow = row
      
      // 将当前行的作业内容复制到临时变量
      this.tempHomeworkContent = row.content || ''
      // 显示作业内容输入弹窗
      this.homeworkContentDialogVisible = true
    },

    saveHomeworkContentFromDialog() {
      // 更新当前编辑行的内容
      if (this.editingRow) {
        // 如果是学生历史欠交记录弹窗
        if (this.studentHistoryRecords.includes(this.editingRow)) {
          // 构造保存作业内容所需的数据
          const contentData = {
            date: this.editingRow.date,
            homeworkTypeId: this.editingRow.homeworkTypeId,
            content: this.tempHomeworkContent
          }
          
          // 保存作业内容到服务器
          this.$http.post('/homeworkContents', contentData)
            .then(response => {
              // 保存成功后，更新当前行的内容
              this.editingRow.content = this.tempHomeworkContent
              
              // 同时更新同一弹窗中具有相同日期和作业类型的所有记录
              for (let i = 0; i < this.studentHistoryRecords.length; i++) {
                const record = this.studentHistoryRecords[i]
                // 确保日期格式一致后再比较
                const editingRowDate = new Date(this.editingRow.date).toDateString()
                const recordDate = new Date(record.date).toDateString()
                
                if (recordDate === editingRowDate && 
                    record.homeworkTypeId === this.editingRow.homeworkTypeId) {
                  this.$set(this.studentHistoryRecords, i, {
                    ...record,
                    content: this.tempHomeworkContent
                  })
                }
              }
              
              this.$message({
                type: 'success',
                message: '作业内容保存成功!'
              })
            })
            .catch(error => {
              this.$message({
                type: 'error',
                message: '作业内容保存失败: ' + (error.response?.data?.message || error.message || '未知错误')
              })
            })
            .finally(() => {
              // 隐藏弹窗
              this.homeworkContentDialogVisible = false
              // 清空编辑行
              this.editingRow = null
            })
        } 
        // 如果是历史欠交学生弹窗
        else if (this.unfinishedStudents.includes(this.editingRow)) {
          // 构造保存作业内容所需的数据
          const contentData = {
            date: this.editingRow.date,
            homeworkTypeId: this.selectedType,
            content: this.tempHomeworkContent
          }
          
          // 保存作业内容到服务器
          this.$http.post('/homeworkContents', contentData)
            .then(response => {
              // 保存成功后，更新当前行的内容
              this.editingRow.content = this.tempHomeworkContent
              
              // 同时更新同一弹窗中具有相同日期的所有记录（因为这个弹窗中所有记录都是同一作业类型）
              for (let i = 0; i < this.unfinishedStudents.length; i++) {
                const student = this.unfinishedStudents[i]
                if (student.date === this.editingRow.date) {
                  this.$set(this.unfinishedStudents, i, {
                    ...student,
                    content: this.tempHomeworkContent
                  })
                }
              }
              
              this.$message({
                type: 'success',
                message: '作业内容保存成功!'
              })
            })
            .catch(error => {
              this.$message({
                type: 'error',
                message: '作业内容保存失败: ' + (error.response?.data?.message || error.message || '未知错误')
              })
            })
            .finally(() => {
              // 隐藏弹窗
              this.homeworkContentDialogVisible = false
              // 清空编辑行
              this.editingRow = null
            })
        }
      } else {
        // 如果没有编辑行，说明是从主页面调用的
        // 将临时变量的值保存到作业内容
        this.homeworkContent = this.tempHomeworkContent
        // 保存作业内容到服务器
        this.saveHomeworkContent()
        // 隐藏弹窗
        this.homeworkContentDialogVisible = false
      }
    },
    
    // 显示全局Popover
    showPopover() {
      // 显示全局Popover
      this.contextMenuVisible = true
      
      // 设置popover位置为鼠标位置
      this.$nextTick(() => {
        const popover = this.$refs.globalPopover
        if (popover && popover.$el) {
          const popper = popover.$el
          popper.style.position = 'fixed'
          popper.style.left = this.mousePosition.x + 'px'
          popper.style.top = this.mousePosition.y + 'px'
          popper.style.transform = 'translate(0, 0)'
        }
      })
      
      // 添加全局点击监听器，用于隐藏popover
      this.addGlobalClickListener()
    },
    // 记录按下结束时间并判断是否为长按
    recordPressEnd(event) {
      // 取消长按检测
      this.cancelLongPress()
      
      // 移除document级别的mouseup事件监听器
      document.removeEventListener('mouseup', this.recordPressEndBound)
      
      // 如果已经触发了长按（无论popover是否完全显示），则不需要做其他操作
      if (this.isLongPressTriggered) {
        return
      }
    },
    // 显示右键菜单
    showContextMenu(event, student) {
      console.log('显示右键菜单被调用', { 
        studentId: student.id, 
        eventType: event.type
      })
      
      event.preventDefault()  // 阻止浏览器默认右键菜单
      event.stopPropagation() // 阻止事件冒泡
      
      this.selectedStudent = student
      console.log('显示右键菜单，学生ID:', student.id)
      
      // 清除长按检测
      this.cancelLongPress()
      
      // 保存鼠标位置
      this.mousePosition = {
        x: event.clientX,
        y: event.clientY
      }
      
      // 显示全局Popover
      this.contextMenuVisible = true
      console.log('设置contextMenuVisible为true')
      
      // 设置popover位置为鼠标位置
      this.$nextTick(() => {
        const popover = this.$refs.globalPopover
        if (popover && popover.$el) {
          const popper = popover.$el
          popper.style.position = 'fixed'
          popper.style.left = this.mousePosition.x + 'px'
          popper.style.top = this.mousePosition.y + 'px'
          popper.style.transform = 'translate(0, 0)'
        }
      })
      
      // 添加全局点击监听器，用于隐藏popover
      this.addGlobalClickListener()
    },
    
    // 定位到学生历史欠交记录
    locateToRecord(row) {
      // 关闭弹窗
      this.studentHistoryDialogVisible = false
      
      // 设置日期
      this.currentDate = row.date
      
      // 设置作业类型
      this.selectedType = row.homeworkTypeId
      
      // 刷新数据
      this.loadData()
    },
    
    // 定位到历史欠交学生记录
    locateToUnfinishedRecord(row) {
      // 关闭弹窗
      this.unfinishedStudentsDialogVisible = false
      
      // 设置日期
      this.currentDate = row.date
      
      // 设置作业类型（历史欠交学生弹窗中的所有记录都是同一作业类型）
      // this.selectedType 保持不变，因为我们已经在正确的作业类型下了
      
      // 刷新数据
      this.loadData()
    },
    // 添加全局点击监听器
    addGlobalClickListener() {
      // 创建一个函数用于隐藏popover
      const hidePopover = (event) => {
        // 检查mousedown事件是否发生在popover内部
        const popoverElements = document.querySelectorAll('.el-popover')
        let isClickInsidePopover = false
        for (let i = 0; i < popoverElements.length; i++) {
          if (popoverElements[i] === event.target || popoverElements[i].contains(event.target)) {
            isClickInsidePopover = true
            break
          }
        }
        
        // 检查是否是学生按钮
        const isClickOnStudentButton = event.target.classList.contains('student-btn') || 
                                     event.target.closest('.student-btn')
        
        // 只有当mousedown事件发生在popover外部且不是学生按钮时，才隐藏popover
        if (!isClickInsidePopover && !isClickOnStudentButton) {
          this.contextMenuVisible = false
          // 移除事件监听器
          document.removeEventListener('mousedown', hidePopover)
        }
      }
      
      // 在下一个tick添加事件监听器，避免立即触发
      this.$nextTick(() => {
        // 先移除之前的监听器（如果有的话）
        document.removeEventListener('mousedown', this.globalClickListener)
        // 保存当前监听器的引用
        this.globalClickListener = hidePopover
        // 使用mousedown事件而不是click事件
        document.addEventListener('mousedown', hidePopover)
      })
    },
    
    // 显示导入学生名单对话框
    showImportStudentsDialog() {
      this.importStudentsText = ''
      this.importStudentsDialogVisible = true
    },
    
    // 处理导入的学生名单
    processImportStudents() {
      if (!this.importStudentsText.trim()) {
        this.$message({
          type: 'warning',
          message: '请输入学生名单'
        })
        return
      }
      
      // 按行分割文本
      const lines = this.importStudentsText.split('\n')
      const unmatchedLines = [] // 无法匹配的行
      const matchedStudents = [] // 成功匹配的学生
      
      // 遍历每一行
      for (const line of lines) {
        if (!line.trim()) continue // 跳过空行
        
        // 提取可能的学生姓名（最后两个汉字）
        const names = this.extractStudentNamesFromLine(line)
        
        if (names.length > 0) {
          // 尝试匹配每个名字
          let lineMatched = false
          const matchedStudentsInLine = [] // 用于存储当前行匹配到的学生
          
          for (const name of names) {
            const student = this.students.find(s => s.name.slice(-2) === name)
            if (student) {
              // 检查该学生是否已在当前行中被匹配过
              if (!matchedStudentsInLine.includes(student.id)) {
                matchedStudents.push({
                  student: student,
                  line: line
                })
                matchedStudentsInLine.push(student.id)
                lineMatched = true
              }
            }
          }
          
          // 只有当一行中没有任何名字被匹配时，才将其添加到未匹配列表
          if (!lineMatched) {
            unmatchedLines.push(line)
          }
        } else {
          unmatchedLines.push(line)
        }
      }
      
      // 处理匹配到的学生
      if (matchedStudents.length > 0) {
        this.markStudentsAsFinished(matchedStudents)
      }
      
      // 显示无法匹配的行
      if (unmatchedLines.length > 0) {
        this.unmatchedStudentsText = unmatchedLines.join('\n')
        this.unmatchedStudentsDialogVisible = true
      }
      
      // 关闭导入对话框
      this.importStudentsDialogVisible = false
      
      // 如果有匹配到的学生，给出提示
      if (matchedStudents.length > 0) {
        this.$message({
          type: 'success',
          message: `成功匹配并标记${matchedStudents.length}名学生为已完成`
        })
      }
    },
    
    // 从文本行中提取可能的学生姓名（最后两个汉字）
    extractStudentNamesFromLine(text) {
      const names = []
      
      // 遍历系统中的所有学生，获取他们的姓名后两位作为匹配目标
      const studentNameEndings = this.students.map(student => student.name.slice(-2))
      
      // 在原文本中查找所有匹配的学生姓名后两位
      studentNameEndings.forEach(ending => {
        // 检查该姓名后缀在文本中出现了多少次
        let startIndex = 0
        while (startIndex < text.length) {
          const index = text.indexOf(ending, startIndex)
          if (index === -1) break
          names.push(ending)
          startIndex = index + 1 // 从下一个位置继续查找，支持重复出现的名字
        }
      })
      
      return [...new Set(names)] // 去重
    },
    
    // 标记学生为已完成状态
    markStudentsAsFinished(students) {
      // 去除重复的学生（同一行可能匹配到同一个学生多次）
      const uniqueStudents = []
      const seenStudentIds = new Set()
      
      students.forEach(studentObj => {
        if (!seenStudentIds.has(studentObj.student.id)) {
          seenStudentIds.add(studentObj.student.id)
          uniqueStudents.push(studentObj)
        }
      })
      
      const updatePromises = uniqueStudents.map(studentObj => {
        const student = studentObj.student
        let record = this.correctionRecords.find(r => r.studentId === student.id)
        
        if (!record) {
          // 创建新记录
          record = {
            date: this.currentDate,
            studentId: student.id,
            homeworkTypeId: this.selectedType,
            subjectId: this.getSubjectIdByHomeworkType(this.selectedType),
            corrected: true
          }
        } else {
          // 更新现有记录
          record.corrected = true
        }
        
        return this.$http.post('/correctionRecords', record)
      })
      
      // 执行所有更新请求
      Promise.all(updatePromises)
        .then(responses => {
          // 更新本地记录
          responses.forEach(response => {
            const updatedRecord = response.data
            const index = this.correctionRecords.findIndex(r => r.studentId === updatedRecord.studentId)
            if (index > -1) {
              this.$set(this.correctionRecords, index, updatedRecord)
            } else {
              this.correctionRecords.push(updatedRecord)
            }
          })
          
          // 更新统计数据
          this.updateStats()
          
          // 如果开启了欠交数量显示，则更新欠交数量
          if (this.showUnfinishedCount) {
            this.loadUnfinishedCounts()
          }
        })
        .catch(error => {
          console.error('标记学生为已完成状态时出错:', error)
          this.$message({
            type: 'error',
            message: '更新学生状态时出错'
          })
        })
    },
    
    // 导出学生欠交作业数据到剪贴板
    exportStudentData() {
      Utils.exportStudentData(this)
    }
  }
}
</script>

<style>
/* 引入完整的样式表 */
@import '../assets/styles/index.css';

/* 覆盖scoped样式，确保标题颜色正确 */
.header h1 {
  color: white !important;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

/* 覆盖scoped样式，确保诗歌文本正确 */
.poem {
  text-align: center;
  font-size: 18px;
  line-height: 1.5;
  font-weight: bold;
}

/* 覆盖scoped样式，确保当前时间文本正确 */
.current-time {
  font-size: 28px;
  font-weight: bold;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
}

/* 覆盖scoped样式，确保控制区域正确 */
.controls {
  margin-bottom: 20px;
  padding: 20px;
  background-color: white;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

/* 覆盖scoped样式，确保主要内容区域正确 */
.main-content {
  display: flex;
  gap: 20px;
}

/* 覆盖scoped样式，确保统计区域正确 */
.stats {
  width: 130px;
  background: linear-gradient(135deg, #ffffff, #f0f4f8);
  padding: 20px 15px;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
}

/* 覆盖scoped样式，确保统计项正确 */
.stat-item {
  text-align: center;
  margin-bottom: 25px;
  padding: 10px;
  border-radius: 12px;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: all 0.3s ease;
}

/* 覆盖scoped样式，确保统计数字正确 */
.stat-number {
  font-size: 28px;
  font-weight: bold;
}

.finished {
  color: #67C23A;
}

.unfinished {
  color: #F56C6C;
}

/* 覆盖scoped样式，确保学生网格正确 */
.students-grid {
  display: grid;
  grid-template-columns: repeat(10, 1fr);
  gap: 10px;
  margin-bottom: 20px;
}

/* 覆盖scoped样式，确保学生按钮正确 */
.student-btn {
  height: 70px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  width: 100%;
  box-sizing: border-box;
  padding: 8px;
  text-align: center;
  margin: 0;
  border-radius: 15px;
  border: 2px solid #e0e0e0;
  background: linear-gradient(135deg, #ffffff, #f8f9fa);
  box-shadow: 0 3px 6px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.student-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 10px rgba(0,0,0,0.15);
  /* 修复悬停时变蓝的问题 */
  color: #333;
  border-color: #e0e0e0;
}

.student-btn span {
  text-align: center;
  width: 100%;
}

.subject-toggle-btn {
  width: 100%;
  height: 70px;
  font-size: 18px;
  font-weight: bold;
  padding: 8px;
  text-align: center;
  margin: 0;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
}

.chinese-subject {
  background: linear-gradient(135deg, #FFA500, #FF8C00) !important; /* 橙色 */
  border: 2px solid #FFA500 !important;
}

.math-subject {
  background: linear-gradient(135deg, #409EFF, #64b5f6) !important; /* 蓝色 */
  border: 2px solid #409EFF !important;
}

.english-subject {
  background: linear-gradient(135deg, #F56C6C, #ff4242) !important; /* 红色 */
  border: 2px solid #F56C6C !important;
}

.subject-toggle-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4);
}

/* 欠交数量按钮样式 */
.unfinished-count-btn {
  background: white; /* 白底 */
  border: 2px solid #909399; /* 灰色边框 */
  color: black; /* 黑字 */
  width: 100%;
  height: 70px;
  font-size: 18px;
  font-weight: bold;
  padding: 8px;
  text-align: center;
  margin: 0;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(144, 147, 153, 0.3);
  transition: all 0.3s ease;
}

.unfinished-count-btn:hover {
  background: #f5f5f5; /* 悬停时 slightly gray background */
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(144, 147, 153, 0.4);
}

.unfinished-count-btn.active {
  background: linear-gradient(135deg, #E6A23C, #d6821c); /* 琥珀色 */
  border: 2px solid #E6A23C;
  color: white; /* 激活时白字 */
  box-shadow: 0 4px 8px rgba(230, 162, 60, 0.3);
}

.unfinished-count-btn.active:hover {
  background: linear-gradient(135deg, #f6b24c, #e6922c);
  box-shadow: 0 6px 12px rgba(230, 162, 60, 0.4);
}

/* 覆盖Element UI默认按钮样式，确保按钮颜色统一 */
.el-button.unfinished-count-btn {
  border: 1px solid #909399 !important;
  background: white !important;
  color: black !important;
  border-radius: 15px !important;
}

.el-button.unfinished-count-btn:hover {
  background: #f5f5f5 !important;
  border: 2px solid #909399 !important;
}

.el-button.unfinished-count-btn.active {
  background: linear-gradient(135deg, #E6A23C, #d6821c) !important;
  border: 2px solid #E6A23C !important;
  color: white !important;
}

.el-button.unfinished-count-btn.active:hover {
  background: linear-gradient(135deg, #f6b24c, #e6922c) !important;
  border: 2px solid #E6A23C !important;
}

/* 覆盖scoped样式，确保未完成徽章正确 */
.unfinished-badge {
  position: absolute;
  top: -10px;
  right: -10px;
  background-color: #FFA500;
  color: white;
  border-radius: 50%;
  min-width: 28px;
  height: 28px;
  font-size: 21px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  padding: 0 4px;
  z-index: 1;
}

/* 覆盖scoped样式，确保间隔元素正确 */
.spacer {
  width: 130px;
}

</style>