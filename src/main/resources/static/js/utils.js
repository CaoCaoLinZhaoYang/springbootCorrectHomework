// 工具函数文件，包含通用功能

/**
 * 创建日期选择器配置对象
 * @param {Object} vm - Vue实例
 * @returns {Object} 日期选择器配置
 */
function createDatepickerOptions(vm) {
    return {
        shortcuts: [
            {
                text: '前天',
                onClick: (picker) => {
                    const date = new Date();
                    date.setDate(date.getDate() - 2);
                    if (vm) vm.highlightedShortcut = '前天';
                    picker.$emit('pick', date);
                }
            },
            {
                text: '昨天',
                onClick: (picker) => {
                    const date = new Date();
                    date.setDate(date.getDate() - 1);
                    if (vm) vm.highlightedShortcut = '昨天';
                    picker.$emit('pick', date);
                }
            },
            {
                text: '今天',
                onClick: (picker) => {
                    if (vm) vm.highlightedShortcut = '今天';
                    picker.$emit('pick', new Date());
                }
            }
        ],
        onPick: () => {
            // 每次选择日期后更新高亮状态
            if (vm) {
                vm.$nextTick(() => {
                    setTimeout(() => {
                        vm.setHighlightedShortcut && vm.setHighlightedShortcut();
                    }, 50);
                });
            }
        },
        // 当日期面板打开时
        onOpen: () => {
            // 等待DOM更新完成后设置高亮
            if (vm) {
                vm.$nextTick(() => {
                    setTimeout(() => {
                        // 确保打开时显示选中日期所在的月份
                        if (vm.$refs.datePicker && vm.$refs.datePicker.picker) {
                            vm.$refs.datePicker.picker.value = new Date(vm.currentDate);
                            vm.$refs.datePicker.picker.date = new Date(vm.currentDate);
                            vm.$refs.datePicker.picker.resetView && vm.$refs.datePicker.picker.resetView();
                        }
                        
                        // 更新高亮状态
                        vm.setHighlightedShortcut && vm.setHighlightedShortcut();
                    }, 50);
                });
            }
        },
        // 当日期面板关闭时
        onClose: () => {
            // 面板关闭时重置到选中日期所在的月份
            if (vm) {
                vm.$nextTick(() => {
                    // 延迟执行以确保面板完全关闭后再重置
                    setTimeout(() => {
                        // 确保下次打开时显示正确的月份
                        if (vm.$refs.datePicker && vm.$refs.datePicker.picker) {
                            vm.$refs.datePicker.picker.value = new Date(vm.currentDate);
                            vm.$refs.datePicker.picker.date = new Date(vm.currentDate);
                        }
                    }, 100);
                });
            }
        }
    };
}

/**
 * 根据当前日期设置高亮的快捷按钮
 * @param {Object} vm - Vue实例
 */
function setHighlightedShortcut(vm) {
    if (!vm) return;
    
    const offset = calculateDateOffset(vm.currentDate);
    
    switch(offset) {
        case -2:
            vm.highlightedShortcut = '前天';
            break;
        case -1:
            vm.highlightedShortcut = '昨天';
            break;
        case 0:
            vm.highlightedShortcut = '今天';
            break;
        default:
            vm.highlightedShortcut = '';
    }
    
    // 更新日期选择器中快捷按钮的高亮状态
    const updateHighlight = () => {
        try {
            // 查找所有日期选择器面板中的快捷按钮
            const shortcuts = document.querySelectorAll('.el-picker-panel__shortcut');
            if (shortcuts && shortcuts.length > 0) {
                shortcuts.forEach(shortcut => {
                    shortcut.classList.remove('highlighted');
                    if (shortcut.textContent === vm.highlightedShortcut) {
                        shortcut.classList.add('highlighted');
                    }
                });
            }
        } catch (error) {
            console.warn('设置日期选择器高亮时出错:', error);
        }
    };
    
    // 尝试立即更新
    updateHighlight();
}

/**
 * 强制更新日期选择器高亮状态
 * @param {Object} vm - Vue实例
 */
function forceUpdateDatepickerHighlight(vm) {
    if (!vm) return;
    
    // 延迟执行以确保DOM已更新
    setTimeout(() => setHighlightedShortcut(vm), 100);
}

/**
 * 日期选择器获得焦点时的处理函数
 * @param {Object} vm - Vue实例
 */
function onDatePickerFocus(vm) {
    if (!vm) return;
    
    // 延迟设置高亮，确保日期选择器已经渲染
    setTimeout(() => {
        setHighlightedShortcut(vm);
    }, 100);
    
    // 确保日期选择器显示正确的月份
    vm.$nextTick(() => {
        if (vm.$refs.datePicker && vm.$refs.datePicker.picker) {
            // 强制日期选择器面板显示选中日期所在的月份
            vm.$refs.datePicker.picker.value = new Date(vm.currentDate);
            // 确保面板显示正确的月份
            vm.$refs.datePicker.picker.date = new Date(vm.currentDate);
            // 强制刷新日期面板
            vm.$refs.datePicker.picker.resetView && vm.$refs.datePicker.picker.resetView();
        }
    });
}

/**
 * 导出学生欠交作业数据到剪贴板
 * @param {Object} vm - Vue实例
 */
function exportStudentData(vm) {
    // 根据当前排序顺序导出数据
    let exportText = '';
    
    // 添加标题行
    exportText += vm.currentSubject + '科目数据导出：学生' + vm.selectedStudentName +
                 '，未订正作业共' + vm.studentHistoryRecords.length + '次';
    
    // 统计各作业类型的次数
    const typeCount = {};
    vm.studentHistoryRecords.forEach(record => {
        if (typeCount[record.typeName]) {
            typeCount[record.typeName]++;
        } else {
            typeCount[record.typeName] = 1;
        }
    });
    
    // 添加各作业类型的统计信息
    let typeStats = '';
    for (const typeName in typeCount) {
        if (typeCount.hasOwnProperty(typeName)) {
            typeStats += '，' + typeName + '' + typeCount[typeName] + '次';
        }
    }
    
    // 如果有作业类型统计信息，则添加到标题行
    if (typeStats) {
        exportText += '（其中：' + typeStats.substring(1) + '）'; // 去掉开头的逗号
    }
    
    // 添加空行
    exportText += '\n\n';
    
    // 遍历当前显示的记录
    vm.studentHistoryRecords.forEach((record, index) => {
        // 添加日期
        exportText += '日期：' + vm.formatDate(record.date) + '\n';
        
        // 添加作业类型
        exportText += '类型：' + record.typeName + '\n';
        
        // 添加内容（如果存在）
        if (record.content) {
            exportText += '内容：' + record.content + '\n';
        }
        
        // 在每条记录之间添加换行符（除了最后一条记录）
        if (index < vm.studentHistoryRecords.length - 1) {
            exportText += '\n';
        }
    });
    
    // 复制到剪贴板
    if (navigator.clipboard) {
        navigator.clipboard.writeText(exportText).then(() => {
            vm.$message({
                type: 'success',
                message: '数据已复制到剪贴板!'
            });
        }).catch(err => {
            console.error('复制失败:', err);
            vm.$message({
                type: 'error',
                message: '复制到剪贴板失败'
            });
        });
    } else {
        // 兼容旧版浏览器
        const textArea = document.createElement('textarea');
        textArea.value = exportText;
        document.body.appendChild(textArea);
        textArea.select();
        try {
            const successful = document.execCommand('copy');
            if (successful) {
                vm.$message({
                    type: 'success',
                    message: '数据已复制到剪贴板!'
                });
            } else {
                vm.$message({
                    type: 'error',
                    message: '复制到剪贴板失败'
                });
            }
        } catch (err) {
            console.error('复制失败:', err);
            vm.$message({
                type: 'error',
                message: '复制到剪贴板失败'
            });
        }
        document.body.removeChild(textArea);
    }
}

/**
 * 计算当前日期相对于今天的偏移天数
 * @param {string} currentDate - 当前选中的日期 (YYYY-MM-DD格式)
 * @returns {number} 相对于今天的偏移天数
 */
function calculateDateOffset(currentDate) {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const current = new Date(currentDate);
    current.setHours(0, 0, 0, 0);
    
    const diffTime = current - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return diffDays;
}

// 导出函数供外部使用
window.Utils = {
    createDatepickerOptions,
    setHighlightedShortcut,
    forceUpdateDatepickerHighlight,
    onDatePickerFocus,
    exportStudentData,
    calculateDateOffset
};