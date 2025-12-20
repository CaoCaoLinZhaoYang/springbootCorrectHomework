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


/**
 * 初始化手动快录功能
 * @param {Object} vm - Vue实例
 */
function initManualQuickRecord(vm) {
    // 初始化手动快录数据
    vm.manualQuickRecordData = vm.students.map(student => {
        // 对1到9号学生添加前导0
        let displayNumber = student.studentNumber;
        const studentNum = parseInt(student.studentNumber);
        if (studentNum >= 1 && studentNum <= 9) {
            displayNumber = '0' + studentNum;
        }
        
        return {
            key: student.id,
            label: `${displayNumber}.${student.name}`,
            studentNumber: student.studentNumber,
            name: student.name,
            pinyinInitials: student.pinyinInitials || ''
        };
    })
    // 按学号数字大小排序
    .sort((a, b) => {
        const numA = parseInt(a.studentNumber);
        const numB = parseInt(b.studentNumber);
        return numA - numB;
    });
}

/**
 * 打开手动快录对话框
 * @param {Object} vm - Vue实例
 */
function openManualQuickRecord(vm) {
    // 初始化手动快录数据
    initManualQuickRecord(vm);
    
    // 显示手动快录对话框
    vm.manualQuickRecordVisible = true;
    
    // 在下一帧聚焦到搜索框并调整面板高度
    vm.$nextTick(() => {
        // 获取穿梭框组件
        const transfer = vm.$refs.manualQuickRecordTransfer;
        if (transfer) {
            // 延迟确保DOM完全渲染
            setTimeout(() => {
                // 聚焦到左侧面板的搜索框
                const inputs = transfer.$el.querySelectorAll('.el-transfer-panel:first-child .el-input__inner');
                if (inputs && inputs.length > 0) {
                    // 设置输入框属性，使移动设备默认显示数字键盘
                    inputs[0].setAttribute('inputmode', 'numeric');
                    inputs[0].setAttribute('pattern', '[0-9]*');
                    inputs[0].focus();
                }
                
                // 添加输入事件监听器
                const handleInput = (event) => {
                    // 获取输入值
                    const value = event.target.value;
                    
                    // 检查是否是纯数字输入
                    if (value && /^\d+$/.test(value)) {
                        // 查找完全匹配的学生（输入值与显示的学号完全一致）
                        const exactMatches = vm.manualQuickRecordData.filter(item => {
                            // 获取学号前缀（对于1到9号学生是01-09，其他是原始学号）
                            let displayNumber = item.studentNumber;
                            const studentNum = parseInt(item.studentNumber);
                            if (studentNum >= 1 && studentNum <= 9) {
                                displayNumber = '0' + studentNum;
                            }
                            
                            // 检查输入值是否与显示的学号完全一致
                            return displayNumber === value;
                        });
                        
                        // 只有在完全匹配且唯一的情况下才自动添加
                        if (exactMatches.length === 1 && 
                            !vm.manualQuickRecordSelected.includes(exactMatches[0].key)) {
                            // 自动添加到已选中列表（插入到开头以实现后添加的在上）
                            const studentKey = exactMatches[0].key;
                            vm.manualQuickRecordSelected.unshift(studentKey);
                            
                            // 清空搜索框
                            vm.$nextTick(() => {
                                event.target.value = '';
                                // 手动触发input事件以更新组件状态
                                const inputEvent = new Event('input', { bubbles: true });
                                event.target.dispatchEvent(inputEvent);
                            });
                        }
                    }
                };
                
                // 为输入框添加事件监听器
                if (inputs && inputs.length > 0) {
                    const input = inputs[0];
                    // 移除旧的事件监听器（如果有的话）
                    input.removeEventListener('input', handleInput);
                    // 添加新的事件监听器
                    input.addEventListener('input', handleInput);
                }
            }, 100);
        }
    });
}

/**
 * 处理手动快录对话框关闭事件
 * @param {Object} vm - Vue实例
 */
function handleManualQuickRecordClose(vm) {
    // 清空已选择项
    vm.manualQuickRecordSelected = [];
    
    // 延迟清空Transfer组件的各种状态
    vm.$nextTick(() => {
        if (vm.$refs.manualQuickRecordTransfer) {
            const transfer = vm.$refs.manualQuickRecordTransfer;
            
            // 清空左侧面板的选中状态
            if (transfer.leftChecked) {
                transfer.leftChecked.splice(0);
            }
            
            // 清空右侧面板的选中状态
            if (transfer.rightChecked) {
                transfer.rightChecked.splice(0);
            }
            
            // 清空搜索关键词
            if (transfer.$children && transfer.$children[0]) {
                transfer.$children[0].query = '';
            }
        }
    });
}

/**
 * 确认手动快录
 * @param {Object} vm - Vue实例
 */
function confirmManualQuickRecord(vm) {
    // 获取选中的学生
    const selectedStudents = vm.students.filter(student => 
        vm.manualQuickRecordSelected.includes(student.id)
    );
    
    // 标记这些学生为已完成
    if (selectedStudents.length > 0) {
        // 这里调用外部的标记学生完成方法
        if (typeof vm.markStudentsAsFinished === 'function') {
            vm.markStudentsAsFinished(selectedStudents.map(student => ({
                student: student,
                line: ''
            })));
            
            vm.$message({
                type: 'success',
                message: `成功标记${selectedStudents.length}名学生为已完成`
            });
        }
    }
    
    // 关闭手动快录对话框
    vm.manualQuickRecordVisible = false;
}

/**
 * 手动快录过滤方法
 * @param {string} query - 查询字符串
 * @param {Object} item - 学生项
 * @returns {boolean} 是否匹配
 */
function manualQuickRecordFilterMethod(query, item) {
    // 如果查询为空，显示所有项
    if (!query) {
        return true;
    }
    
    // 特殊处理：如果查询是单个"0"，显示所有带前导零的学号（01-09）以及个位数带零的学号（10、20、30等）
    if (query === '0') {
        const studentNum = parseInt(item.studentNumber);
        // 显示1-9号学生（显示为01-09）以及个位数为0的学生（10、20、30等）
        return (studentNum >= 1 && studentNum <= 9) || (studentNum % 10 === 0);
    }
    // 特殊处理：如果查询以"0"开头（如"01", "02"等），精确匹配对应的学号
    else if (query.startsWith('0') && query.length > 1) {
        // 去掉前导零进行比较
        const normalizedQuery = query.replace(/^0+(\d+)$/, '$1');
        if (item.studentNumber === normalizedQuery) {
            return true;
        }
    }
    // 普通学号匹配
    else if (item.studentNumber.includes(query)) {
        return true;
    }
    // 姓名匹配
    else if (item.name.includes(query)) {
        return true;
    }
    // 拼音首字母匹配
    else if (item.pinyinInitials && item.pinyinInitials.toLowerCase().includes(query.toLowerCase())) {
        return true;
    }
    
    return false;
}

/**
 * 移动选中项到右侧（即"到右边"按钮的功能）
 * @param {Object} vm - Vue实例
 */
function moveToRight(vm) {
    if (vm.$refs.manualQuickRecordTransfer) {
        const transfer = vm.$refs.manualQuickRecordTransfer;
        if (transfer && transfer.leftChecked && transfer.leftChecked.length > 0) {
            // 获取当前选中的学生项，保持原有顺序
            const selectedItems = vm.manualQuickRecordData.filter(item => 
                transfer.leftChecked.includes(item.key)
            );
            
            // 创建一个新的数组来存储选中的学生key
            const selectedKeys = selectedItems.map(item => item.key);
            
            // 从现有的已选择列表中移除这些选中的学生
            const remainingKeys = vm.manualQuickRecordSelected.filter(key => 
                !selectedKeys.includes(key)
            );
            
            // 将选中的学生放在顶部（后来居上），其余学生保持原有顺序
            vm.manualQuickRecordSelected = [...selectedKeys, ...remainingKeys];
            
            // 清空左侧面板的选中状态
            transfer.leftChecked.splice(0);
        }
    }
}

/**
 * 移动选中项到左侧（即"到左边"按钮的功能）
 * @param {Object} vm - Vue实例
 */
function moveToLeft(vm) {
    if (vm.$refs.manualQuickRecordTransfer) {
        const transfer = vm.$refs.manualQuickRecordTransfer;
        if (transfer && transfer.rightChecked && transfer.rightChecked.length > 0) {
            // 从已选择列表中移除右侧面板中选中的项，保持"后来居上"逻辑
            transfer.rightChecked.forEach(key => {
                const index = vm.manualQuickRecordSelected.indexOf(key);
                if (index > -1) {
                    vm.manualQuickRecordSelected.splice(index, 1);
                }
            });
            
            // 清空右侧面板的选中状态
            transfer.rightChecked.splice(0);
        }
    }
}

// 导出函数供外部使用
window.Utils = {
    createDatepickerOptions,
    setHighlightedShortcut,
    forceUpdateDatepickerHighlight,
    onDatePickerFocus,
    exportStudentData,
    calculateDateOffset,
    initManualQuickRecord,
    openManualQuickRecord,
    handleManualQuickRecordClose,
    confirmManualQuickRecord,
    manualQuickRecordFilterMethod,
    moveToRight,
    moveToLeft
};