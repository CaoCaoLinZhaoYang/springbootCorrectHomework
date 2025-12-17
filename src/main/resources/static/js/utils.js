// 工具函数文件，包含通用功能

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

// 导出函数供外部使用
window.Utils = {
    exportStudentData
};