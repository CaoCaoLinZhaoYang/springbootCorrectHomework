export default class Utils {
  static createDatepickerOptions(context) {
    return {
      disabledDate(time) {
        // 禁用今天之后的日期
        return time.getTime() > Date.now()
      },
      shortcuts: [{
        text: '今天',
        onClick(picker) {
          picker.$emit('pick', new Date())
          context.highlightedShortcut = 'today'
        }
      }, {
        text: '昨天',
        onClick(picker) {
          const date = new Date()
          date.setTime(date.getTime() - 3600 * 1000 * 24)
          picker.$emit('pick', date)
          context.highlightedShortcut = 'yesterday'
        }
      }, {
        text: '一周前',
        onClick(picker) {
          const date = new Date()
          date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
          picker.$emit('pick', date)
          context.highlightedShortcut = 'weekAgo'
        }
      }]
    }
  }

  static calculateDateOffset(dateString) {
    const selectedDate = new Date(dateString)
    const today = new Date()
    today.setHours(0, 0, 0, 0)
    selectedDate.setHours(0, 0, 0, 0)
    const diffTime = selectedDate.getTime() - today.getTime()
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  }

  static onDatePickerFocus(context) {
    // 在下一个tick更新高亮状态
    context.$nextTick(() => {
      context.forceUpdateDatepickerHighlight()
    })
  }

  static setHighlightedShortcut(context) {
    const offset = context.currentDateOffset
    if (offset === 0) {
      context.highlightedShortcut = 'today'
    } else if (offset === -1) {
      context.highlightedShortcut = 'yesterday'
    } else if (offset === -7) {
      context.highlightedShortcut = 'weekAgo'
    } else {
      context.highlightedShortcut = ''
    }
    
    // 强制更新日期选择器高亮状态
    context.forceUpdateDatepickerHighlight()
  }

  static forceUpdateDatepickerHighlight(context) {
    // 查找日期选择器中的快捷按钮并更新高亮状态
    context.$nextTick(() => {
      if (context.$refs.datePicker && context.$refs.datePicker.picker && context.$refs.datePicker.picker.$el) {
        const shortcuts = context.$refs.datePicker.picker.$el.querySelectorAll('.el-picker-panel__shortcut')
        shortcuts.forEach((button, index) => {
          // 移除所有高亮类
          button.classList.remove('highlighted-shortcut')
          
          // 根据索引和highlightedShortcut状态添加高亮类
          if ((index === 0 && context.highlightedShortcut === 'today') ||
              (index === 1 && context.highlightedShortcut === 'yesterday') ||
              (index === 2 && context.highlightedShortcut === 'weekAgo')) {
            button.classList.add('highlighted-shortcut')
          }
        })
      }
    })
  }

  static exportStudentData(context) {
    // 构造要导出的数据
    let exportData = "日期\t星期\t学号\t姓名\t作业内容\n"
    
    context.studentHistoryRecords.forEach(record => {
      // 计算星期几
      const dateObj = new Date(record.date)
      const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      const weekDay = weekDays[dateObj.getDay()]
      
      // 转义包含制表符或换行符的内容
      const escapedContent = (record.content || '').replace(/\t/g, '\\t').replace(/\n/g, '\\n')
      
      exportData += `${record.date}\t${weekDay}\t${record.studentNumber || ''}\t${context.selectedStudentName}\t${escapedContent}\n`
    })
    
    // 创建Blob对象
    const blob = new Blob(['\ufeff' + exportData], { type: 'text/plain;charset=utf-8' })
    
    // 创建下载链接
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `${context.selectedStudentName}的作业记录.txt`
    
    // 触发下载
    link.click()
    
    // 释放URL对象
    URL.revokeObjectURL(link.href)
    
    context.$message({
      type: 'success',
      message: '数据已导出到下载文件夹'
    })
  }

  static extractStudentNamesFromLine(text, students) {
    const names = []
    
    // 遍历系统中的所有学生，获取他们的姓名后两位作为匹配目标
    const studentNameEndings = students.map(student => student.name.slice(-2))
    
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
  }
}